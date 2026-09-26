package org.example.client.gui.screen;

import java.util.ArrayList;
import java.util.List;

import org.example.client.Client;
import org.example.client.gui.element.Element;
import org.example.client.gui.element.GuiElementEvent;
import org.example.client.gui.GuiGraphics;

public abstract class Screen {
    Client client;
    int width;
    int height;
    List<Element> elements = new ArrayList<>();

    public Screen(Client client) {
        this.client = client;
    }

    public final void init(int width, int height) {
        this.width = width;
        this.height = height;

        this.init();
    }

    public abstract void init();

    public void resize() {
        this.elements.clear();
        this.init(this.client.gui.getScreenWidth(), this.client.gui.getScreenHeight());
    }

    public void renderBackground(GuiGraphics graphics) {

    }

    public void renderElements(GuiGraphics graphics, int mouseX, int mouseY) {
        for (Element element : this.elements) {
            element.extractRenderState(graphics, mouseX, mouseY);
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        this.renderBackground(graphics);
        this.renderElements(graphics, mouseX, mouseY);
    }

    public void clearElements() {
        this.elements.clear();
    }

    public void onClose() {
    }

    public void mouseClicked(int mouseX, int mouseY) {
        for (Element element : this.elements) {
            if (element instanceof GuiElementEvent event) {
                event.mouseClicked(mouseX, mouseY);
            }
        }
    }
}
