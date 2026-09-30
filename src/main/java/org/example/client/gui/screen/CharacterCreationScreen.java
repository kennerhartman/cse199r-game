package org.example.client.gui.screen;

import org.example.client.Client;
import org.example.client.gui.widgets.ButtonWidget;
import org.example.client.gui.widgets.TextWidget;

public class CharacterCreationScreen extends Screen {
    Screen parent;

    public CharacterCreationScreen(Client client, Screen parent) {
        super(client);

        this.parent = parent;
    }

    @Override
    public void init() {
        this.elements.add(
                new TextWidget("Character Creation Screen", this.width / 2, this.height / 2)
        );

        this.elements.add(
                new ButtonWidget("X", 15, 15, 50, 50, this::onClose)
        );
    }

    @Override
    public void onClose() {
        super.onClose();
        this.client.gui.setScreen(this.parent);
    }
}
