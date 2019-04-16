package com.github.mesayah.cardgame;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class Player {

    private static final int maxHealth = 20;
    private static final int maxMana = 15;
    private static CardGame mainApp;
    private final StringProperty name;
    private Label playerNameLabel;
    private ObservableList<Card> playerDeck;
    private final ObservableList<Card> playerHand = FXCollections.observableArrayList();
    private final ObservableList<Card> playerBoard = FXCollections.observableArrayList();
    private final DoubleProperty healthPercentage;
    private final DoubleProperty manaPercentage;
    private final IntegerProperty health;
    private final IntegerProperty mana;
    private boolean hasMoved;
    private int damageCounter = 0;
    private final IntegerProperty cardsLeftCounter;

    public Player(String name) {
        this.name = new SimpleStringProperty(name);
        this.health = new SimpleIntegerProperty(maxHealth);
        this.healthPercentage = new SimpleDoubleProperty(1);
        this.mana = new SimpleIntegerProperty(0);
        this.manaPercentage = new SimpleDoubleProperty(0 / maxMana);
        cardsLeftCounter = new SimpleIntegerProperty(0);
    }

    public Player(String name, ObservableList<Card> deck) {
        this.name = new SimpleStringProperty(name);
        this.health = new SimpleIntegerProperty(maxHealth);
        this.healthPercentage = new SimpleDoubleProperty(1);
        this.mana = new SimpleIntegerProperty(0);
        this.manaPercentage = new SimpleDoubleProperty(0 / maxMana);
        this.playerDeck = deck;
        cardsLeftCounter = new SimpleIntegerProperty(deck.size());
    }

    public static void setMainApp(CardGame mainApp) {
        Player.mainApp = mainApp;
    }

    public static int getMaxHealth() {
        return maxHealth;
    }

    public static int getMaxMana() {
        return maxMana;
    }

    public void takeCardFromDeck() {
        if (playerDeck.size() == 0) {
            setHealth(this.getHealth() - ++damageCounter);
        } else {
            if (playerHand.size() < mainApp.getMaxCardsOnHand()) {
                int deckPeakIndex = playerDeck.size() - 1;
                Card tmp = playerDeck.get(deckPeakIndex);
                playerDeck.remove(deckPeakIndex);
                playerHand.add(tmp);
                tmp.setPlacement(playerHand);
            }
        }
    }

    public int getHealth() {
        return health.get();
    }

    public void setHealth(int health) {
        if (health < 0) health = 0;
        this.health.set(health);
        this.healthPercentage.set((double) this.health.get() / maxHealth);
    }

    public void putCardOnBoard(Card cardToPut) {
        if (playerBoard.size() < mainApp.getMaxCardsOnBoard()) {
            if (mana.get() >= cardToPut.getManaCost()) {
                setMana(mana.get() - cardToPut.getManaCost());
                playerHand.remove(cardToPut);
                playerBoard.add(cardToPut);
                cardToPut.setPlacement(playerBoard);
                cardToPut.setUsed(true);
            }
        } else System.out.println("Not enough mana.");
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty healthProperty() {
        return health;
    }

    public IntegerProperty manaProperty() {
        return mana;
    }

    public DoubleProperty healthPercentageProperty() {
        return healthPercentage;
    }

    public DoubleProperty manaPercentageProperty() {
        return manaPercentage;
    }

    public String getName() {
        return name.get();
    }

    public Label getPlayerNameLabel() {
        return playerNameLabel;
    }

    public void setPlayerNameLabel(Label playerNameLabel) {
        this.playerNameLabel = playerNameLabel;
    }

    public ObservableList<Card> getPlayerDeck() {
        return playerDeck;
    }

    public void setPlayerDeck(ObservableList<Card> playerDeck) {
        this.playerDeck = playerDeck;
    }

    public boolean isHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public ObservableList<Card> getPlayerHand() {
        return playerHand;
    }

    public ObservableList<Card> getPlayerBoard() {
        return playerBoard;
    }

    public void setPlayerHandPane(AnchorPane playerHandPane) {
    }

    public void setPlayerBoardPane(AnchorPane playerBoardPane) {
    }

    public int getMana() {
        return mana.get();
    }

    public void setMana(int mana) {
        if (mana > maxMana) this.mana.set(maxMana);
        else {
            this.mana.set(mana);
        }
        this.manaPercentage.set((double) this.mana.get() / maxMana);

        for (Card x : playerHand) {
            if (x.getManaCost() > mana) {
                x.getCardNodes().setOpacity(0.5);
            } else {
                x.getCardNodes().setOpacity(1.0);
            }
        }
    }

}
