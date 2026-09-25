package org.example.client.gui;

import org.example.client.Client;
import org.example.util.Identifier;

import com.raylib.Colors;
import com.raylib.Raylib;

public class GuiGraphics {
    Client client;

    private final Raylib.Texture texture = Client.getInstance().guiTextures.getTexture(Identifier.ofDefault("gui/button.png"));

    public GuiGraphics(Client client) {
        this.client = client;
    }

    public void text(String text, int x, int y, Raylib.Color color) {
        Raylib.DrawText(text, x, y, 24, color);
    }

    public void blitNineSliced(int x, int y, int width, int height, int textureWidth, int textureHeight) {
        Raylib.NPatchInfo patchInfo = new Raylib.NPatchInfo()
                .source(new Raylib.Rectangle().x(0).y(0).width(textureWidth).height(textureHeight))
                .left(4)
                .top(4)
                .right(4)
                .bottom(4)
                .layout(Raylib.NPATCH_NINE_PATCH);

        Raylib.Rectangle dest = new Raylib.Rectangle()
                .x(x)
                .y(y)
                .width(width)
                .height(height);

        Raylib.Vector2 origin = new Raylib.Vector2().x(0).y(0);

        Raylib.DrawTextureNPatch(texture, patchInfo, dest, origin, 0.0f, Colors.WHITE);
    }

    public void border(int x, int y, int width, int height) {
        Raylib.DrawLineEx(
                new Raylib.Vector2().x(x).y(y),
                new Raylib.Vector2().x(x).y(y + height),
                3,
                Colors.WHITE
        );

        Raylib.DrawLineEx(
                new Raylib.Vector2().x(x).y(y),
                new Raylib.Vector2().x(x + width).y(y),
                3,
                Colors.WHITE
        );

        Raylib.DrawLineEx(
                new Raylib.Vector2().x(x + width).y(y),
                new Raylib.Vector2().x(x + width).y(y + height),
                3,
                Colors.WHITE
        );

        Raylib.DrawLineEx(
                new Raylib.Vector2().x(x).y(y + height),
                new Raylib.Vector2().x(x + width).y(y + height),
                3,
                Colors.WHITE
        );
    }

    public int getScreenWidth() {
        return (int) this.client.gui.getScreenWidth();
    }

    public int getScreenHeight() {
        return (int) this.client.gui.getScreenHeight();
    }
}
