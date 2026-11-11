package org.example.client.gui.screen;

import org.example.Character.GameSaveData;
import org.example.GameLoop.SaveSystem;
import org.example.client.Client;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.widgets.ButtonWidget;

import com.raylib.Colors;
import com.raylib.Raylib;

/** Pause menu over the map: resume, save, or save and return to the main menu. */
public class PauseScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 45;
    private static final int BUTTON_GAP = 15;

    private String status = "";

    public PauseScreen(Client client) {
        super(client);
    }

    @Override
    public void init() {
        int x = this.width / 2 - BUTTON_WIDTH / 2;
        int y = this.height / 2 - (3 * BUTTON_HEIGHT + 2 * BUTTON_GAP) / 2;

        this.addElement(new ButtonWidget("Resume", x, y, BUTTON_WIDTH, BUTTON_HEIGHT, this::resume));

        y += BUTTON_HEIGHT + BUTTON_GAP;
        this.addElement(new ButtonWidget("Save", x, y, BUTTON_WIDTH, BUTTON_HEIGHT, this::save));

        y += BUTTON_HEIGHT + BUTTON_GAP;
        this.addElement(new ButtonWidget("Save & Exit", x, y, BUTTON_WIDTH, BUTTON_HEIGHT, this::saveAndExit));
    }

    @Override
    public void keyPressed(int keyCode) {
        if (keyCode == Raylib.KEY_ESCAPE) {
            this.resume();
            return;
        }

        super.keyPressed(keyCode);
    }

    private void resume() {
        this.client.gui.setScreen(new GameScreen(this.client));
    }

    private void save() {
        this.status = this.saveGame()
                ? "Saved to slot " + this.client.currentSaveSlot + "."
                : "Could not save: no game in progress.";
    }

    private void saveAndExit() {
        if (this.saveGame()) {
            this.exitToMainMenu();
        } else {
            this.status = "Could not save: no game in progress.";
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        graphics.rectangle(0, 0, this.width, this.height, Raylib.Fade(Colors.BLACK, 0.75f));

        String title = "Paused";
        int titleY = this.height / 2 - (3 * BUTTON_HEIGHT + 2 * BUTTON_GAP) / 2 - 40;
        graphics.text(title, this.width / 2 - GuiGraphics.getTextWidth(title) / 2, titleY, Colors.WHITE);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        super.render(graphics, mouseX, mouseY);

        if (!this.status.isEmpty()) {
            int statusY = this.height / 2 + (3 * BUTTON_HEIGHT + 2 * BUTTON_GAP) / 2 + 20;
            graphics.text(this.status, this.width / 2 - GuiGraphics.getTextWidth(this.status) / 2, statusY, Colors.WHITE);
        }
    }

    private boolean saveGame() {
        if (this.client.mapManager == null || this.client.player.getCharacter() == null || this.client.currentSaveSlot <= 0) {
            return false;
        }

        SaveSystem.saveGame(new GameSaveData(this.client.player.getCharacter(), this.client.player, this.client.mapManager), this.client.currentSaveSlot);

        return true;
    }

    private void exitToMainMenu() {
        this.client.mapManager = null;
        this.client.mapRenderer = null;
        this.client.mapInput = null;
        this.client.currentSaveSlot = 0;

        this.client.gui.setScreenOverlay(null);
        this.client.gui.setScreen(new MainMenuScreen(this.client));
    }
}
