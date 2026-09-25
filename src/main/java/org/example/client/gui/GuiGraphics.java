package org.example.client.gui;

import org.example.client.Client;

import com.raylib.Raylib;

public class GuiGraphics {
    Client client;

    public GuiGraphics(Client client) {
        this.client = client;
    }

    public void text(String text, int x, int y, Raylib.Color color) {
        Raylib.DrawText(text, x, y, 24, color);
    }

    public int getScreenWidth() {
        return this.client.gui.getScreenWidth();
    }

    public int getScreenHeight() {
        return this.client.gui.getScreenHeight();
    }
}
