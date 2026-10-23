package org.example.client.gui.screen;

import org.example.Character.GameSaveData;
import org.example.GameLoop.LoadSystem;
import org.example.GameLoop.SaveSystem;
import org.example.client.Client;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.screen.overlay.ConfirmationOverlay;
import org.example.client.gui.widgets.ButtonWidget;
import org.jetbrains.annotations.Nullable;

import com.raylib.Colors;
import com.raylib.Raylib;

public class LoadSaveSlotScreen extends Screen {
    private @Nullable final GameSaveData saveData;

    public LoadSaveSlotScreen(Client client, @Nullable GameSaveData saveData) {
        super(client);

        this.saveData = saveData;
    }

    @Override
    public void init() {
        int y = this.height / 2 - 25;

        int widgetWidth = (this.width / 3) - 25;

        int totalSpacing = this.width - (3 * widgetWidth);
        int gap = totalSpacing / 4;

        int saveSlotOneX = gap;
        int saveSlotTwoX = saveSlotOneX + widgetWidth + gap;
        int saveSlotThreeX = saveSlotTwoX + widgetWidth + gap;

        this.addElement(
                new SaveSlotWidget(1, "Save 1", widgetWidth, saveSlotOneX, y, () -> this.saveOrLoadSlot(1))
        );

        this.addElement(
                new SaveSlotWidget(2, "Save 2", widgetWidth, saveSlotTwoX, y, () -> this.saveOrLoadSlot(2))
        );

        this.addElement(
                new SaveSlotWidget(3, "Save 3", widgetWidth, saveSlotThreeX, y, () -> this.saveOrLoadSlot(3))
        );
    }

    private void saveOrLoadSlot(int slotNumber) {
        if (this.saveData != null) {
            boolean doesSaveExist = SaveSystem.doesSaveExist("save" + slotNumber);

            if (doesSaveExist) {
                this.client.gui.setScreenOverlay(new ConfirmationOverlay(this.client, this.saveData, slotNumber));
            } else {
                SaveSystem.saveGame(this.saveData, slotNumber);

                this.client.gui.setScreen(new MainMenuScreen(this.client));
            }
        } else {
            LoadSystem.loadGame(slotNumber);
        }
    }

    static class SaveSlotWidget extends ButtonWidget {
        private final int saveSlot;

        private boolean doesSaveExist;

        public SaveSlotWidget(int saveSlot, String text, int width, int x, int y, Runnable action) {
            super(text, x, y, width, 75, action);

            this.saveSlot = saveSlot;
            this.doesSaveExist = SaveSystem.doesSaveExist("save" + this.saveSlot);
        }

        @Override
        public void extractRenderState(GuiGraphics graphics, int mouseX, int mouseY) {
            super.extractRenderState(graphics, mouseX, mouseY);

            this.doesSaveExist = SaveSystem.doesSaveExist("save" + this.saveSlot);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY) {
            Raylib.Color color = this.isHovered() ? Colors.BLUE : Colors.WHITE;

            graphics.blitNineSliced(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 32, 16);

            int x = this.getX() + (this.getWidth() / 2) - (Raylib.MeasureText(this.getText(), 24) / 2);
            int y = this.getY() + (this.getHeight() / 2) - (24 / 2);

            graphics.text(
                    this.getText(),
                    x,
                    this.doesSaveExist ? y : y - 12,
                    color
            );

            if (!this.doesSaveExist) {
                String subtext = "[empty]";

                int subtextX = this.getX() + (this.getWidth() / 2) - (Raylib.MeasureText(subtext, 16) / 2);

                graphics.text(
                        "[empty]",
                        subtextX,
                        y + 15,
                        16,
                        color
                );
            }
        }
    }
}
