package org.example.client;

import org.example.client.gui.Gui;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.screen.MainMenuScreen;

import com.raylib.Raylib;

public class Client {
    private static Client client;
    GuiGraphics graphics;
    public Raylib.Font font;

    public Gui gui;
    public InputHandler input;

    public Client() {
        Client.client = this;
        this.font = Raylib.GetFontDefault();
        this.gui = new Gui(this);
        this.graphics = new GuiGraphics(this);
        this.input = new InputHandler();
        this.gui.setScreen(new MainMenuScreen(Client.getInstance()));
    }

    protected void run() {
        this.input.handleInput();

        this.gui.render(this.graphics, this.input.getMouseX(), this.input.getMouseY());
    }

    public static Client getInstance() {
        return client;
    }
}
