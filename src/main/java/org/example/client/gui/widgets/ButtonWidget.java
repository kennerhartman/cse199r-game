package org.example.client.gui.widgets;

import org.example.client.gui.GuiGraphics;

import com.raylib.Colors;
import com.raylib.Raylib;

public class ButtonWidget extends AbstractWidget{
    private final Runnable action;
    private final String text;

    public ButtonWidget(String text, int x, int y, int width, int height, Runnable action) {
        super(x, y, width, height);

        this.action = action;
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

    @Override
    public void onClick(int mouseX, int mouseY) {
        this.action.run();
    }
}
