package com.inphb.icgl.stockmanager_ci.controller;

import com.inphb.icgl.stockmanager_ci.dao.UtilisateurDAO;
import com.inphb.icgl.stockmanager_ci.model.Utilisateur;
import com.inphb.icgl.stockmanager_ci.repository.IUtilisateurRepository;
import com.inphb.icgl.stockmanager_ci.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

/*
  Contrôleur du module Utilisateurs — réservé aux ADMIN.
  Un ADMIN ne peut pas se supprimer lui-même.
 */
public class UtilisateurController {

    @FXML private TableView<Utilisateur>           tableUsr;
    @FXML private TableColumn<Utilisateur,Integer> colId;
    @FXML private TableColumn<Utilisateur,String>  colNom;
    @FXML private TableColumn<Utilisateur,String>  colLogin;
    @FXML private TableColumn<Utilisateur,String>  colRole;
    @FXML private TableColumn<Utilisateur,String>  colStatut;

    @FXML private TextField     txtNomComplet;
    @FXML private TextField     txtLogin;
    @FXML private PasswordField txtMotDePasse;
    @FXML private Label         lblMdpInfo;
    @FXML private ComboBox<String> cbRole;
    @FXML private CheckBox      chkActif;
    @FXML private Button        btnSauvegarder;
    @FXML private Label         lblStatut;
    @FXML private Label         lblUtilisateurs;

    @FXML private Button    btnPrecedent;
    @FXML private Button    btnSuivant;
    @FXML private Label     lblPage;
    @FXML private TextField txtRecherche;

    private final IUtilisateurRepository repo = new UtilisateurDAO();
    private Utilisateur utilisateurEnEdition = null;
    private int pageCourante = 1;
    private static final int PAGE_SIZE = 15;

    @FXML
    public void initialize() {
        // Gestion de l'icône Utilisateur
        FontIcon iconUtilisateur = new FontIcon("mdi2a-account-group");
        lblUtilisateurs.setGraphic(iconUtilisateur);
        ((FontIcon) lblUtilisateurs.getGraphic()).setIconSize(30);
        iconUtilisateur.setIconColor(Color.web("#0C447C"));

        // Vérification du rôle — sécurité côté vue
        if (!SessionManager.isAdmin()) {
            lblStatut.setText("Accès refusé — Module réservé aux administrateurs.");
            return;
        }
        // Combobox (liste déroulante avec les valeurs GESTIONNAIRE ET ADMIN - valeur par défaut GESTIONNAIRE
        cbRole.getItems().addAll("ADMIN", "GESTIONNAIRE");
        cbRole.setValue("GESTIONNAIRE");
        // Checkbox initialisé a true
        chkActif.setSelected(true);

        //Affichage des utilisateurs actuels
        configurerColonnes();
        txtRecherche.textProperty().addListener((obs, a, n) -> { pageCourante = 1; chargerPage(1); });
        chargerPage(1);
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statutLabel"));

        // Couleur selon rôle
        colRole.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) { setText(null); setStyle(""); return; }
                setText(role);
                setStyle("ADMIN".equals(role)
                    ? "-fx-text-fill: #1565C0; -fx-font-weight: bold;"
                    : "-fx-text-fill: #2E7D32;");
            }
        });

        tableUsr.getSelectionModel().selectedItemProperty().addListener(
            (obs, a, n) -> { if (n != null) remplirFormulaire(n); });
    }

    private void chargerPage(int page) {
        pageCourante = page;
        String motCle = txtRecherche.getText().trim();
        int total;
        if (motCle.isEmpty()) {
            tableUsr.setItems(repo.findAll(page, PAGE_SIZE));
            total = repo.countAll();
        } else {
            tableUsr.setItems(repo.search(motCle, page, PAGE_SIZE));
            total = repo.countSearch(motCle);
        }
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        lblPage.setText("Page " + page + " / " + totalPages + "  —  " + total + " utilisateur(s)");
        btnPrecedent.setDisable(page <= 1);
        btnSuivant.setDisable(page >= totalPages);
    }

    @FXML
    private void handleSauvegarder() {
        String nom   = txtNomComplet.getText().trim();
        String login = txtLogin.getText().trim();
        String mdp   = txtMotDePasse.getText();

        if (nom.isEmpty() || login.isEmpty()) {
            lblStatut.setText("Le nom et le login sont obligatoires."); return;
        }
        if (utilisateurEnEdition == null && mdp.isEmpty()) {
            lblStatut.setText("Le mot de passe est obligatoire pour un nouvel utilisateur."); return;
        }

        Utilisateur u = (utilisateurEnEdition != null) ? utilisateurEnEdition : new Utilisateur();
        u.setNomComplet(nom);
        u.setLogin(login);
        u.setMotDePasse(mdp); // Le DAO se charge du hashage
        u.setRole(cbRole.getValue());
        u.setActif(chkActif.isSelected());

        boolean ok = (utilisateurEnEdition == null) ? repo.save(u) : repo.update(u);
        lblStatut.setText(ok
            ? "Utilisateur " + (utilisateurEnEdition == null ? "créé" : "modifié") + "."
            : "Erreur (login déjà existant ?).");
        viderFormulaire();
        chargerPage(pageCourante);
    }

    @FXML
    private void handleSupprimer() {
        Utilisateur sel = tableUsr.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Sélectionnez un utilisateur."); return; }

        // Interdit de se supprimer soi-même
        if (sel.getId() == SessionManager.getUtilisateur().getId()) {
            lblStatut.setText("Vous ne pouvez pas supprimer votre propre compte.");
            return;
        }

        new Alert(Alert.AlertType.CONFIRMATION,
            "Supprimer l'utilisateur « " + sel.getNomComplet() + " » ?",
            ButtonType.YES, ButtonType.NO)
            .showAndWait()
            .filter(b -> b == ButtonType.YES)
            .ifPresent(b -> {
                if (repo.delete(sel.getId())) {
                    lblStatut.setText("Utilisateur supprimé avec succès.");
                    viderFormulaire();
                    chargerPage(pageCourante);
                }
            });
    }

    @FXML private void handleAnnuler()   { viderFormulaire(); tableUsr.getSelectionModel().clearSelection(); }
    @FXML private void handlePrecedent() { chargerPage(pageCourante - 1); }
    @FXML private void handleSuivant()   { chargerPage(pageCourante + 1); }

    private void remplirFormulaire(Utilisateur u) {
        utilisateurEnEdition = u;
        txtNomComplet.setText(u.getNomComplet());
        txtLogin.setText(u.getLogin());
        txtMotDePasse.clear();
        lblMdpInfo.setText("(laisser vide = conserver l'actuel)");
        cbRole.setValue(u.getRole());
        chkActif.setSelected(u.isActif());
        btnSauvegarder.setText("Modifier");
        lblStatut.setText("");
    }

    private void viderFormulaire() {
        utilisateurEnEdition = null;
        txtNomComplet.clear(); txtLogin.clear(); txtMotDePasse.clear();
        lblMdpInfo.setText("");
        cbRole.setValue("GESTIONNAIRE");
        chkActif.setSelected(true);
        btnSauvegarder.setText("Créer");
        lblStatut.setText("");
    }
}
