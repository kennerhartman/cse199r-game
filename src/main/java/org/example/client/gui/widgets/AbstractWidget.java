package org.example.client.gui.widgets;

import org.example.client.gui.element.Element;
import org.example.client.gui.element.Focusable;
import org.example.client.gui.element.GuiElementEvent;
import org.example.client.gui.GuiGraphics;

public abstract class AbstractWidget implements Element, Focusable, GuiElementEvent {
    int x;
    int y;
    int width;
    int height;

    boolean isHovered = false;
    boolean isFocused = false;
    boolean isActive = true;

    public AbstractWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void extractRenderState(GuiGraphics graphics, int mouseX, int mouseY) {
        this.isHovered = this.isMouseOver(mouseX, mouseY) && this.isActive;
        this.render(graphics, mouseX, mouseY);
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY);

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY) {
        if (this.isActive) {
            if (this.isMouseOver(mouseX, mouseY)) {
                this.onClick(mouseX, mouseY);

                return true;
            }

            this.setFocused(false);

        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode) {
        this.onKeyPressed(keyCode);

        return GuiElementEvent.super.keyPressed(keyCode);
    }

    @Override
    public void keyDown(int keyCode) {
        this.onKeyDown(keyCode);
    }

    public void onClick(int mouseX, int mouseY) {
    }

    public void onKeyPressed(int code) {
    }

    public void onKeyDown(int code) {
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    @Override
    public boolean isFocused() {
        return this.isFocused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.isFocused = focused;
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

    @Override
    public boolean getActive() {
        return this.isActive;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }
}
