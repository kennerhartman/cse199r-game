package org.example.client.gui;

import org.example.client.Client;
import org.example.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import com.raylib.Colors;
import com.raylib.Raylib;
import com.raylib.Raylib.Vector2;
import com.raylib.Raylib.Rectangle;

public class Gui {
    public Client client;

    @Nullable
    public Screen screen;

    public static final int VIRTUAL_WIDTH = 850;
    public static final int VIRTUAL_HEIGHT = 500;

    private final Raylib.RenderTexture target;

    public Gui(Client client) {
        this.client = client;
        this.target = Raylib.LoadRenderTexture(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        float screenWidth = (float) this.getScreenWidth();
        float screenHeight = (float) this.getScreenHeight();

        float scale = Math.min(screenWidth / VIRTUAL_WIDTH, screenHeight / VIRTUAL_HEIGHT);

        int destWidth = (int) (VIRTUAL_WIDTH * scale);
        int destHeight = (int) (VIRTUAL_HEIGHT * scale);
        int destX = (int) ((screenWidth - destWidth) / 2);
        int destY = (int) ((screenHeight - destHeight) / 2);

        int virtualMouseX = (int) ((mouseX - destX) / scale);
        int virtualMouseY = (int) ((mouseY - destY) / scale);

        Raylib.BeginTextureMode(target);
        Raylib.ClearBackground(Colors.BLACK);

        if (this.screen != null) {
            this.screen.render(graphics, virtualMouseX, virtualMouseY);
        }

        Raylib.EndTextureMode();

        Raylib.BeginDrawing();
        Raylib.ClearBackground(Colors.BLACK);

        Rectangle sourceRec = new Rectangle().x(0).y(0).width(VIRTUAL_WIDTH).height(-VIRTUAL_HEIGHT);
        Rectangle destRec = new Rectangle().x(destX).y(destY).width(destWidth).height(destHeight);
        Vector2 origin = new Vector2().x(0).y(0);

        Raylib.DrawTexturePro(target.texture(), sourceRec, destRec, origin, 0.0f, Colors.WHITE);

        Raylib.EndDrawing();
    }

    public void setScreen(@Nullable Screen screen) {
        this.screen = screen;

        if (screen != null) {
            screen.clearElements();
            screen.init(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }
    }

    public int getScreenWidth() {
        return Raylib.GetScreenWidth();
    }

    public int getScreenHeight() {
        return Raylib.GetScreenHeight();
    }

    public float getScale() {
        float screenWidth = (float) Client.getInstance().gui.getScreenWidth();
        float screenHeight = (float) Client.getInstance().gui.getScreenHeight();

        return Math.min(screenWidth / 850, screenHeight / 500);
    }
}