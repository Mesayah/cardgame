package main.java.CardGame;/**
 * Created by Mesayah on 26.12.2016.
 */

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
import main.java.CardGame.model.Card;
import main.java.CardGame.model.Player;
import main.java.CardGame.model.TurnPhase;
import main.java.CardGame.view.BattleBoardController;
import main.java.CardGame.view.RootLayoutController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class CardGame extends Application {

    private final int maxCardsOnHand = 5;
    private final int maxCardsOnBoard = 8;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private AnchorPane battleBoard;
    private BattleBoardController controller;
    private RootLayoutController rootcontrl;
    private ObservableList<Card> wholeDeck = FXCollections.observableArrayList();
    private ObservableList<Card> wholeDeck2 = FXCollections.observableArrayList();
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

        loadWholeDeck(getClass().getResource("resources/cards/deck1.cd").getPath());

        showBattleBoard();


    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CardGame.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            rootcontrl = loader.getController();
            rootcontrl.setMainApp(this);

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

                Card tmp = new Card(Integer.parseInt(var[3]), Integer.parseInt(var[1]), Integer.parseInt(var[2]), var[0], getClass().getResource("resources/cards/" + var[0] + ".png").toExternalForm());
                wholeDeck.add(tmp);

                tmp = new Card(Integer.parseInt(var[3]), Integer.parseInt(var[1]), Integer.parseInt(var[2]), var[0], getClass().getResource("resources/cards/" + var[0] + ".png").toExternalForm());
                wholeDeck2.add(tmp);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showBattleBoard() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CardGame.class.getResource("view/BattleBoard.fxml"));
            battleBoard = loader.load();
            battleBoard.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());
            rootLayout.setCenter(battleBoard);

            controller = loader.getController();
            controller.setMainApp(this);

            Card.setMainApp(this);
            Player.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startNewGame() {
        loadWholeDeck(getClass().getResource("resources/cards/deck1.cd").getPath());
        setPlayers();
        controller.setBinding();
        turnCounter = 0;
        playerOne.setMana(playerOne.getMana() + 1);
        playerTwo.setMana(playerTwo.getMana() + 1);

        controller.getEndTurnButton().setDisable(false);
        controller.getEndTurnButton().toFront();

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
        showAnnouncement(currentPlayer.getName() + " starts the game.");
        System.out.println("Starting player: " + currentPlayer.getName());

        hideCardsOnHand();

        turnCounter = 1;
        showAnnouncement(currentPlayer.getName() + " starts the game.");
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
        if (result.isPresent()) {
            playerOne = new Player(result.get());
        }
        playerOne.setPlayerDeck(wholeDeck);                                               // kopiowanie talli do talli graczy
        playerOne.setPlayerBoardPane(controller.getPlayerOneBoardPane());
        playerOne.setPlayerHandPane(controller.getPlayerOneHandPane());
        playerOne.setPlayerNameLabel(controller.getPlayerOneNameLabel());

        dialog = new TextInputDialog("Player 2");
        dialog.setTitle("First player name");
        dialog.setHeaderText("Name for second player");
        dialog.setContentText("Name:");
        result = dialog.showAndWait();
        if (result.isPresent()) {
            playerTwo = new Player(result.get());
        }
        playerTwo = new Player(result.get());
        playerTwo.setPlayerDeck(wholeDeck2);
        playerTwo.setPlayerBoardPane(controller.getPlayerTwoBoardPane());
        playerTwo.setPlayerHandPane(controller.getPlayerTwoHandPane());
        playerTwo.setPlayerNameLabel(controller.getPlayerTwoNameLabel());

        for (Card x : playerOne.getPlayerDeck()) {
            x.setOwnership(playerOne);
            x.setPlacement(playerOne.getPlayerDeck());
            //x.setImageURL(getClass().getResource("resources/cards/redCard.png").toExternalForm());
        }

        for (Card x : playerTwo.getPlayerDeck()) {
            x.setOwnership(playerTwo);
            x.setPlacement(playerTwo.getPlayerDeck());
            //x.setImageURL(getClass().getResource("resources/cards/blueCard.png").toExternalForm());
        }

        FXCollections.shuffle(playerOne.getPlayerDeck());
        FXCollections.shuffle(playerTwo.getPlayerDeck());
    }

    public void showAnnouncement(String announcement) {


    }

    public void hideCardsOnHand() {
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
        controller.getPlayerOneBoardPane().getChildren().clear();
        controller.getPlayerOneHandPane().getChildren().clear();
        controller.getPlayerTwoBoardPane().getChildren().clear();
        controller.getPlayerTwoHandPane().getChildren().clear();

        ObservableList<Card> collection = playerOne.getPlayerHand();
        for (int i = 0; i < collection.size(); i++) {
            collection.get(i).calcNodeLayout(i);
            controller.getPlayerOneHandPane().getChildren().add(collection.get(i).getCardNodes());
        }
        collection = playerOne.getPlayerBoard();
        for (int i = 0; i < collection.size(); i++) {
            collection.get(i).calcNodeLayout(i);
            controller.getPlayerOneBoardPane().getChildren().add(collection.get(i).getCardNodes());
        }
        collection = playerTwo.getPlayerHand();
        for (int i = 0; i < collection.size(); i++) {
            collection.get(i).calcNodeLayout(i);
            controller.getPlayerTwoHandPane().getChildren().add(collection.get(i).getCardNodes());
        }
        collection = playerTwo.getPlayerBoard();
        for (int i = 0; i < collection.size(); i++) {
            collection.get(i).calcNodeLayout(i);
            controller.getPlayerTwoBoardPane().getChildren().add(collection.get(i).getCardNodes());
        }
    }

    public void endTurn() {
        if (isGameStarted()) {
            currentPlayer.setHasMoved(true);
            if (playerOne.isHasMoved() && playerTwo.isHasMoved()) {
                turnCounter++;
                showAnnouncement("Turn number " + turnCounter);
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

    public void setGameStarted(boolean gameStarted) {
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

    public void clearBoard() {
        playerOne = null;
        playerTwo = null;
        turnPhase = null;
        turnCounter = 0;
        currentPlayer = null;
        selectedCard = null;
    }

    public void attackPlayer(Player p) {
        if (selectedCard.isUsed() == false) {
            if (currentPlayer != p) {
                if (p.getPlayerBoard().size() == 0) {
                    p.setHealth(p.getHealth() - selectedCard.getAttack());
                    getSelectedCard().setUsed(true);
                    setSelectedCard(null);
                    setTurnPhase(TurnPhase.ChooseFriendlyCard);
                }
            }

        } else System.out.println("Card was laready used.");
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

    public void setCurrentPlayer(Player currentPlayer) {

        if (this.currentPlayer != null)
            this.currentPlayer.getPlayerNameLabel().setFont(Font.font("System", FontWeight.NORMAL, 21));
        this.currentPlayer = currentPlayer;
        this.currentPlayer.getPlayerNameLabel().setFont(Font.font("System", FontWeight.BOLD, 21));

        controller.getPlayerOneNameLabel().setEffect(null);
        controller.getPlayerTwoNameLabel().setEffect(null);
        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.RED);
        borderGlow.setWidth(50);
        borderGlow.setHeight(50);
        if (currentPlayer == playerOne) controller.getPlayerOneNameLabel().setEffect(borderGlow);
        else controller.getPlayerTwoNameLabel().setEffect(borderGlow);
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public int getMaxCardsOnHand() {
        return maxCardsOnHand;
    }

    public int getMaxCardsOnBoard() {
        return maxCardsOnBoard;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }
}
