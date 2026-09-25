package org.example.client.gui.widgets;

import org.example.client.Client;
import org.example.client.gui.GuiGraphics;

import com.raylib.Colors;
import com.raylib.Raylib;

public class TextWidget extends AbstractWidget {
    private final String text;

    public TextWidget(String text, int x, int y) {
        super(x, y, getTextWidth(text), getTextHeight(text));

        this.text = text;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        Raylib.Color color;

        if (this.isHovered()) {
            color = Colors.BLUE;
        } else {
            color = Colors.WHITE;
        }

        graphics.text(
                this.text,
                this.getX(),
                this.getY(),
                color
        );
    }

    private static int getTextWidth(String text) {
        return (int) Raylib.MeasureTextEx(Client.getInstance().font, text, 24, 2).x();
    }

    private static int getTextHeight(String text) {
        return (int) Raylib.MeasureTextEx(Client.getInstance().font, text, 24, 2).y();
    }
}
