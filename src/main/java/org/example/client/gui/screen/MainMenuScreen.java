package org.example.client.gui.screen;

import org.example.client.Client;
import org.example.client.gui.widgets.BorderWidget;
import org.example.client.gui.widgets.ButtonWidget;

public class MainMenuScreen extends Screen {
    public MainMenuScreen(Client client) {
        super(client);
    }

    @Override
    public void init() {
        int borderWidgetWidth = 150;
        int borderWidgetHeight = 250;
        int borderX = this.width / 2 - borderWidgetWidth / 2;
        int borderY = this.height / 2 - borderWidgetHeight / 2;

        this.elements.add(
                new BorderWidget(
                        borderX,
                        borderY,
                        borderWidgetWidth,
                        borderWidgetHeight
                )
        );

        this.elements.add(
                new ButtonWidget(
                        "New",
                        borderX + 125/8,
                        borderY + 25,
                        125,
                        45,
                        () -> Client.getInstance().gui.setScreen(new CharacterCreationScreen(this.client, this))
                )
        );

        this.elements.add(
                new ButtonWidget(
                        "Load",
                        borderX + 125/8,
                        borderY + 95,
                        125,
                        45,
                        () -> {}
                )
        );
    }
}
