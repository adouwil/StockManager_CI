package com.inphb.icgl.stockmanager_ci.controller;

import com.inphb.icgl.stockmanager_ci.dao.MouvementDAO;
import com.inphb.icgl.stockmanager_ci.dao.ProduitDAO;
import com.inphb.icgl.stockmanager_ci.model.Mouvement;
import com.inphb.icgl.stockmanager_ci.model.Produit;
import com.inphb.icgl.stockmanager_ci.repository.IMouvementRepository;
import com.inphb.icgl.stockmanager_ci.repository.IProduitRepository;
import com.inphb.icgl.stockmanager_ci.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

/*
 - Contrôleur du module Mouvements de stock.
 - Enregistre les entrées/sorties et met à jour quantite_stock via transaction.
 - Pas de modification ni de suppression (traçabilité).
 */
public class MouvementController {

    @FXML private Label     lblMouvements;
    // Formulaire de saisie
    @FXML private ComboBox<Produit>  cbProduit;
    @FXML private ComboBox<String>   cbType;
    @FXML private TextField          txtQuantite;
    @FXML private TextField          txtMotif;
    @FXML private Label              lblStockActuel;
    @FXML private Label              lblStatut;
    @FXML private Button             btnEnregistrer;

    //  Historique
    @FXML private TableView<Mouvement>           tableMvt;
    @FXML private TableColumn<Mouvement,String>  colDate;
    @FXML private TableColumn<Mouvement,String>  colProduit;
    @FXML private TableColumn<Mouvement,String>  colType;
    @FXML private TableColumn<Mouvement,Integer> colQte;
    @FXML private TableColumn<Mouvement,String>  colMotif;
    @FXML private TableColumn<Mouvement,String>  colUser;

    //  Pagination
    @FXML private Button    btnPrecedent;
    @FXML private Button    btnSuivant;
    @FXML private Label     lblPage;
    @FXML private TextField txtRecherche;

    private final IMouvementRepository mouvementRepo = new MouvementDAO();
    private final IProduitRepository   produitRepo   = new ProduitDAO();

    private int pageCourante = 1;
    private static final int PAGE_SIZE = 15;

    @FXML
    public void initialize() {
        // Gestion de l'icône Mouvements
        FontIcon iconMouvements = new FontIcon("mdi2t-transfer");
        lblMouvements.setGraphic(iconMouvements);
        ((FontIcon) lblMouvements.getGraphic()).setIconSize(30);
        iconMouvements.setIconColor(Color.web("#0C447C"));

        configurerColonnes();
        peuplerComboBoxProduits();
        cbType.getItems().addAll("ENTREE", "SORTIE");
        cbType.setValue("ENTREE");

        // Affichage du stock actuel quand on change de produit
        cbProduit.valueProperty().addListener((obs, a, n) -> afficherStockActuel(n));
        txtRecherche.textProperty().addListener((obs, a, n) -> { pageCourante = 1; chargerPage(1); });
        chargerPage(1);
    }

    private void configurerColonnes() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
        colProduit.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeMouvement"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("nomUtilisateur"));

        // Couleur ENTREE = vert, SORTIE = orange
        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) { setText(null); setStyle(""); return; }
                setText(type);
                setStyle("ENTREE".equals(type)
                    ? "-fx-text-fill: #1B5E20; -fx-font-weight: bold;"
                    : "-fx-text-fill: #E65100; -fx-font-weight: bold;");
            }
        });
    }

    private void peuplerComboBoxProduits() {
        // Charge tous les produits (pas paginés — pour la ComboBox)
        cbProduit.setItems(produitRepo.findAll(1, Integer.MAX_VALUE));
    }

    private void afficherStockActuel(Produit p) {
        if (p == null) { lblStockActuel.setText(""); return; }
        String alerte = p.isEnAlerte() ? "ALERTE !!!" : "";
        lblStockActuel.setText("Stock actuel : " + p.getQuantiteStock()
            + " " + p.getUnite() + " | Minimum : " + p.getStockMinimum() + alerte);
        lblStockActuel.setStyle(p.isEnAlerte()
            ? "-fx-text-fill: #C62828; -fx-font-weight: bold;"
            : "-fx-text-fill: #1B5E20;");
    }

    @FXML
    private void handleEnregistrer() {
        Produit produit = cbProduit.getValue();
        String  type    = cbType.getValue();
        String  motif   = txtMotif.getText().trim();

        if (produit == null) { lblStatut.setText("Veuillez sélectionner un produit."); return; }
        if (type == null)    { lblStatut.setText("Veuillez sélectionner le type."); return; }

        int qte;
        try {
            qte = Integer.parseInt(txtQuantite.getText().trim());
            if (qte <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblStatut.setText("Quantité invalide (saisissez un entier > 0).");
            return;
        }

        // Vérification : pas de sortie supérieure au stock disponible
        if ("SORTIE".equals(type) && qte > produit.getQuantiteStock()) {
            lblStatut.setText("Stock insuffisant !!! Le stock disponible est : "
                + produit.getQuantiteStock() + " " + produit.getUnite() + ".");
            return;
        }

        Mouvement m = new Mouvement();
        m.setIdProduit(produit.getId());
        m.setTypeMouvement(type);
        m.setQuantite(qte);
        m.setMotif(motif.isEmpty() ? null : motif);
        m.setIdUtilisateur(SessionManager.getUtilisateur().getId());

        if (mouvementRepo.save(m)) {
            lblStatut.setText("Mouvement enregistré avec succès.");
            txtQuantite.clear();
            txtMotif.clear();
            // Rafraîchit la ComboBox produits pour afficher le nouveau stock
            peuplerComboBoxProduits();
            cbProduit.setValue(null);
            lblStockActuel.setText("");
            chargerPage(1);
        } else {
            lblStatut.setText("Erreur lors de l'enregistrement.");
        }
    }

    private void chargerPage(int page) {
        pageCourante = page;
        String motCle = txtRecherche.getText().trim();
        int total;
        if (motCle.isEmpty()) {
            tableMvt.setItems(mouvementRepo.findAll(page, PAGE_SIZE));
            total = mouvementRepo.countAll();
        } else {
            tableMvt.setItems(mouvementRepo.search(motCle, page, PAGE_SIZE));
            total = mouvementRepo.countSearch(motCle);
        }
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        lblPage.setText("Page " + page + " / " + totalPages + "  —  " + total + " mouvement(s)");
        btnPrecedent.setDisable(page <= 1);
        btnSuivant.setDisable(page >= totalPages);
    }

    @FXML private void handlePrecedent() { chargerPage(pageCourante - 1); }
    @FXML private void handleSuivant()   { chargerPage(pageCourante + 1); }
}
