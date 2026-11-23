package org.example.client.map.renderer;

import org.example.client.gui.Gui;
import org.example.client.map.MapView;

import com.raylib.Raylib;

public class MapCamera {
    private final Raylib.Camera2D camera = new Raylib.Camera2D();

    public MapCamera() {
        this.camera.offset(new Raylib.Vector2().x(Gui.VIRTUAL_WIDTH / 2.0f).y(Gui.VIRTUAL_HEIGHT / 2.0f));
        this.camera.target(new Raylib.Vector2().x(MapView.MAP_WIDTH / 2.0f).y(MapView.MAP_HEIGHT / 2.0f));
        this.camera.rotation(0.0f);
        this.camera.zoom(1.0f);
    }

    public Raylib.Camera2D raw() {
        return this.camera;
    }

    public void pan(double deltaX, double deltaY) {
        float zoom = this.camera.zoom();

        float x = clampAxis((float) (this.camera.target().x() - deltaX / zoom), Gui.VIRTUAL_WIDTH / zoom, MapView.MAP_WIDTH);
        float y = clampAxis((float) (this.camera.target().y() - deltaY / zoom), Gui.VIRTUAL_HEIGHT / zoom, MapView.MAP_HEIGHT);

        this.camera.target().x(x).y(y);
    }

    private static float clampAxis(float target, float viewSize, float mapSize) {
        float halfMap = mapSize / 2.0f;
        float halfView = viewSize / 2.0f;

        if (viewSize >= mapSize) {
            float overpan = halfView - halfMap;

            return Math.clamp(target, halfMap - overpan, halfMap + overpan);
        }

        return Math.clamp(target, halfView, mapSize - halfView);
    }

    public Raylib.Vector2 toWorld(double virtualX, double virtualY) {
        Raylib.Vector2 screen = new Raylib.Vector2().x((float) virtualX).y((float) virtualY);

        return Raylib.GetScreenToWorld2D(screen, this.camera);
    }
}
