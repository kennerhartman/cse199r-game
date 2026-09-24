package org.example.client.gui;

import org.example.client.Client;
import org.example.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;

public class Gui {
    public Client client;

    @Nullable
    public Screen screen;

    public Gui(Client client) {
        this.client = client;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1.0f);

        if (this.screen != null) {
            this.screen.render(graphics, mouseX, mouseY);
        }
    }

    public void setScreen(@Nullable Screen screen) {
        this.screen = screen;

        if (screen != null) {
            this.screen.init(this.getScreenWidth(), this.getScreenHeight());
        }
    }

    public int getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getScreenHeight() {
        return Gdx.graphics.getHeight();
    }
}
