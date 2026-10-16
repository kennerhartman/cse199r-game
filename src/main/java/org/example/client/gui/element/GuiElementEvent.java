package org.example.client.gui.element;

public interface GuiElementEvent {
    default boolean mouseClicked(int mouseX, int mouseY) {
        return false;
    }

    default boolean keyPressed(int keyCode) {
        return false;
    }

    default boolean isMouseOver(int mouseX, int mouseY) {
        return false;
    }

    void keyDown(int keyCode);
}
