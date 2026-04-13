package com.inphb.icgl.stockmanager_ci.controller;

import com.inphb.icgl.stockmanager_ci.dao.FournisseurDAO;
import com.inphb.icgl.stockmanager_ci.model.Fournisseur;
import com.inphb.icgl.stockmanager_ci.repository.IFournisseurRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

/*
  Contrôleur CRUD des fournisseurs avec pagination et recherche.
 */
public class FournisseurController {

    @FXML private TableView<Fournisseur>          tableFour;
    @FXML private TableColumn<Fournisseur,Integer> colId;
    @FXML private TableColumn<Fournisseur,String>  colNom;
    @FXML private TableColumn<Fournisseur,String>  colTel;
    @FXML private TableColumn<Fournisseur,String>  colEmail;
    @FXML private TableColumn<Fournisseur,String>  colVille;

    @FXML private TextField txtNom;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtVille;
    @FXML private Button    btnSauvegarder;
    @FXML private Label     lblStatut;

    @FXML private Button    btnPrecedent;
    @FXML private Button    btnSuivant;
    @FXML private Label     lblPage;
    @FXML private TextField txtRecherche;
    @FXML private Label     lblFournisseurs;

    private final IFournisseurRepository repo = new FournisseurDAO();
    private Fournisseur fournisseurEnEdition = null;
    private int pageCourante = 1;
    private static final int PAGE_SIZE = 15;

    @FXML
    public void initialize() {
        // Gestion de l'icône Fournisseur
       FontIcon iconFournisseur = new FontIcon("mdi2t-truck-delivery-outline");
        lblFournisseurs.setGraphic(iconFournisseur);
        ((FontIcon) lblFournisseurs.getGraphic()).setIconSize(30);
        iconFournisseur.setIconColor(Color.web("#0C447C"));

        configurerColonnes();
        txtRecherche.textProperty().addListener((obs, a, n) -> { pageCourante = 1; chargerPage(1); });
        chargerPage(1);
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));

        tableFour.getSelectionModel().selectedItemProperty().addListener(
            (obs, a, n) -> { if (n != null) remplirFormulaire(n); });
    }
    //gestion de la pagination
    private void chargerPage(int page) {
        pageCourante = page;
        String motCle = txtRecherche.getText().trim();
        int total;
        if (motCle.isEmpty()) {
            tableFour.setItems(repo.findAll(page, PAGE_SIZE));
            total = repo.countAll();
        } else {
            tableFour.setItems(repo.search(motCle, page, PAGE_SIZE));
            total = repo.countSearch(motCle);
        }
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        lblPage.setText("Page " + page + " / " + totalPages + "  —  " + total + " fournisseur(s)");
        btnPrecedent.setDisable(page <= 1);
        btnSuivant.setDisable(page >= totalPages);
    }
    // Gestion des Methodes CRUD
    @FXML
    private void handleSauvegarder() {
        String nom = txtNom.getText().trim();
        if (nom.isEmpty()) { lblStatut.setText("Le nom est obligatoire."); return; }

        Fournisseur f = (fournisseurEnEdition != null) ? fournisseurEnEdition : new Fournisseur();
        f.setNom(nom);
        f.setTelephone(txtTelephone.getText().trim());
        f.setEmail(txtEmail.getText().trim());
        f.setAdresse(txtAdresse.getText().trim());
        f.setVille(txtVille.getText().trim());

        boolean ok = (fournisseurEnEdition == null) ? repo.save(f) : repo.update(f);
        lblStatut.setText(ok ? "Fournisseur " + (fournisseurEnEdition == null ? "ajouté avec succès" : "modifié avec succès") + "."
                              : "Erreur lors de l'opération.");
        viderFormulaire();
        chargerPage(pageCourante);
    }

    @FXML
    private void handleSupprimer() {
        Fournisseur sel = tableFour.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("veuillez sélectionner un fournisseur."); return; }

        int nbProd = repo.countProduitsLies(sel.getId());
        if (nbProd > 0) {
            lblStatut.setText("Impossible !!! Il y a " + nbProd + " produit(s) liés à ce fournisseur");
            return;
        }
        new Alert(Alert.AlertType.CONFIRMATION,
            "Voulez-vous Supprimer « " + sel.getNom() + " » ?", ButtonType.YES, ButtonType.NO)
            .showAndWait()
            .filter(b -> b == ButtonType.YES)
            .ifPresent(b -> {
                if (repo.delete(sel.getId())) {
                    lblStatut.setText("Fournisseur supprimé.");
                    viderFormulaire();
                    chargerPage(pageCourante);
                }
            });
    }

    @FXML private void handleAnnuler()   { viderFormulaire(); tableFour.getSelectionModel().clearSelection(); }
    @FXML private void handlePrecedent() { chargerPage(pageCourante - 1); }
    @FXML private void handleSuivant()   { chargerPage(pageCourante + 1); }

    private void remplirFormulaire(Fournisseur f) {
        fournisseurEnEdition = f;
        txtNom.setText(f.getNom());
        txtTelephone.setText(f.getTelephone() != null ? f.getTelephone() : "");
        txtEmail.setText(f.getEmail() != null ? f.getEmail() : "");
        txtAdresse.setText(f.getAdresse() != null ? f.getAdresse() : "");
        txtVille.setText(f.getVille() != null ? f.getVille() : "");
        btnSauvegarder.setText("Modifier");
        lblStatut.setText("");
    }

    private void viderFormulaire() {
        fournisseurEnEdition = null;
        txtNom.clear(); txtTelephone.clear(); txtEmail.clear();
        txtAdresse.clear(); txtVille.clear();
        btnSauvegarder.setText("Ajouter");
        lblStatut.setText("");
    }
}
