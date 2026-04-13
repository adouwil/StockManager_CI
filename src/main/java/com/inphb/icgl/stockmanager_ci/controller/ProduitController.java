package com.inphb.icgl.stockmanager_ci.controller;

import com.inphb.icgl.stockmanager_ci.dao.CategorieDAO;
import com.inphb.icgl.stockmanager_ci.dao.FournisseurDAO;
import com.inphb.icgl.stockmanager_ci.dao.ProduitDAO;
import com.inphb.icgl.stockmanager_ci.model.Categorie;
import com.inphb.icgl.stockmanager_ci.model.Fournisseur;
import com.inphb.icgl.stockmanager_ci.model.Produit;
import com.inphb.icgl.stockmanager_ci.repository.*;
import com.inphb.icgl.stockmanager_ci.utils.ExportUtil;
//import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/*
 - Contrôleur du module Produits.
 - Fonctionnalités : CRUD, alerte visuelle rouge (RowFactory), pagination,
 - recherche multicritères, export XLSX via Apache POI.
 */
public class ProduitController {

    @FXML private Label      lblProduits;
    //  TableView
    @FXML private TableView<Produit>           tableProduits;
    @FXML private TableColumn<Produit,String>  colRef;
    @FXML private TableColumn<Produit,String>  colDesignation;
    @FXML private TableColumn<Produit,String>  colCategorie;
    @FXML private TableColumn<Produit,String>  colFournisseur;
    @FXML private TableColumn<Produit,Double>  colPrix;
    @FXML private TableColumn<Produit,Integer> colQte;
    @FXML private TableColumn<Produit,Integer> colMin;
    @FXML private TableColumn<Produit,String>  colUnite;

    //  Formulaire
    @FXML private TextField     txtReference;
    @FXML private TextField     txtDesignation;
    @FXML private ComboBox<Categorie>    cbCategorie;
    @FXML private ComboBox<Fournisseur>  cbFournisseur;
    @FXML private TextField     txtPrix;
    @FXML private TextField     txtQuantite;
    @FXML private TextField     txtStockMin;
    @FXML private ComboBox<String> cbUnite;
    @FXML private Button        btnSauvegarder;
    @FXML private Label         lblStatut;
    @FXML private Label         lblAlerte;

    //  Pagination
    @FXML private Button    btnPrecedent;
    @FXML private Button    btnSuivant;
    @FXML private Label     lblPage;
    @FXML private TextField txtRecherche;

    private final IProduitRepository    produitRepo    = new ProduitDAO();
    private final ICategorieRepository  categorieRepo  = new CategorieDAO();
    private final IFournisseurRepository fournisseurRepo = new FournisseurDAO();

    private Produit produitEnEdition = null;
    private int pageCourante = 1;
    private static final int PAGE_SIZE = 15;

    private static final NumberFormat FMT_FCFA = NumberFormat.getNumberInstance(Locale.FRANCE);

    @FXML
    public void initialize() {
        // Gestion de l'icône Produits
        FontIcon iconProduits = new FontIcon("mdi2p-package-variant");
        lblProduits.setGraphic(iconProduits);
        ((FontIcon) lblProduits.getGraphic()).setIconSize(30);
        iconProduits.setIconColor(Color.web("#0C447C"));



        //Afficher la liste des produits en stock
        configurerColonnes();
        configurerAlerteVisuelle();
        peuplerComboBox();
        txtRecherche.textProperty().addListener((obs, a, n) -> { pageCourante = 1; chargerPage(1); });
        chargerPage(1);
        viderFormulaire();
    }

    private void configurerColonnes() {
        colRef.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("nomCategorie"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("nomFournisseur"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimum"));
        colUnite.setCellValueFactory(new PropertyValueFactory<>("unite"));

        // Formatage du prix en FCFA
        colPrix.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double prix, boolean empty) {
                super.updateItem(prix, empty);
                setText(empty || prix == null ? null : FMT_FCFA.format(prix.longValue()) + " F");
            }
        });

