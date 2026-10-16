package org.example.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.card.Card;
import org.example.client.Client;
import org.example.client.gui.widgets.ButtonWidget;
import org.example.client.gui.widgets.CardWidget;
import org.example.client.gui.widgets.TextInputWidget;
import org.example.client.gui.widgets.TextWidget;
import org.example.core.registries.Registries;

public class CharacterCreationScreen extends Screen {
    Screen parent;
    List<Card> cards;

    Random random = new Random();

    ScreenCard card1;
    ScreenCard card2;

    public CharacterCreationScreen(Client client, Screen parent) {
        super(client);

        this.parent = parent;
        this.cards = Registries.CARDS.stream().toList();
        this.addCards();
    }

    @Override
    public void init() {
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

        this.elements.add(
            new TextInputWidget(
                    this.width / 2 - 150,
                    250,
                    300,
                    50
            )
        );

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

        // this.elements.add(
        //         this.buildCardWidget(this.card1)
        // );
        //
        // this.elements.add(
        //         this.buildCardWidget(this.card2)
        // );
    }

    @Override
    public void onClose() {
        super.onClose();
        this.client.gui.setScreen(this.parent);
    }

    private void createCharacter() {

    }

    private void addCards() {
        this.card1 = new ScreenCard(0, this.cards.get(this.random.nextInt(this.cards.size())));
        this.card2 = new ScreenCard(1, this.cards.get(this.random.nextInt(this.cards.size())));
    }

    private void addCardToDeckAndRefresh(ScreenCard card) {
        ArrayList<Card> cards = this.client.player.getCharacter().getHand();

        cards.add(card.card);

        if (card.index == 0) {
            this.card1 = new ScreenCard(0, this.cards.get(this.random.nextInt(this.cards.size())));
        } else {
            this.card2 = new ScreenCard(1, this.cards.get(this.random.nextInt(this.cards.size())));
        }

        this.rebuild();
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

    private void rebuild() {
        this.elements.clear();
        this.init();
    }

    private record ScreenCard(int index, Card card) {}
}

