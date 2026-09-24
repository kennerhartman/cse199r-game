package org.example.client.gui.widgets;

import org.example.client.gui.element.Element;
import org.example.client.gui.element.GuiElementEvent;
import org.example.client.gui.GuiGraphics;

public abstract class AbstractWidget implements Element, GuiElementEvent {
    int x;
    int y;
    int width;
    int height;
    boolean isHovered = false;

    public AbstractWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void extractRenderState(GuiGraphics graphics, int mouseX, int mouseY) {
        this.isHovered = this.isMouseOver(mouseX, mouseY);
        this.render(graphics, mouseX, mouseY);
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY);

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.onClick(mouseX, mouseY);

            return true;
        }

        return false;
    }

    public void onClick(int mouseX, int mouseY) {
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}
