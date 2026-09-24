package org.example.client.gui.screen;

import org.example.client.Client;
import org.example.client.gui.widgets.ButtonWidget;
import org.example.client.gui.widgets.TextWidget;

public class MainMenuScreen extends Screen {
    public MainMenuScreen(Client client) {
        super(client);
    }

    @Override
    public void init() {
        this.elements.add(
                new TextWidget("Main Menu Screen", 50, 50)
        );

        this.elements.add(
                new ButtonWidget("Click me!", 50, 75, 50, 50, () -> Client.getInstance().gui.setScreen(new CharacterCreationScreen(this.client, this)))
        );
    }
}
