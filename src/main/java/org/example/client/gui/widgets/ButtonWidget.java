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
        graphics.blitNineSliced(this.x, this.y, this.width, this.height, 32, 16);
        Raylib.Color color = this.isHovered() ? Colors.BLUE : Colors.WHITE;

        int x = this.getX() + (this.width / 2) - (Raylib.MeasureText(this.text, 24) / 2);
        int y = this.getY() + (this.height / 2) - (24 / 2);

        graphics.text(
                this.text,
                x,
                y,
                color
        );
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        this.action.run();
    }
}
