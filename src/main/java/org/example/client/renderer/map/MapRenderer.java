package org.example.client.renderer.map;

import org.example.client.gui.Gui;
import org.example.client.gui.GuiGraphics;
import org.example.util.Identifier;

import com.raylib.Raylib;

public class MapRenderer {
    private final Identifier mapId;

    public Raylib.Camera2D mapCamera = new Raylib.Camera2D();
    private float cameraZoom = 1f;
    private final int mapWidth = 724;
    private final int mapHeight = 357;

    private double lastMouseX = 0;
    private double lastMouseY = 0;

    public MapRenderer() {
        this.mapId = Identifier.ofDefault("map_tile_1");

        this.mapCamera.offset(new Raylib.Vector2().x(Gui.VIRTUAL_WIDTH / 2.0f).y(Gui.VIRTUAL_HEIGHT / 2.0f));

        this.mapCamera.target(new Raylib.Vector2().x(mapWidth / 2.0f).y(mapHeight / 2.0f));

        this.mapCamera.rotation(0.0f);
        this.mapCamera.zoom(this.cameraZoom);
    }

    public void render(GuiGraphics graphics) {
        Raylib.BeginScissorMode(0, 0, Gui.VIRTUAL_WIDTH, Gui.VIRTUAL_HEIGHT);

        Raylib.BeginMode2D(this.mapCamera);

        this.renderFrame(graphics);

        graphics.blit(
                Identifier.of(this.mapId.namespace, "pieces/" + this.mapId.path + ".png"),
                0,
                0,
                0,
                0
        );

        Raylib.EndMode2D();
        Raylib.EndScissorMode();
    }

    private void renderFrame(GuiGraphics graphics) {
        int frameWidth = this.mapWidth + 64;
        int frameHeight = this.mapHeight + 64;
        int frameX = (this.mapWidth - frameWidth) / 2;
        int frameY = (this.mapHeight - frameHeight) / 2;

        graphics.blitNineSliced(
                GuiGraphics.TexturePipeline.MAP,
                Identifier.ofDefault("map_background.png"),
                frameX,
                frameY,
                frameWidth,
                frameHeight,
                64,
                64
        );
    }

    public void mouseClicked(double mouseX, double mouseY) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    public void mouseDragged(double mouseX, double mouseY) {
        double deltaX = mouseX - this.lastMouseX;
        double deltaY = mouseY - this.lastMouseY;

        if (deltaX != 0 || deltaY != 0) {
            float newX = (float) (this.mapCamera.target().x() - deltaX);
            float newY = (float) (this.mapCamera.target().y() - deltaY);

            float zoomedScreenWidth = Gui.VIRTUAL_WIDTH / this.mapCamera.zoom();
            float zoomedScreenHeight = Gui.VIRTUAL_HEIGHT / this.mapCamera.zoom();

            float minX, maxX;
            float minY, maxY;

            if (zoomedScreenWidth >= this.mapWidth) {
                float overpanX = (zoomedScreenWidth - this.mapWidth) * 0.5f;

                minX = (this.mapWidth / 2.0f) - overpanX;
                maxX = (this.mapWidth / 2.0f) + overpanX;
            } else {
                minX = zoomedScreenWidth / 2.0f;
                maxX = this.mapWidth - (zoomedScreenWidth / 2.0f);
            }

            if (zoomedScreenHeight >= this.mapHeight) {
                float overpanY = (zoomedScreenHeight - this.mapHeight) * 0.5f;

                minY = (this.mapHeight / 2.0f) - overpanY;
                maxY = (this.mapHeight / 2.0f) + overpanY;
            } else {
                minY = zoomedScreenHeight / 2.0f;
                maxY = this.mapHeight - (zoomedScreenHeight / 2.0f);
            }

            if (newX < minX) {
                newX = minX;
            }

            if (newX > maxX) {
                newX = maxX;
            }


            if (newY < minY) {
                newY = minY;
            }

            if (newY > maxY) {
                newY = maxY;
            }

            this.mapCamera.target().x(newX).y(newY);

            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    public void mouseReleased(double mouseX, double mouseY) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }
}