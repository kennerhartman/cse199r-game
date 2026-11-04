package org.example.client.gui.screen.overlay;

import org.example.Character.GameSaveData;
import org.example.GameLoop.SaveSystem;
import org.example.client.Client;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.screen.MainMenuScreen;
import org.example.client.gui.widgets.ButtonWidget;

import com.raylib.Colors;
import com.raylib.Raylib;

public class ConfirmationOverlay extends ScreenOverlay {
    private final GameSaveData saveData;
    private final int saveSlot;

    public ConfirmationOverlay(Client client, GameSaveData saveData, int saveSlot) {
        super(client);

        this.saveData = saveData;
        this.saveSlot = saveSlot;
    }

    @Override
    public void init() {
        int buttonWidth = 100;
        int buttonHeight = 50;
        int padding = 16;

        int totalWidth = (buttonWidth * 2) + padding;

        int startX = (this.width - totalWidth) / 2;
        int y = (this.height - buttonHeight) / 2 + 16;

        this.addElement(
                new ButtonWidget(
                        "Yes",
                        startX,
                        y,
                        buttonWidth,
                        buttonHeight,
                        () -> {
                            SaveSystem.saveGame(this.saveData, this.saveSlot);

                            this.client.gui.setScreenOverlay(null);
                            this.client.gui.setScreen(new MainMenuScreen(this.client));
                        }
                )
        );

        this.addElement(
                new ButtonWidget(
                        "No",
                        startX + buttonWidth + padding,
                        y,
                        buttonWidth,
                        buttonHeight,
                        () -> this.client.gui.setScreenOverlay(null)
                )
        );
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        graphics.rectangle(0, 0, this.width, this.height, Raylib.Fade(Colors.BLACK, 0.75f));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        super.render(graphics, mouseX, mouseY);

        String lineOne = "This will overwrite save slot " + this.saveSlot + ".";
        String lineTwo = "Continue?";

        graphics.text(
                lineOne,
                (this.width / 2) - (GuiGraphics.getTextWidth(lineOne) / 2),
                this.height / 2 - 75,
                Colors.WHITE
        );

        graphics.text(
                lineTwo,
                (this.width / 2) - (GuiGraphics.getTextWidth(lineTwo) / 2),
                this.height / 2 - 50,
                Colors.WHITE
        );
    }
}
