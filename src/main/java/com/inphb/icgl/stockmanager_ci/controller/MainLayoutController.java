package com.inphb.icgl.stockmanager_ci.controller;

import com.inphb.icgl.stockmanager_ci.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

/*
 - Contrôleur du layout principal (fenêtre avec menu latéral).
 - Charge dynamiquement les vues FXML dans le panneau central.
 - Restreint les menus selon le rôle de l'utilisateur connecté.
 */
public class MainLayoutController {

    @FXML private BorderPane  rootPane;
    @FXML private Label       lblNomUtilisateur;
    @FXML private Label       lblRole;
    @FXML private MenuItem    menuUtilisateurs;   // Admin seulement
    @FXML private MenuItem       menuDashboard;
    @FXML private MenuItem       menuDeconnexion;
    @FXML private MenuItem       menuQuitter;
    @FXML private MenuItem       menuCategories;
    @FXML private MenuItem       menuFournisseurs;
    @FXML private MenuItem       menuProduits;
    @FXML private MenuItem       menuMouvements;
    @FXML private Label       lblAlerteBadge;

    @FXML
    public void initialize() {
        menuDashboard.setGraphic(new FontIcon("mdi2h-home"));
        menuDeconnexion.setGraphic(new FontIcon("mdi2l-logout"));
        menuQuitter.setGraphic(new FontIcon("mdi2c-close-circle-outline"));
        menuCategories.setGraphic(new FontIcon("mdi2f-folder-plus-outline"));
        menuFournisseurs.setGraphic(new FontIcon("mdi2t-truck-delivery-outline"));
        menuProduits.setGraphic(new FontIcon("mdi2p-package-variant"));
        menuMouvements.setGraphic(new FontIcon("mdi2a-arrow-up-down"));
        menuUtilisateurs.setGraphic(new FontIcon("mdi2a-account-group"));
        // Affiche les informations de l'utilisateur connecté
        if (SessionManager.isConnecte()) {
            lblNomUtilisateur.setText(SessionManager.getUtilisateur().getNomComplet());
            lblRole.setText(SessionManager.getUtilisateur().getRole());
        }

        // Restreint le menu Utilisateurs aux ADMIN
        if (!SessionManager.isAdmin()) {
            menuUtilisateurs.setVisible(false);
        }

        // Ouvre le dashboard par défaut
        chargerVue("Dashboard");
    }

    //  Handlers du menu

    @FXML private void ouvrirDashboard()     { chargerVue("Dashboard"); }
    @FXML private void ouvrirCategories()    { chargerVue("Categorie"); }
    @FXML private void ouvrirFournisseurs()  { chargerVue("Fournisseur"); }
    @FXML private void ouvrirProduits()      { chargerVue("Produit"); }
    @FXML private void ouvrirMouvements()    { chargerVue("Mouvement"); }
    @FXML private void ouvrirUtilisateurs()  {
        if (SessionManager.isAdmin()) chargerVue("Utilisateur");
    }

    @FXML
    private void handleDeconnexion() {
        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/inphb/icgl/stockmanager_ci/fxml/Login.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 420, 500);
            scene.getStylesheets().add(
                getClass().getResource("/com/inphb/icgl/stockmanager_ci/css/styles.css").toExternalForm());
            Stage loginStage = new Stage();
            loginStage.setTitle("StockManager CI — Connexion");
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.centerOnScreen();
            loginStage.show();
            ((Stage) rootPane.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuitter() {
        System.exit(0);
    }

    //  Chargement dynamique des vues

    /*
     - Charge la vue FXML correspondante dans le panneau central (CENTER du BorderPane).
     - nom du fichier FXML sans extension (ex: "Produit")
     */
    public void chargerVue(String nomFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/inphb/icgl/stockmanager_ci/fxml/" + nomFxml + ".fxml"));
            Node vue = loader.load();
            rootPane.setCenter(vue);
        } catch (Exception e) {
            System.err.println("[MainLayoutController] Impossible de charger " + nomFxml + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
