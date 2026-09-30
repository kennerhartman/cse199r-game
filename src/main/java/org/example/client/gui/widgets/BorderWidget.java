package org.example.client.gui.widgets;

import org.example.client.gui.GuiGraphics;

public class BorderWidget extends AbstractWidget{
    public BorderWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.border(this.x, this.y, this.width, this.height);
    }
}
