package com.inphb.icgl.stockmanager_ci.controller;

import com.inphb.icgl.stockmanager_ci.dao.UtilisateurDAO;
import com.inphb.icgl.stockmanager_ci.model.Utilisateur;
import com.inphb.icgl.stockmanager_ci.repository.IUtilisateurRepository;
import com.inphb.icgl.stockmanager_ci.utils.SessionManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

//Contrôleur de la fenêtre de connexion.

public class LoginController {

    @FXML private TextField     txtLogin;
    @FXML private PasswordField txtMotDePasse;
    @FXML private Label         lblErreur;
    @FXML private Label         lblTentatives;
    @FXML private Button        btnConnexion;

    private final IUtilisateurRepository utilisateurRepo = new UtilisateurDAO();

    private int     tentatives   = 0;
    private static final int MAX_TENTATIVES  = 3;
    private static final int BLOCAGE_SECONDES = 30;

    @FXML
    public void initialize() {
        lblErreur.setText("");
        lblTentatives.setText("");
        // Permettre la connexion avec la touche Entrée
        txtMotDePasse.setOnAction(e -> handleConnexion());
    }

    @FXML
    private void handleConnexion() {
        String login = txtLogin.getText().trim();
        String mdp   = txtMotDePasse.getText();

        if (login.isEmpty() || mdp.isEmpty()) {
            lblErreur.setText("Veuillez remplir tous les champs.");
            return;
        }

        Utilisateur user = utilisateurRepo.authentifier(login, mdp);

        if (user != null) {
            // Succès
            SessionManager.setUtilisateur(user);
            tentatives = 0;
            ouvrirApplicationPrincipale();
        } else {
            tentatives++;
            int restantes = MAX_TENTATIVES - tentatives;

            if (tentatives >= MAX_TENTATIVES) {
                bloquerCompte();
            } else {
                lblErreur.setText("Identifiants incorrects.");
                lblTentatives.setText(restantes + " tentative(s) restante(s).");
            }
            txtMotDePasse.clear();
        }
    }

    @FXML
    private void handleQuitter() {
        System.exit(0);
    }


    private void bloquerCompte() {
        //Ce code bloque temporairement l'interface de connexion après trop de tentatives échouées
        btnConnexion.setDisable(true);
        txtLogin.setDisable(true);
        txtMotDePasse.setDisable(true);
        lblErreur.setText("Compte bloqué pour " + BLOCAGE_SECONDES + " secondes.");
        lblTentatives.setText("");

        //Après le temps de blocage (30 sec) l'utilisation peut rééssayer encore
        PauseTransition deblocage = new PauseTransition(Duration.seconds(BLOCAGE_SECONDES));
        deblocage.setOnFinished(e -> {
            tentatives = 0;
            btnConnexion.setDisable(false);
            txtLogin.setDisable(false);
            txtMotDePasse.setDisable(false);
            lblErreur.setText("");
            lblTentatives.setText("Vous pouvez réessayer.");
        });
        deblocage.play();
    }

    private void ouvrirApplicationPrincipale() {
        try {
            //Ouverture de la fenêtre qui servira de template aux autres
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/inphb/icgl/stockmanager_ci/fxml/MainLayout.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 750);
            scene.getStylesheets().add(
                getClass().getResource("/com/inphb/icgl/stockmanager_ci/css/styles.css").toExternalForm());

            Stage mainStage = new Stage();
            mainStage.setTitle("StockManager CI — " +
                SessionManager.getUtilisateur().getNomComplet());
            mainStage.setScene(scene);
            mainStage.setMinWidth(900);
            mainStage.setMinHeight(600);
            mainStage.show();

            // Fermer la fenêtre de login
            Stage loginStage = (Stage) txtLogin.getScene().getWindow(); // récupéré AVANT show()
            mainStage.show();
            Platform.runLater(loginStage::close);

        } catch (Exception e) {
            lblErreur.setText("Erreur d'ouverture de l'application : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
