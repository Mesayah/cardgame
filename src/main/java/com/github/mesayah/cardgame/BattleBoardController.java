package com.github.mesayah.cardgame;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

public class BattleBoardController {

    private CardGame mainApp;

    @FXML
    private Button endTurnButton;

    @FXML
    private AnchorPane playerOneBoardPane;

    @FXML
    private AnchorPane playerTwoBoardPane;

    @FXML
    private AnchorPane playerOneHandPane;

    @FXML
    private AnchorPane playerTwoHandPane;

    @FXML
    private ProgressBar playerOneHealthBar;

    @FXML
    private ProgressBar playerTwoHealthBar;

    @FXML
    private ProgressBar playerOneManaBar;

    @FXML
    private ProgressBar playerTwoManaBar;

    @FXML
    private Label playerOneNameLabel;

    @FXML
    private Label playerTwoNameLabel;

    @FXML
    private Label playerOneHealthValueLabel;

    @FXML
    private Label playerOneManaValueLabel;

    @FXML
    private Label playerTwoHealthValueLabel;

    @FXML
    private Label playerTwoManaValueLabel;

    @FXML
    public void endTurnButtonClicked() {
        mainApp.endTurn();
    }

    @FXML
    public void playerOneStatPaneClicked() {
        if (mainApp.isGameStarted() && mainApp.getSelectedCard() != null) mainApp.attackPlayer(mainApp.getPlayerOne());
    }

    @FXML
    public void playerTwoStatPaneClicked() {
        if (mainApp.isGameStarted() && mainApp.getSelectedCard() != null) mainApp.attackPlayer(mainApp.getPlayerTwo());
    }

    public void setBinding() {
        playerOneHealthBar.progressProperty().bind(mainApp.getPlayerOne().healthPercentageProperty());
        playerTwoHealthBar.progressProperty().bind(mainApp.getPlayerTwo().healthPercentageProperty());
        playerOneManaBar.progressProperty().bind(mainApp.getPlayerOne().manaPercentageProperty());
        playerTwoManaBar.progressProperty().bind(mainApp.getPlayerTwo().manaPercentageProperty());
        playerOneNameLabel.textProperty().bind(mainApp.getPlayerOne().nameProperty());
        playerTwoNameLabel.textProperty().bind(mainApp.getPlayerTwo().nameProperty());
        playerOneHealthValueLabel.textProperty().bind(Bindings.format("%d / %d", mainApp.getPlayerOne().healthProperty(), Player.getMaxHealth()));
        playerOneManaValueLabel.textProperty().bind(Bindings.format("%d / %d", mainApp.getPlayerOne().manaProperty(), Player.getMaxMana()));
        playerTwoHealthValueLabel.textProperty().bind(Bindings.format("%d / %d", mainApp.getPlayerTwo().healthProperty(), Player.getMaxHealth()));
        playerTwoManaValueLabel.textProperty().bind(Bindings.format("%d / %d", mainApp.getPlayerTwo().manaProperty(), Player.getMaxMana()));
    }

    public AnchorPane getPlayerOneBoardPane() {
        return playerOneBoardPane;
    }

    public Button getEndTurnButton() {
        return endTurnButton;
    }

    public AnchorPane getPlayerTwoBoardPane() {
        return playerTwoBoardPane;
    }

    public AnchorPane getPlayerOneHandPane() {
        return playerOneHandPane;
    }

    public AnchorPane getPlayerTwoHandPane() {
        return playerTwoHandPane;
    }

    public Label getPlayerOneNameLabel() {
        return playerOneNameLabel;
    }

    public Label getPlayerTwoNameLabel() {
        return playerTwoNameLabel;
    }

    public void setMainApp(CardGame mainApp) {
        this.mainApp = mainApp;
    }

}
