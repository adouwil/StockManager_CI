package com.inphb.icgl.stockmanager_ci;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Fenêtre de démarrage animée (Splash Screen).
 * Affichée pendant 3 secondes sans barre de titre.
 * Après disparition, ouvre la fenêtre de connexion.
 */
public class SplashScreen extends Stage {

    public SplashScreen() throws Exception {
        initStyle(StageStyle.UNDECORATED); // Sans barre de titre ni boutons

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/inphb/icgl/stockmanager_ci/fxml/SplashScreen.fxml"));
        Scene scene = new Scene(loader.load(), 520, 320);
        scene.getStylesheets().add(
            getClass().getResource("/com/inphb/icgl/stockmanager_ci/css/styles.css").toExternalForm());

        setScene(scene);
        setResizable(false);

        // Centre l'écran sur le moniteur
        centerOnScreen();
    }

    // Affiche le splash 3 secondes, puis ouvre loginStage.

    public void showAndProceed(Stage loginStage) {
        show();

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            close();
            loginStage.centerOnScreen();
            loginStage.show();
        });
        pause.play();
    }
}
