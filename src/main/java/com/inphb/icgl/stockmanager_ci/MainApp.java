package com.inphb.icgl.stockmanager_ci;

import com.inphb.icgl.stockmanager_ci.utils.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
  Point d'entrée de l'application StockManager CI.
  Lance le Splash Screen, puis la fenêtre de connexion.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Affiche le Splash Screen en premier
        SplashScreen splash = new SplashScreen();
        Stage loginStage = buildLoginStage();
        splash.showAndProceed(loginStage);
    }

    private Stage buildLoginStage() throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/inphb/icgl/stockmanager_ci/fxml/Login.fxml"));
        Scene scene = new Scene(loader.load(), 420, 500);
        scene.getStylesheets().add(
            getClass().getResource("/com/inphb/icgl/stockmanager_ci/css/styles.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("StockManager CI — Connexion");
        stage.setScene(scene);
        stage.setResizable(false);
        return stage;
    }

    @Override
    public void stop() {
        // Ferme proprement la connexion JDBC à la sortie
        try {
            DatabaseConnection.getInstance().closeConnection();
        } catch (Exception e) {
            System.err.println("[MainApp.stop] " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
