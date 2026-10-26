package org.example.client;

import org.example.Player.Player;
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
    final GuiGraphics graphics;
    public final Raylib.Font font;
    public final Gui gui;
    public final InputHandler input;
    public final Player player;
    private boolean running;

    public Client() {
        Client.client = this;

        this.guiTextures = new TextureAtlas(Identifier.ofDefault("gui/widget"));
        this.guiTextures.prepare();
        this.font = Raylib.GetFontDefault();
        this.gui = new Gui(this);
        this.graphics = new GuiGraphics(this);
        this.input = new InputHandler();
        this.gui.setScreen(new MainMenuScreen(Client.getInstance()));
        // this.gui.setScreen(new SaveSlotScreen(Client.getInstance()));

        this.player = new Player();

        this.running = true;
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

    public boolean isRunning() {
        return this.running;
    }

    public void stop() {
        this.running = false;
    }

    public static Client getInstance() {
        return client;
    }
}
