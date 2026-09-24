package org.example.client;

import org.example.client.gui.screen.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {
    int mouseX = 0;
    int mouseY = 0;

    public void handleInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            this.mouseClicked(this.mouseX, this.mouseY);
        }

        this.mouseX = Gdx.input.getX();
        this.mouseY = Gdx.input.getY();
    }

    @Override
    public boolean keyDown(int keycode) {
        Screen screen = Client.getInstance().gui.screen;

        if (screen != null) {
            // TODO: IMPLEMENT

            return true;
        }

        return super.keyDown(keycode);
    }

    public void mouseClicked(int mouseX, int mouseY) {
        Screen screen = Client.getInstance().gui.screen;

        if (screen != null) {
            screen.mouseClicked(mouseX, mouseY);
        }
    }

    public int getMouseX() {
        return this.mouseX;
    }

    public int getMouseY() {
        return this.mouseY;
    }
}
