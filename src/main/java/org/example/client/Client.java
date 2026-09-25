package org.example.client;

import org.example.client.gui.Gui;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.screen.MainMenuScreen;
import org.example.client.gui.screen.Screen;
import org.example.client.texture.TextureAtlas;
import org.example.util.Identifier;

import com.raylib.Raylib;

public class Client {
    private static Client client;

    public final TextureAtlas guiTextures;
    GuiGraphics graphics;
    public Raylib.Font font;
    public Gui gui;
    public InputHandler input;

    public Client() {
        Client.client = this;

        this.guiTextures = new TextureAtlas(Identifier.ofDefault("gui"));
        this.guiTextures.prepare();
        this.font = Raylib.GetFontDefault();
        this.gui = new Gui(this);
        this.graphics = new GuiGraphics(this);
        this.input = new InputHandler();
        this.gui.setScreen(new MainMenuScreen(Client.getInstance()));
    }

    protected void run() {
        this.input.handleInput();

        if (Raylib.IsWindowResized()) {
            Screen screen = this.gui.screen;

            if (screen != null) {
                screen.resize();
            }
        }

        this.gui.render(this.graphics, this.input.getMouseX(), this.input.getMouseY());
    }

    public static Client getInstance() {
        return client;
    }
}
