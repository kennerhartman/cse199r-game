package org.example.client.gui;

import org.example.client.Client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GuiGraphics {
    Client client;
    SpriteBatch batch = new SpriteBatch();

    public GuiGraphics(Client client) {
        this.client = client;
    }

    public void text(String text, int x, int y, int color) {
        BitmapFont font = this.client.font;

        this.batch.getProjectionMatrix().setToOrtho2D(0, 0, this.getScreenWidth(), this.getScreenHeight());

        this.batch.begin();

        Color initialColor = font.getColor();

        font.setColor(this.getColor(color));

        font.draw(this.batch, text, x, this.getScreenHeight() - y);

        font.setColor(initialColor);
        this.batch.end();
    }

    public int getScreenWidth() {
        return this.client.gui.getScreenWidth();
    }

    public int getScreenHeight() {
        return this.client.gui.getScreenHeight();
    }

    private Color getColor(int color) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;

        float aF = (float) a / 255;
        float rF = (float) r / 255;
        float gF = (float) g / 255;
        float bF = (float) b / 255;

        return new Color(rF, gF, bF, aF);
    }
}
