package org.example.client.gui.screen;

import org.example.client.Client;
import org.example.client.gui.widgets.ButtonWidget;

import com.raylib.Raylib;

public class GameScreen extends Screen {
    public GameScreen(Client client) {
        super(client);
    }

    @Override
    public void init() {
    }

    @Override
    public boolean allowsMapInput() {
        return true;
    }

    @Override
    public void keyPressed(int keyCode) {
        if (keyCode == Raylib.KEY_ESCAPE) {
            this.openPauseMenu();
            return;
        }

        super.keyPressed(keyCode);
    }

    private void openPauseMenu() {
        this.client.gui.setScreen(new PauseScreen(this.client));
    }
}
