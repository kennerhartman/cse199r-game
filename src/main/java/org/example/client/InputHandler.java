package org.example.client;

import org.example.client.gui.Gui;
import org.example.client.gui.screen.Screen;

import com.raylib.Raylib;

public class InputHandler {
    int mouseX = 0;
    int mouseY = 0;

    public void handleInput() {
        int code = Raylib.GetKeyPressed();

        while (code != 0) {
            this.keyDown(code);

            code = Raylib.GetKeyPressed();
        }

        Raylib.Vector2 mousePos = Raylib.GetMousePosition();

        this.mouseX = (int) mousePos.x();
        this.mouseY = (int) mousePos.y();
        if (Raylib.IsMouseButtonPressed(0)) {
            this.mouseClicked(this.getVirtualMouseX(), this.getVirtualMouseY());
        }
    }

    public boolean keyDown(int keycode) {
        Screen screen = Client.getInstance().gui.screen;

        if (screen != null) {
            // TODO: IMPLEMENT

            return true;
        }

        return false;
    }

    public void mouseClicked(int mouseX, int mouseY) {
        Screen screen = Client.getInstance().gui.screen;

        if (screen != null) {
            screen.mouseClicked(mouseX, mouseY);
        }
    }

    public int getVirtualMouseX() {
        float scale = Client.getInstance().gui.getScale();
        float screenWidth = (float) Client.getInstance().gui.getScreenWidth();
        int destWidth = (int) (Gui.VIRTUAL_WIDTH * scale);
        int destX = (int) ((screenWidth - destWidth) / 2);

        return (int) ((this.mouseX - destX) / scale);
    }

    public int getVirtualMouseY() {
        float scale = Client.getInstance().gui.getScale();
        float screenHeight = (float) Client.getInstance().gui.getScreenHeight();
        int destHeight = (int) (Gui.VIRTUAL_HEIGHT * scale);
        int destY = (int) ((screenHeight - destHeight) / 2);

        return (int) ((this.mouseY - destY) / scale);
    }

    public int getMouseX() {
        return this.mouseX;
    }

    public int getMouseY() {
        return this.mouseY;
    }
}