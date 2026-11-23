package org.example.client.gui.widgets;

import org.example.client.gui.GuiGraphics;

import com.raylib.Colors;
import com.raylib.Raylib;

public class TextWidget extends AbstractWidget {
    public enum Alignment {
        LEFT,
        CENTERED,
        RIGHT
    }

    private final String text;
    private final Alignment alignment;
    private final Raylib.Color color;

    public TextWidget(String text, int x, int y) {
        this(text, x, y, Alignment.LEFT, Colors.WHITE);
    }

    public TextWidget(String text, int x, int y, Raylib.Color color) {
        this(text, x, y, Alignment.LEFT, color);
    }

    public TextWidget(String text, int x, int y, Alignment alignment, Raylib.Color color) {
        super(x, y, GuiGraphics.getTextWidth(text), GuiGraphics.getTextHeight(text));

        this.text = text;
        this.alignment = alignment;
        this.color = color;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        int renderX = switch (this.alignment) {
            case LEFT -> this.getX();
            case CENTERED -> this.getX() - GuiGraphics.getTextWidth(this.text) / 2;
            case RIGHT -> this.getX() - GuiGraphics.getTextWidth(this.text);
        };

        graphics.text(
                this.text,
                renderX,
                this.getY() - GuiGraphics.getTextHeight(this.text) / 2,
                this.color
        );
    }
}