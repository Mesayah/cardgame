package main.java.CardGame.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import main.java.CardGame.CardGame;

/**
 * Created by Mesayah on 31.12.2016.
 */
public class RootLayoutController {

    private CardGame mainApp;

    @FXML
    public void aboutButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Project for programming class");
        alert.setContentText("Made by Filip Piechowski & Łukasz Jaworski");

        alert.showAndWait();
    }

    public void newGameButtonClicked() {
        mainApp.startNewGame();
    }

    public void closeGameButtonClicked() {
        System.exit(0);
    }

    public void setMainApp(CardGame mainApp) {
        this.mainApp = mainApp;
    }

}
