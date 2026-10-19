package org.example.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.card.Card;
import org.example.card.Cards;
import org.example.client.Client;
import org.example.client.gui.widgets.ButtonWidget;
import org.example.client.gui.widgets.CardWidget;
import org.example.client.gui.widgets.TextInputWidget;
import org.example.client.gui.widgets.TextWidget;
import org.example.core.registries.Registries;

public class CharacterCreationScreen extends Screen {
    private final Screen parent;
    private TextInputWidget characterNameInputWidget;

    public CharacterCreationScreen(Client client, Screen parent) {
        super(client);

        this.parent = parent;
    }

    @Override
    public void init() {
        this.characterNameInputWidget = new TextInputWidget(
                this.width / 2 - 150,
                250,
                300,
                50
        );

        this.elements.add(
                new ButtonWidget("X", 15, 15, 50, 50, this::onClose)
        );

        this.elements.add(
                new TextWidget(
                        "Enter Character Name:",
                        this.width / 2 - 150,
                        225
                )
        );

        this.elements.add(this.characterNameInputWidget);

        this.elements.add(
                new ButtonWidget(
                        "Continue",
                        this.width / 2 - 150,
                        325,
                        300,
                        50,
                        this::createCharacter
                )
        );
    }

    @Override
    public void onClose() {
        super.onClose();
        this.client.gui.setScreen(this.parent);
    }

    private void createCharacter() {
        String name = this.characterNameInputWidget.getText();

        if (name.isEmpty()) {
            return;
        }

        this.client.player.CreateNewCharacter(name);

        this.client.gui.setScreen(new DeckBuilderScreen(this.client, this.parent));
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
            this.elements.add(
                    new ButtonWidget("X", 15, 15, 50, 50, this::onClose)
            );

            CardWidget widget1 = this.buildCardWidget(this.card1);

            CardWidget widget2 = this.buildCardWidget(this.card2);

            this.elements.add(widget1);
            this.elements.add(widget2);
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
            ArrayList<Card> playersHand = this.client.player.getCharacter().getHand();

            playersHand.add(card.card);

            int currentStatIndex = this.stats.indexOf(this.currentStat);

            if (currentStatIndex + 1 < this.stats.size()) {
                this.currentStat = this.stats.get(currentStatIndex + 1);

                this.addCards();

                this.rebuildScreen();
            } else {
                // SAVE CHARACTER

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

