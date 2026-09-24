package org.example.client.gui.element;

import org.example.client.gui.GuiGraphics;

public interface Element {
    void extractRenderState(GuiGraphics graphics, int mouseX, int mouseY);

    void setX(int x);
    void setY(int y);

    int getX();
    int getY();

    int getWidth();
    int getHeight();
}
