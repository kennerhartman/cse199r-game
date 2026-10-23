package org.example.client.gui.screen;

import java.util.ArrayList;
import java.util.List;

import org.example.client.Client;
import org.example.client.gui.element.Element;
import org.example.client.gui.element.GuiElementEvent;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.screen.overlay.ScreenOverlay;

public abstract class Screen {
    protected Client client;
    protected int width;
    protected int height;
    protected List<Element> elements = new ArrayList<>();

    public Screen(Client client) {
        this.client = client;
    }

    public final void init(int width, int height) {
        this.width = width;
        this.height = height;

        this.init();

        if (this.client.gui.screenOverlay != null && !(this instanceof ScreenOverlay)) {
            for (Element element : this.elements) {
                element.setActive(false);
            }
        }
    }

    public abstract void init();

    public void resize() {
        this.elements.clear();
        this.init(850, 500);
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

    public void addElement(Element element) {
        this.elements.add(element);
    }

    public List<Element> getElements() {
        return this.elements;
    }

    public void clearElements() {
        this.elements.clear();
    }

    public void onClose() {
    }

    public void mouseClicked(int mouseX, int mouseY) {
        List<Element> elements = new ArrayList<>(this.elements);

        if (this.client.gui.screenOverlay != null) {
            for (Element element : this.client.gui.screenOverlay.getElements()) {
                elements.addFirst(element);
            }
        }

        for (Element element : elements) {
            if (element instanceof GuiElementEvent event) {
                boolean clicked = event.mouseClicked(mouseX, mouseY);

                if (clicked) {
                    break;
                }
            }
        }
    }

    public void keyPressed(int keyCode) {
        for (Element element : new ArrayList<>(this.elements)) {
            if (element instanceof GuiElementEvent event) {
                event.keyPressed(keyCode);
            }
        }
    }

    public void keyDown(int keyCode) {
        for (Element element : new ArrayList<>(this.elements)) {
            if (element instanceof GuiElementEvent event) {
                event.keyDown(keyCode);
            }
        }
    }
}
