package org.example.client.gui.screen.gameplay;

import org.example.card.Card;
import org.example.card.CardStorage;
import org.example.client.Client;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.widgets.ButtonWidget;
import org.example.client.gui.widgets.CardWidget;
import org.example.client.gui.widgets.TextWidget;

import com.raylib.Colors;
import com.raylib.Raylib;

public class ShopScreen extends EncounterScreen {
    private final CardStorage shop;

    private String errorMessage = null;
    private long errorDisplayTime = 0L;

    public ShopScreen(Client client) {
        super(client);

        this.encounter.createShop(this.client.cardStorage);

        this.shop = this.client.cardStorage;
    }

    @Override
    public void init() {
        this.addElement(
                new TextWidget(
                        "Shop",
                        this.width / 2 - GuiGraphics.getTextWidth("Shop") / 2,
                        28
                )
        );

        String amountOfCoins = String.format("Coins: %s", this.player.getCurrency());

        this.addElement(
                new TextWidget(
                        amountOfCoins,
                        this.width - GuiGraphics.getTextWidth(amountOfCoins) - 15,
                        28,
                        Colors.GOLD
                )
        );

        int cardWidth = 200;
        int cardHeight = 260;
        int spacing = 20;
        int totalCards = this.shop.StoreCards.size();

        int totalRowWidth = (totalCards * cardWidth) + Math.max(0, (totalCards - 1) * spacing);
        int startX = (this.width - totalRowWidth) / 2;
        int startY = 80;

        for (int i = 0; i < totalCards; i++) {
            Card storeCard = this.shop.StoreCards.get(i);

            int cardX = startX + i * (cardWidth + spacing);
            int cardY = startY;

            this.addElement(
                    new CardWidget(
                            storeCard,
                            cardX,
                            cardY,
                            cardWidth,
                            cardHeight,
                            () -> this.buy(storeCard)
                    )
            );

            String costString = String.format("Cost: %s", storeCard.getPrice());

            this.addElement(
                    new TextWidget(
                            costString,
                            cardX + (cardWidth / 2) - (GuiGraphics.getTextWidth(costString) / 2),
                            cardY + cardHeight + 12,
                            Colors.GOLD
                    )
            );
        }

        this.addElement(
                new ButtonWidget(
                        "Exit Shop",
                        this.width / 2 - 148 / 2,
                        this.height - 84,
                        148,
                        56,
                        () -> this.client.gui.setScreen(new GameScreen(this.client))
                )
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        super.render(graphics, mouseX, mouseY);

        if (this.errorMessage != null) {
            if (System.currentTimeMillis() - this.errorDisplayTime < 2000) {
                int errorWidth = GuiGraphics.getTextWidth(this.errorMessage);

                graphics.text(
                        this.errorMessage,
                        (this.width / 2) - (errorWidth / 2),
                        this.height - 130,
                        Colors.RED
                );
            } else {
                this.errorMessage = null;
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        graphics.rectangle(0, 0, this.width, this.height, Raylib.Fade(Colors.BLACK, 0.75f));
    }

    @Override
    public void keyPressed(int keyCode) {
        if (keyCode == Raylib.KEY_ESCAPE) {
            this.client.gui.setScreen(new GameScreen(this.client));

            return;
        }

        if (keyCode == Raylib.KEY_ENTER) {
            this.rebuildShop();
        }

        super.keyPressed(keyCode);
    }

    private void buy(Card card) {
        if (this.player.spendCurrency(card.getPrice())) {
            this.player.deck.addCard(card);
            this.shop.StoreCards.remove(card);
            this.rebuildShop();
        } else {
            this.errorMessage = "Not enough coins!";
            this.errorDisplayTime = System.currentTimeMillis();
        }
    }

    private void rebuildShop() {
        this.clearElements();
        this.init();
    }
}