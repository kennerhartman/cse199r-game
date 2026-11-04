package org.example.client;

import org.example.client.gui.Gui;
import org.example.client.gui.screen.Screen;

import com.raylib.Raylib;

public class InputHandler {
    int mouseX = 0;
    int mouseY = 0;

    int trackedKey = 0;
    boolean isNewPressThisFrame = false;
    boolean isHeldDownThisFrame = false;

    boolean startedTime = false;
    long startTime = 0L;

    public void handleInput() {
        int code = Raylib.GetKeyPressed();

        if (code != 0) {
            this.trackedKey = code;
            this.isNewPressThisFrame = true;
            this.keyPressed(code);
        } else {
            this.isNewPressThisFrame = false;
        }

        if (this.trackedKey != 0) {
            if (Raylib.IsKeyDown(this.trackedKey)) {
                this.isHeldDownThisFrame = true;

                this.keyDown(this.trackedKey);
            } else {
                this.isHeldDownThisFrame = false;
                this.trackedKey = 0;
            }
        } else {
            this.isHeldDownThisFrame = false;
        }

        if (this.isHeldDownThisFrame) {
            if (!this.startedTime) {
                this.startTime = System.nanoTime() + 500_000_000L;

                this.startedTime = true;
            } else {
                if (this.startTime != 0L) {
                    long now = System.nanoTime();

                    if (now > this.startTime) {
                        this.keyPressed(this.trackedKey);

                        this.startTime = now + 50_000_000L;
                    }
                }
            }
        } else {
            this.startedTime = false;
            this.startTime = 0L;
        }

        Raylib.Vector2 mousePos = Raylib.GetMousePosition();

        this.mouseX = (int) mousePos.x();
        this.mouseY = (int) mousePos.y();

        if (Raylib.IsMouseButtonPressed(0)) {
            this.mouseClicked(this.getVirtualMouseX(), this.getVirtualMouseY());
        }
    }

    public boolean keyPressed(int keycode) {
        Screen screen = Client.getInstance().gui.screen;

        if (screen != null) {
            screen.keyPressed(keycode);

            return true;
        }

        return false;
    }

    public boolean keyDown(int keycode) {
        Screen screen = Client.getInstance().gui.screen;

        if (screen != null) {
            screen.keyDown(keycode);

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