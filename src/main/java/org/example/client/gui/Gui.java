package org.example.client.gui;

import org.example.client.Client;
import org.example.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

import com.raylib.Colors;
import com.raylib.Raylib;

public class Gui {
    public Client client;

    @Nullable
    public Screen screen;

    public Gui(Client client) {
        this.client = client;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        Raylib.BeginDrawing();
        Raylib.ClearBackground(Colors.BLACK);

        if (this.screen != null) {
            this.screen.render(graphics, mouseX, mouseY);
        }

        Raylib.EndDrawing();
    }

    public void setScreen(@Nullable Screen screen) {
        this.screen = screen;

        if (screen != null) {
            screen.clearElements();
            this.screen.init(this.getScreenWidth(), this.getScreenHeight());
        }
    }

    public int getScreenWidth() {
        return Raylib.GetScreenWidth();
    }

    public int getScreenHeight() {
        return Raylib.GetScreenHeight();
    }
}