        tableProduits.getSelectionModel().selectedItemProperty().addListener(
            (obs, a, n) -> { if (n != null) remplirFormulaire(n); });
    }

    /*
      RowFactory : colore en rouge les lignes dont stock inférieur ou égal stock_minimum.
      C'est l'alerte visuelle obligatoire du cahier des charges.
     */
    private void configurerAlerteVisuelle() {
        tableProduits.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                if (p == null || empty) {
                    setStyle("");
                } else if (p.isEnAlerte()) {
                    setStyle("-fx-background-color: #FFCDD2;"); // Rouge clair
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void peuplerComboBox() {
        cbCategorie.setItems(categorieRepo.findAllSansPagination());
        cbFournisseur.setItems(fournisseurRepo.findAllSansPagination());
        cbUnite.getItems().addAll(
            "pièce","kg","litre","sac","carton","boîte",
            "bidon","rouleau","pack","ramette","flacon","mètre"
        );
        cbUnite.setValue("pièce");
    }

    private void chargerPage(int page) {
        pageCourante = page;
        String motCle = txtRecherche.getText().trim();
        int total;
        if (motCle.isEmpty()) {
            tableProduits.setItems(produitRepo.findAll(page, PAGE_SIZE));
            total = produitRepo.countAll();
        } else {
            tableProduits.setItems(produitRepo.search(motCle, page, PAGE_SIZE));
            total = produitRepo.countSearch(motCle);
        }
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        lblPage.setText("Page " + page + " / " + totalPages + "  —  " + total + " produit(s)");
        btnPrecedent.setDisable(page <= 1);
        btnSuivant.setDisable(page >= totalPages);

        // Mise à jour du compteur d'alertes
        int nbAlertes = produitRepo.countEnAlerte();
        lblAlerte.setText(nbAlertes > 0 ? "ATTENTION !!! " + nbAlertes + " produit(s) en alerte de stock !" : "");
        lblAlerte.setStyle(nbAlertes > 0 ? "-fx-text-fill: #C62828; -fx-font-weight: bold;" : "");
    }

    // les methodes CRUD
    @FXML
    private void handleSauvegarder() {
        try {
            String ref   = txtReference.getText().trim();
            String desig = txtDesignation.getText().trim();
            if (ref.isEmpty() || desig.isEmpty()) {
                lblStatut.setText("La référence et la désignation sont obligatoires."); return;
            }
            double prix = Double.parseDouble(txtPrix.getText().replace(",", ".").trim());
            int qte     = Integer.parseInt(txtQuantite.getText().trim());
            int min     = Integer.parseInt(txtStockMin.getText().trim());

            Produit p = (produitEnEdition != null) ? produitEnEdition : new Produit();
            p.setReference(ref);
            p.setDesignation(desig);
            p.setIdCategorie(cbCategorie.getValue() != null ? cbCategorie.getValue().getId() : 0);
            p.setIdFournisseur(cbFournisseur.getValue() != null ? cbFournisseur.getValue().getId() : 0);
            p.setPrixUnitaire(prix);
            p.setQuantiteStock(qte);
            p.setStockMinimum(min);
            p.setUnite(cbUnite.getValue() != null ? cbUnite.getValue() : "pièce");

            boolean ok = (produitEnEdition == null) ? produitRepo.save(p) : produitRepo.update(p);
            lblStatut.setText(ok ? "Produit " + (produitEnEdition == null ? "ajouté avec succès" : "modifié avec succès") + "."
                                 : "Erreur !!! (référence déjà existante ?).");
            viderFormulaire();
            chargerPage(pageCourante);

        } catch (NumberFormatException e) {
            lblStatut.setText("Les prix, quantité et stock minimum doivent être des nombres.");
        }
    }

    @FXML
    private void handleSupprimer() {
        Produit sel = tableProduits.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Sélectionnez un produit."); return; }

        new Alert(Alert.AlertType.CONFIRMATION,
            "Supprimer « " + sel.getDesignation() + " » ?\n" +
            "Les mouvements associés seront également supprimés.",
            ButtonType.YES, ButtonType.NO)
            .showAndWait()
            .filter(b -> b == ButtonType.YES)
            .ifPresent(b -> {
                if (produitRepo.delete(sel.getId())) {
                    lblStatut.setText("Le produit est supprimé.");
                    viderFormulaire();
                    chargerPage(pageCourante);
                }
            });
    }

    /*
      Export XLSX : récupère TOUS les produits (sans pagination),
      les exporte via Apache POI, propose un FileChooser pour le chemin.
     */
    @FXML
    private void handleExporterXLSX() {
        FileChooser filechoose = new FileChooser();
        filechoose.setTitle("Enregistrer le fichier Excel");
        LocalDateTime mtn = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String dateFormatee = mtn.format(dtf);

        filechoose.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichier Excel (*.xlsx)", "*.xlsx"));
        filechoose.setInitialFileName("produits_stock"+ dateFormatee +".xlsx");

        File fichier = filechoose.showSaveDialog(tableProduits.getScene().getWindow());
        if (fichier == null) return;

        try {
            // Charger tous les produits (toutes pages)
            List<Produit> tous = produitRepo.findAll(1, Integer.MAX_VALUE);
            ExportUtil.exporterXLSX(tous, fichier);
           // lblStatut.setText(" Export réussi : " + fichier.getAbsolutePath());
            new Alert(Alert.AlertType.INFORMATION,
                "Export terminé !\n" + fichier.getAbsolutePath(),
                ButtonType.OK).showAndWait();
        } catch (Exception e) {
            lblStatut.setText("✘ Erreur lors de l'export : " + e.getMessage());
        }
    }

    @FXML private void handleAnnuler()   { viderFormulaire(); tableProduits.getSelectionModel().clearSelection(); }
    @FXML private void handlePrecedent() { chargerPage(pageCourante - 1); }
    @FXML private void handleSuivant()   { chargerPage(pageCourante + 1); }

    private void remplirFormulaire(Produit p) {
        produitEnEdition = p;
        txtReference.setText(p.getReference());
        txtDesignation.setText(p.getDesignation());
        txtPrix.setText(String.valueOf(p.getPrixUnitaire()));
        txtQuantite.setText(String.valueOf(p.getQuantiteStock()));
        txtStockMin.setText(String.valueOf(p.getStockMinimum()));
        cbUnite.setValue(p.getUnite());

        // Sélectionne la catégorie dans la ComboBox
        cbCategorie.getItems().stream()
            .filter(c -> c.getId() == p.getIdCategorie())
            .findFirst().ifPresent(cbCategorie::setValue);

        // Sélectionne le fournisseur dans la ComboBox
        cbFournisseur.getItems().stream()
            .filter(f -> f.getId() == p.getIdFournisseur())
            .findFirst().ifPresent(cbFournisseur::setValue);

        btnSauvegarder.setText("Modifier");
        lblStatut.setText("");
    }

    private void viderFormulaire() {
        produitEnEdition = null;
        txtReference.clear(); txtDesignation.clear();
        txtPrix.clear(); txtQuantite.clear(); txtStockMin.clear();
        cbCategorie.setValue(null); cbFournisseur.setValue(null);
        cbUnite.setValue("pièce");
        btnSauvegarder.setText("Ajouter");
        lblStatut.setText("");
    }
}
