package org.example.client.map;

import org.example.client.map.renderer.MapCamera;

public class MapInputHandler {
    private static final float CLICK_THRESHOLD = 4f;

    private final MapView view;
    private final MapCamera camera;

    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private double pressX = 0;
    private double pressY = 0;

    private boolean pressed = false;

    public MapInputHandler(MapView view, MapCamera camera) {
        this.view = view;
        this.camera = camera;
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public void mouseClicked(double mouseX, double mouseY) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        this.pressX = mouseX;
        this.pressY = mouseY;
        this.pressed = true;
    }

    public void cancelPress() {
        this.pressed = false;
    }

    public void mouseDragged(double mouseX, double mouseY) {
        if (!this.pressed) {
            return;
        }

        double deltaX = mouseX - this.lastMouseX;
        double deltaY = mouseY - this.lastMouseY;

        if (deltaX != 0 || deltaY != 0) {
            this.camera.pan(deltaX, deltaY);

            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    public void mouseReleased(double mouseX, double mouseY) {
        if (!this.pressed) {
            return;
        }

        this.pressed = false;

        double movedX = mouseX - this.pressX;
        double movedY = mouseY - this.pressY;

        if (movedX * movedX + movedY * movedY > CLICK_THRESHOLD * CLICK_THRESHOLD) {
            return;
        }

        this.view.choiceAt(this.camera.toWorld(mouseX, mouseY)).ifPresent(this.view::select);
    }
}
