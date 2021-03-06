package com.github.mesayah.cardgame;


import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Card {

    private static CardGame mainApp;
    private final SimpleIntegerProperty health;
    private final int maxHealth;
    private final SimpleIntegerProperty manaCost;
    private final SimpleIntegerProperty attack;
    private final StringProperty name;
    private final Group cardNodes = new Group();
    private Player owner;
    private ObservableList<Card> placement;
    private boolean isUsed;

    public Card(int health, int manaCost, int attack, String name, String imageURL) {
        this.health = new SimpleIntegerProperty(health);
        this.maxHealth = health;
        this.manaCost = new SimpleIntegerProperty(manaCost);

        this.attack = new SimpleIntegerProperty(attack);
        this.name = new SimpleStringProperty(name);

        ImageView cardImage = new ImageView(new Image(imageURL));
        cardImage.setLayoutX(0);
        cardImage.setLayoutY(0);

        Label healthLabel = new Label();
        healthLabel.textProperty().bind(Bindings.format("%d", this.health));
        Label manaCostLabel = new Label();
        manaCostLabel.textProperty().bind(Bindings.format("%d", this.manaCost));
        Label attackLabel = new Label();
        attackLabel.textProperty().bind(Bindings.format("%d", this.attack));
        Label nameLabel = new Label();
        nameLabel.textProperty().bind(this.name);

        healthLabel.setLayoutX(71);
        healthLabel.setLayoutY(94);
        int cardWidth = 90;
        healthLabel.setPrefWidth(cardWidth);
        healthLabel.setTextAlignment(TextAlignment.CENTER);
        healthLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        healthLabel.setTextFill(Color.WHITE);
        manaCostLabel.setLayoutX(71);
        manaCostLabel.setLayoutY(5);
        manaCostLabel.setPrefWidth(cardWidth);
        manaCostLabel.setTextAlignment(TextAlignment.CENTER);
        manaCostLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        manaCostLabel.setTextFill(Color.WHITE);
        attackLabel.setLayoutX(10);//10
        attackLabel.setLayoutY(94);//94
        attackLabel.setPrefWidth(cardWidth);
        attackLabel.setTextAlignment(TextAlignment.CENTER);
        attackLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        attackLabel.setTextFill(Color.WHITE);
        nameLabel.setLayoutX(10);
        nameLabel.setLayoutY(75);
        nameLabel.setPrefWidth(cardWidth);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        cardNodes.getChildren().addAll(cardImage, healthLabel, manaCostLabel, attackLabel, nameLabel);
        cardNodes.setLayoutY(15);


        cardNodes.setOnMouseClicked(event -> cardClicked());
    }

    public static void setMainApp(CardGame mainApp) {
        Card.mainApp = mainApp;
    }

    private void cardClicked() {
        if (mainApp.isGameStarted()) {
            if (this.placement == mainApp.getCurrentPlayer().getPlayerHand()) {
                mainApp.getCurrentPlayer().putCardOnBoard(this);
            } else if (mainApp.getTurnPhase() == TurnPhase.ChooseFriendlyCard) {
                if (this.getOwner() == mainApp.getCurrentPlayer() && !this.isUsed()) {
                    System.out.println("Owned card was selected.");
                    mainApp.setTurnPhase(TurnPhase.ChooseTargetCard);
                    mainApp.setSelectedCard(this);
                } else if (this.isUsed()) {
                    System.out.println("Card was already used.");
                } else {
                    System.out.println("You cannot select enemy's card.");
                }
            } else {
                if (this.getOwner() != mainApp.getCurrentPlayer() && (this.placement == mainApp.getPlayerOne().getPlayerBoard() || this.placement == mainApp.getPlayerTwo().getPlayerBoard())) {

                    System.out.println("Target card was selected.");
                    performAttack(mainApp.getSelectedCard(), this);
                    mainApp.setTurnPhase(TurnPhase.ChooseFriendlyCard);
                    mainApp.setSelectedCard(null);
                } else if (this.getOwner() == mainApp.getCurrentPlayer()) {
                    if (mainApp.getSelectedCard() == this) {
                        mainApp.setTurnPhase(TurnPhase.ChooseFriendlyCard);
                        mainApp.setSelectedCard(null);
                        System.out.println("Card unselected.");
                    }
                    System.out.println("You cannot attack your own card.");
                } else {
                    System.out.println("You cannot attack player's hand.");
                }
            }
            mainApp.redrawAllCards();
            mainApp.checkIfWon();
        }
    }

    private Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        if (used) this.getCardNodes().setOpacity(0.5);
        else this.getCardNodes().setOpacity(1.0);
        isUsed = used;
    }

    private void performAttack(Card attacker, Card target) {
        if (!attacker.isUsed) {
            target.setHealth(target.getHealth() - attacker.getAttack());
            attacker.setHealth(attacker.getHealth() - target.getAttack());
            attacker.setUsed(true);
        } else System.out.println("Card was already used.");
    }

    private int getHealth() {
        return health.get();
    }

    private void setHealth(int health) {
        this.health.set(health);
        if (this.health.get() <= 0) {
            killCard(this);
        } else {
            Label tmp = (Label) cardNodes.getChildren().get(1);
            if ((float) this.health.get() / maxHealth < 0.6) {
                tmp.setTextFill(Color.ORANGE);
            }
        }


    }

    public int getAttack() {
        return attack.get();
    }

    private void killCard(Card card) {
        card.placement.remove(card);
        mainApp.redrawAllCards();
    }

    public Group getCardNodes() {
        return cardNodes;
    }

    @Override
    public String toString() {
        return "Card{" +
                "health=" + health +
                ", manaCost=" + manaCost +
                ", attack=" + attack +
                ", name='" + name + '\'' +
                '}';
    }

    public void calcNodeLayout(int index) {
        this.cardNodes.setLayoutY(15);
        this.cardNodes.setLayoutX(10 + 100 * index);
    }

    public int getManaCost() {
        return manaCost.get();
    }

    public void setPlacement(ObservableList<Card> placement) {
        this.placement = placement;
    }

}
