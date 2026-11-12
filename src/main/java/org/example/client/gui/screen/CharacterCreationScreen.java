package org.example.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.Character.GameSaveData;
import org.example.card.Card;
import org.example.card.Cards;
import org.example.client.Client;
import org.example.client.audio.MusicManager;
import org.example.client.audio.MusicManager.MusicType;
import org.example.client.gui.widgets.ButtonWidget;
import org.example.client.gui.widgets.CardWidget;
import org.example.client.gui.widgets.TextInputWidget;
import org.example.client.gui.widgets.TextWidget;
import org.example.client.gui.widgets.SpriteSelectorWidget;
import org.example.client.texture.AnimatedSprite;
import org.example.client.texture.SpriteSheet;

public class CharacterCreationScreen extends Screen {
    private final Screen parent;
    private TextInputWidget characterNameInputWidget;
    private SpriteSelectorWidget spriteSelector;


    public CharacterCreationScreen(Client client, Screen parent) {
        super(client);
        this.parent = parent;
    }

    @Override
    public void init() {

        this.characterNameInputWidget = new TextInputWidget(
                this.width / 2 - 150,
                340,
                300,
                40
        );

        SpriteSheet knightSheet = this.client.spriteManager.getSheet(
                "sprites/knight/Soldier_Idle.png"
        );

        SpriteSheet orcSheet = this.client.spriteManager.getSheet(
                "sprites/orc/Orc_Idle.png"
        );

        this.spriteSelector = new SpriteSelectorWidget(
                this.width / 2 - 100,
                105,
                200,
                180,
                List.of(
                        new AnimatedSprite(knightSheet, 8.0f),
                        new AnimatedSprite(orcSheet, 8.0f)
                ),
                List.of("knight", "orc")
        );

        this.addElement(this.spriteSelector);

        this.addElement(
                new ButtonWidget(
                        "<",
                        this.width / 2 - 180,
                        170,
                        50,
                        50,
                        () -> this.spriteSelector.previousSprite()
                )
        );

        this.addElement(
                new ButtonWidget(
                        ">",
                        this.width / 2 + 130,
                        170,
                        50,
                        50,
                        () -> this.spriteSelector.nextSprite()
                )
        );

        this.addElement(
                new ButtonWidget(
                        "X",
                        15,
                        15,
                        50,
                        50,
                        this::onClose
                )
        );

        this.addElement(
                new TextWidget(
                        "Enter Character Name:",
                        this.width / 2 - 150,
                        310
                )
        );

        this.addElement(characterNameInputWidget);

        this.addElement(
                new ButtonWidget(
                        "Continue",
                        this.width / 2 - 150,
                        405,
                        300,
                        50,
                        this::createCharacter
                )
        );

        this.client.musicManager.play(MusicType.MENU);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.client.gui.setScreen(this.parent);
    }

    private void createCharacter() {
        String name = this.characterNameInputWidget.getText();

        if (name.isBlank()) {
            return;
        }

        String spriteId = this.spriteSelector.getSelectedSpriteId();

        this.client.player.createNewCharacter(name, spriteId);

        this.client.gui.setScreen(
                new DeckBuilderScreen(this.client, this.parent)
        );
    }

    static class DeckBuilderScreen extends Screen {
        private final Screen parent;

        private final List<Card.CardStat> stats = List.of(
                Card.CardStat.HEALTH,
                Card.CardStat.SPEED,
                Card.CardStat.STRENGTH,
                Card.CardStat.ATTACK
        );

        private Card.CardStat currentStat = this.stats.getFirst();

        private ScreenCard card1;
        private ScreenCard card2;

        private final Random random = new Random();

        public DeckBuilderScreen(Client client, Screen parent) {
            super(client);

            this.parent = parent;
            this.addCards();
        }

        @Override
        public void init() {
            this.addElement(
                    new ButtonWidget("X", 15, 15, 50, 50, this::onClose)
            );

            CardWidget widget1 = this.buildCardWidget(this.card1);

            CardWidget widget2 = this.buildCardWidget(this.card2);

            this.addElement(widget1);
            this.addElement(widget2);
        }

        @Override
        public void onClose() {
            super.onClose();

            this.client.gui.setScreen(this.parent);
        }

        private void addCards() {
            List<Card> cards = Cards.getCardsByStat(this.currentStat);

            this.card1 = new ScreenCard(
                    0,
                    cards.get(this.random.nextInt(cards.size()))
            );

            this.card2 = new ScreenCard(
                    1,
                    cards.get(this.random.nextInt(cards.size()))
            );
        }

        private void addCardToDeckAndRefresh(ScreenCard card) {
            ArrayList<Card> playersHand = this.client.player.getCharacter().getDrawPile();

            playersHand.add(card.card);

            int currentStatIndex = this.stats.indexOf(this.currentStat);

            if (currentStatIndex + 1 < this.stats.size()) {
                this.currentStat = this.stats.get(currentStatIndex + 1);

                this.addCards();

                this.rebuildScreen();
            } else {
                GameSaveData saveData = new GameSaveData(this.client.player.getCharacter(), this.client.player, this.client.mapManager);

                this.client.gui.setScreen(new LoadSaveSlotScreen(this.client, saveData));

                // CLOSE SCREEN

                // START GAME
            }
        }

        private CardWidget buildCardWidget(ScreenCard card) {
            final int cardWidth = 250;
            final int cardHeight = 350;
            final int spacing = 16;

            int totalGroupWidth = (2 * cardWidth) + spacing;
            int startX = (this.width / 2) - (totalGroupWidth / 2);

            int cardWidgetX = startX + (card.index * (cardWidth + spacing));
            int cardWidgetY = (this.height / 2) - (cardHeight / 2);

            return new CardWidget(
                    card.card,
                    cardWidgetX,
                    cardWidgetY,
                    cardWidth,
                    cardHeight,
                    () -> this.addCardToDeckAndRefresh(card)
            );
        }

        private void rebuildScreen() {
            this.elements.clear();
            this.init();
        }

        private record ScreenCard(int index, Card card) {}
    }
}

