package org.example.client;

import org.example.client.gui.Gui;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.screen.MainMenuScreen;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Client extends ApplicationAdapter {
    private static Client client;
    GuiGraphics graphics;
    public BitmapFont font;

    public Gui gui;
    public InputHandler input;

    public Client() {
        Client.client = this;
    }

    @Override
    public void create() {
        this.font = new BitmapFont();
        this.gui = new Gui(this);
        this.graphics = new GuiGraphics(this);
        this.input = new InputHandler();

        Gdx.input.setInputProcessor(this.input);
        this.gui.setScreen(new MainMenuScreen(Client.getInstance()));
    }

    private void tick() {
        this.input.handleInput();

        this.gui.render(this.graphics, this.input.getMouseX(), this.input.getMouseY());
    }

    public static Client getInstance() {
        return client;
    }

    @Override
    public void render() {
        this.tick();
    }
}
