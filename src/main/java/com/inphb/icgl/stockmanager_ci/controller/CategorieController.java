package com.inphb.icgl.stockmanager_ci.controller;

import com.inphb.icgl.stockmanager_ci.dao.CategorieDAO;
import com.inphb.icgl.stockmanager_ci.model.Categorie;
import com.inphb.icgl.stockmanager_ci.repository.ICategorieRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

/*
  Contrôleur CRUD des catégories avec pagination et recherche en temps réel.
 */
public class CategorieController {

    @FXML private Label     lblCategorie;
    // ── TableView ─────────────────────────────────────────────────────────────
    @FXML private TableView<Categorie>          tableCat;
    @FXML private TableColumn<Categorie,Integer> colId;
    @FXML private TableColumn<Categorie,String>  colLibelle;
    @FXML private TableColumn<Categorie,String>  colDescription;

    // ── Formulaire ────────────────────────────────────────────────────────────
    @FXML private TextField txtLibelle;
    @FXML private TextArea  txtDescription;
    @FXML private Button    btnSauvegarder;
    @FXML private Button    btnAnnuler;
    @FXML private Label     lblStatut;

    //  Pagination
    @FXML private Button btnPrecedent;
    @FXML private Button btnSuivant;
    @FXML private Label  lblPage;

    //  Recherche
    @FXML private TextField txtRecherche;


    private final ICategorieRepository repo = new CategorieDAO();
    private Categorie categorieEnEdition = null;
    private int pageCourante = 1;
    private static final int PAGE_SIZE = 15;

    @FXML
    public void initialize() {
        // Gestion de l'icône Fournisseur
        FontIcon iconCategorie = new FontIcon("mdi2f-folder-plus-outline");
        lblCategorie.setGraphic(iconCategorie);
        ((FontIcon) lblCategorie.getGraphic()).setIconSize(30);
        iconCategorie.setIconColor(Color.web("#0C447C"));

        configurerColonnes();
        configurerRecherche();
        chargerPage(1);
        viderFormulaire();
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Clic sur une ligne → remplit le formulaire
        tableCat.getSelectionModel().selectedItemProperty().addListener(
            (obs, ancien, nouveau) -> {
                if (nouveau != null) remplirFormulaire(nouveau);
            });
    }

    private void configurerRecherche() {
        // Recherche en temps réel à chaque frappe
        txtRecherche.textProperty().addListener((obs, ancien, nouveau) -> {
            pageCourante = 1;
            chargerPage(1);
        });
    }

    //  Chargement avec pagination LIMIT/OFFSET

    private void chargerPage(int page) {
        pageCourante = page;
        String motCle = txtRecherche.getText().trim();
        int total;

        if (motCle.isEmpty()) {
            tableCat.setItems(repo.findAll(page, PAGE_SIZE));
            total = repo.countAll();
        } else {
            tableCat.setItems(repo.search(motCle, page, PAGE_SIZE));
            total = repo.countSearch(motCle);
        }

        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        lblPage.setText("Page " + page + " / " + totalPages + "  —  " + total + " catégorie(s)");
        btnPrecedent.setDisable(page <= 1);
        btnSuivant.setDisable(page >= totalPages);
    }

    //  CRUD

    @FXML
    private void handleSauvegarder() {
        String libelle = txtLibelle.getText().trim();
        if (libelle.isEmpty()) {
            lblStatut.setText("Le libellé est obligatoire.");
            return;
        }

        if (categorieEnEdition == null) {
            // INSERTION
            Categorie nouvelle = new Categorie(0, libelle, txtDescription.getText().trim());
            if (repo.save(nouvelle)) {
                lblStatut.setText("Catégorie ajoutée avec succès.");
            } else {
                lblStatut.setText("Erreur lors de l'ajout (libellé déjà existant ?).");
            }
        } else {
            // MISE À JOUR
            categorieEnEdition.setLibelle(libelle);
            categorieEnEdition.setDescription(txtDescription.getText().trim());
            if (repo.update(categorieEnEdition)) {
                lblStatut.setText("Catégorie modifiée avec succès.");
            } else {
                lblStatut.setText("Erreur lors de la modification.");
            }
        }

        viderFormulaire();
        chargerPage(pageCourante);
    }

    @FXML
    private void handleSupprimer() {
        Categorie sel = tableCat.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Veuillez sélectionner une catégorie."); return; }

        // Vérification contrainte d'intégrité
        int nbProduits = repo.countProduitsLies(sel.getId());
        if (nbProduits > 0) {
            lblStatut.setText("Impossible : " + nbProduits + " produit(s) utilisent cette catégorie.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Voulez-vous supprimer la catégorie « " + sel.getLibelle() + " » ?",
            ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                if (repo.delete(sel.getId())) {
                    lblStatut.setText("Catégorie supprimée.");
                    viderFormulaire();
                    chargerPage(pageCourante);
                } else {
                    lblStatut.setText("Erreur lors de la suppression.");
                }
            }
        });
    }

    @FXML
    private void handleAnnuler() {
        viderFormulaire();
        tableCat.getSelectionModel().clearSelection();
    }

    @FXML private void handlePrecedent() { chargerPage(pageCourante - 1); }
    @FXML private void handleSuivant()   { chargerPage(pageCourante + 1); }

    //  Helpers

    private void remplirFormulaire(Categorie c) {
        categorieEnEdition = c;
        txtLibelle.setText(c.getLibelle());
        txtDescription.setText(c.getDescription() != null ? c.getDescription() : "");
        btnSauvegarder.setText("Modifier");
        lblStatut.setText("");
    }

    private void viderFormulaire() {
        categorieEnEdition = null;
        txtLibelle.clear();
        txtDescription.clear();
        btnSauvegarder.setText("Ajouter");
        lblStatut.setText("");
    }
}
