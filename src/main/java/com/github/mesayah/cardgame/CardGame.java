package com.github.mesayah.cardgame;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class CardGame extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private BattleBoardController battleBoardController;
    private final ObservableList<Card> wholeDeck = FXCollections.observableArrayList();
    private final ObservableList<Card> wholeDeck2 = FXCollections.observableArrayList();
    private Player playerOne;
    private Player playerTwo;
    private TurnPhase turnPhase;
    private int turnCounter = 0;
    private Player currentPlayer;
    private Card selectedCard;
    private boolean isGameStarted;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CardGame");

        initRootLayout();

        loadWholeDeck(getClass().getClassLoader().getResource("cards/deck1.cd").getPath());

        showBattleBoard();
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CardGame.class.getClassLoader().getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            RootLayoutController rootLayoutController = loader.getController();
            rootLayoutController.setMainApp(this);

            Scene scene = new Scene(rootLayout);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWholeDeck(String fileName) {

        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {

            String s;
            wholeDeck.clear();
            wholeDeck2.clear();
            while ((s = in.readLine()) != null) {

                String[] var = s.split(",");

                Card tmp = new Card(Integer.parseInt(var[3]), Integer.parseInt(var[1]), Integer.parseInt(var[2]),
                        var[0], getClass().getClassLoader().getResource("cards/" + var[0] + ".png").toExternalForm());
                wholeDeck.add(tmp);

                tmp = new Card(Integer.parseInt(var[3]), Integer.parseInt(var[1]), Integer.parseInt(var[2]), var[0], getClass().getClassLoader().getResource("cards/" + var[0] + ".png").toExternalForm());
                wholeDeck2.add(tmp);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showBattleBoard() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CardGame.class.getClassLoader().getResource("view/BattleBoard.fxml"));
            AnchorPane battleBoard = loader.load();
            battleBoard.getStylesheets().add(getClass().getClassLoader().getResource("view/style.css").toExternalForm());
            rootLayout.setCenter(battleBoard);

            battleBoardController = loader.getController();
            battleBoardController.setMainApp(this);

            Card.setMainApp(this);
            Player.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startNewGame() {
        loadWholeDeck(getClass().getClassLoader().getResource("cards/deck1.cd").getPath());
        setPlayers();
        battleBoardController.setBinding();
        turnCounter = 0;
        playerOne.setMana(playerOne.getMana() + 1);
        playerTwo.setMana(playerTwo.getMana() + 1);

        battleBoardController.getEndTurnButton().setDisable(false);
        battleBoardController.getEndTurnButton().toFront();

        for (int i = 0; i < 5; i++) {
            playerOne.takeCardFromDeck();
            playerTwo.takeCardFromDeck();
        }

        if (Math.random() < 0.5) {
            setCurrentPlayer(playerOne);
            playerTwo.setMana(playerTwo.getMana() + 1);
        } else {
            setCurrentPlayer(playerTwo);
            playerOne.setMana(playerOne.getMana() + 1);
        }
        showAnnouncement();
        System.out.println("Starting player: " + currentPlayer.getName());

        hideCardsOnHand();

        turnCounter = 1;
        showAnnouncement();
        System.out.println("Turn number " + turnCounter + "Player: " + currentPlayer.getName());

        turnPhase = TurnPhase.ChooseFriendlyCard;


        for (Card x : currentPlayer.getPlayerHand()) {
            if (x.getManaCost() > currentPlayer.getMana()) {
                x.getCardNodes().setOpacity(0.5);
            } else {
                x.getCardNodes().setOpacity(1.0);
            }
        }

        redrawAllCards();

        setGameStarted(true);
    }

    private void setPlayers() {
        TextInputDialog dialog = new TextInputDialog("Player 1");
        dialog.setTitle("First player name");
        dialog.setHeaderText("Name for first player");
        dialog.setContentText("Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> playerOne = new Player(s));
        playerOne.setPlayerDeck(wholeDeck);
        playerOne.setPlayerBoardPane(battleBoardController.getPlayerOneBoardPane());
        playerOne.setPlayerHandPane(battleBoardController.getPlayerOneHandPane());
        playerOne.setPlayerNameLabel(battleBoardController.getPlayerOneNameLabel());

        dialog = new TextInputDialog("Player 2");
        dialog.setTitle("First player name");
        dialog.setHeaderText("Name for second player");
        dialog.setContentText("Name:");
        result = dialog.showAndWait();
        result.ifPresent(s -> playerTwo = new Player(s));
        playerTwo.setPlayerDeck(wholeDeck2);
        playerTwo.setPlayerBoardPane(battleBoardController.getPlayerTwoBoardPane());
        playerTwo.setPlayerHandPane(battleBoardController.getPlayerTwoHandPane());
        playerTwo.setPlayerNameLabel(battleBoardController.getPlayerTwoNameLabel());

        for (Card x : playerOne.getPlayerDeck()) {
            x.setOwner(playerOne);
            x.setPlacement(playerOne.getPlayerDeck());
            //x.setImageURL(getClass().getResource("resources/cards/redCard.png").toExternalForm());
        }

        for (Card x : playerTwo.getPlayerDeck()) {
            x.setOwner(playerTwo);
            x.setPlacement(playerTwo.getPlayerDeck());
            //x.setImageURL(getClass().getResource("resources/cards/blueCard.png").toExternalForm());
        }

        FXCollections.shuffle(playerOne.getPlayerDeck());
        FXCollections.shuffle(playerTwo.getPlayerDeck());
    }

    private void showAnnouncement() {


    }

    private void hideCardsOnHand() {
        Player p, p2;
        if (currentPlayer == playerOne) {
            p = playerTwo;
            p2 = playerOne;
        } else {
            p = playerOne;
            p2 = playerTwo;
        }
        for (Card x : p.getPlayerHand()) {
            x.getCardNodes().setVisible(false);
        }
        for (Card x : p2.getPlayerHand()) {
            x.getCardNodes().setVisible(true);
        }
    }

    public void redrawAllCards() {
        battleBoardController.getPlayerOneBoardPane().getChildren().clear();
        battleBoardController.getPlayerOneHandPane().getChildren().clear();
        battleBoardController.getPlayerTwoBoardPane().getChildren().clear();
        battleBoardController.getPlayerTwoHandPane().getChildren().clear();

        ObservableList<Card> collection = playerOne.getPlayerHand();
        for (int i = 0; i < collection.size(); i++) {
            collection.get(i).calcNodeLayout(i);
            battleBoardController.getPlayerOneHandPane().getChildren().add(collection.get(i).getCardNodes());
        }
        collection = playerOne.getPlayerBoard();
        for (int i = 0; i < collection.size(); i++) {
            collection.get(i).calcNodeLayout(i);
            battleBoardController.getPlayerOneBoardPane().getChildren().add(collection.get(i).getCardNodes());
        }
        collection = playerTwo.getPlayerHand();
        for (int i = 0; i < collection.size(); i++) {
            collection.get(i).calcNodeLayout(i);
            battleBoardController.getPlayerTwoHandPane().getChildren().add(collection.get(i).getCardNodes());
        }
        collection = playerTwo.getPlayerBoard();
        for (int i = 0; i < collection.size(); i++) {
            collection.get(i).calcNodeLayout(i);
            battleBoardController.getPlayerTwoBoardPane().getChildren().add(collection.get(i).getCardNodes());
        }
    }

    public void endTurn() {
        if (isGameStarted()) {
            currentPlayer.setHasMoved(true);
            if (playerOne.isHasMoved() && playerTwo.isHasMoved()) {
                turnCounter++;
                showAnnouncement();
                int manaForTurn;
                if (turnCounter <= 10) manaForTurn = turnCounter;
                else manaForTurn = 10;
                playerOne.setMana(playerOne.getMana() + manaForTurn);
                playerTwo.setMana(playerTwo.getMana() + manaForTurn);
                playerOne.takeCardFromDeck();
                playerTwo.takeCardFromDeck();
                playerOne.setHasMoved(false);
                playerTwo.setHasMoved(false);

            }

            if (currentPlayer == playerOne) {
                setCurrentPlayer(playerTwo);
            } else setCurrentPlayer(playerOne);


            turnPhase = TurnPhase.ChooseFriendlyCard;


            System.out.println("Turn number " + turnCounter + "Player: " + currentPlayer.getName());
            hideCardsOnHand();
            for (Card x : playerOne.getPlayerBoard()) {
                x.setUsed(false);
            }
            for (Card x : playerTwo.getPlayerBoard()) {
                x.setUsed(false);
            }


            for (Card x : currentPlayer.getPlayerHand()) {
                if (x.getManaCost() > currentPlayer.getMana()) {
                    x.getCardNodes().setOpacity(0.5);
                } else {
                    x.getCardNodes().setOpacity(1.0);
                }
            }

            redrawAllCards();

            checkIfWon();
        }

    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    private void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
    }

    public void checkIfWon() {
        if (playerOne.getHealth() == 0 || playerTwo.getHealth() == 0) {
            Player winner;
            if (playerTwo.getHealth() == 0) winner = playerOne;
            else winner = playerTwo;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over!");
            alert.setHeaderText("Game Over!");
            alert.setContentText("The winner is " + winner.getName());

            alert.showAndWait();

            clearBoard();
            setGameStarted(false);
        }
    }

    private void clearBoard() {
        playerOne = null;
        playerTwo = null;
        turnPhase = null;
        turnCounter = 0;
        currentPlayer = null;
        selectedCard = null;
    }

    public void attackPlayer(Player p) {
        if (!selectedCard.isUsed()) {
            if (currentPlayer != p) {
                if (p.getPlayerBoard().size() == 0) {
                    p.setHealth(p.getHealth() - selectedCard.getAttack());
                    getSelectedCard().setUsed(true);
                    setSelectedCard(null);
                    setTurnPhase(TurnPhase.ChooseFriendlyCard);
                }
            }

        } else System.out.println("Card was already used.");
        checkIfWon();
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(Card selectedCard) {
        if (this.selectedCard != null) this.selectedCard.getCardNodes().getChildren().get(0).setEffect(null);
        this.selectedCard = selectedCard;
        if (this.selectedCard != null) {
            int depth = 30;
            DropShadow borderGlow = new DropShadow();
            borderGlow.setOffsetY(0f);
            borderGlow.setOffsetX(0f);
            borderGlow.setColor(Color.RED);
            borderGlow.setWidth(depth);
            borderGlow.setHeight(depth);
            this.selectedCard.getCardNodes().getChildren().get(0).setEffect(borderGlow);
        }
    }

    public TurnPhase getTurnPhase() {
        return turnPhase;

    }

    public void setTurnPhase(TurnPhase turnPhase) {
        this.turnPhase = turnPhase;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    private void setCurrentPlayer(Player currentPlayer) {

        if (this.currentPlayer != null)
            this.currentPlayer.getPlayerNameLabel().setFont(Font.font("System", FontWeight.NORMAL, 21));
        this.currentPlayer = currentPlayer;
        this.currentPlayer.getPlayerNameLabel().setFont(Font.font("System", FontWeight.BOLD, 21));

        battleBoardController.getPlayerOneNameLabel().setEffect(null);
        battleBoardController.getPlayerTwoNameLabel().setEffect(null);
        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.RED);
        borderGlow.setWidth(50);
        borderGlow.setHeight(50);
        if (currentPlayer == playerOne) battleBoardController.getPlayerOneNameLabel().setEffect(borderGlow);
        else battleBoardController.getPlayerTwoNameLabel().setEffect(borderGlow);
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public int getMaxCardsOnHand() {
        return 5;
    }

    public int getMaxCardsOnBoard() {
        return 8;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }
}
