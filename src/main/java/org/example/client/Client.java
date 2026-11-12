package org.example.client;

import org.example.Player.Player;
import org.example.client.gui.Gui;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.screen.MainMenuScreen;
import org.example.client.gui.screen.Screen;
import org.example.client.texture.TextureAtlas;
import org.example.map.MapManager;
import org.example.util.Identifier;
import org.example.client.GameSettings;
import org.example.client.audio.MusicManager;
import org.example.client.audio.MusicManager.MusicType;
import org.example.client.audio.SoundManager;
import org.example.client.texture.SpriteManager;

import com.raylib.Raylib;

public class Client {
    private static Client client;

    public final TextureAtlas guiTextures;
    final GuiGraphics graphics;
    public final Raylib.Font font;
    public final Gui gui;
    public final InputHandler input;
    public final Player player;
    public final MapManager mapManager;
    public final MusicManager musicManager;
    public final SoundManager soundManager;
    public final SpriteManager spriteManager;
    public final GameSettings settings = new GameSettings();
    private boolean running;

    public Client() {
        Client.client = this;
        this.settings.load();

        this.guiTextures = new TextureAtlas(Identifier.ofDefault("gui/widget"));
        this.guiTextures.prepare();
        this.font = Raylib.GetFontDefault();
        this.gui = new Gui(this);
        this.graphics = new GuiGraphics(this);
        this.input = new InputHandler();

        this.player = new Player();
        this.mapManager = new MapManager();
        this.spriteManager = new SpriteManager();

        Raylib.InitAudioDevice();

        this.musicManager = new MusicManager();
        this.musicManager.setVolume(this.settings.getMusicVolume());

        this.soundManager = new SoundManager();
        this.soundManager.setVolume(this.settings.getSoundVolume());

        this.gui.setScreen(new MainMenuScreen(Client.getInstance()));

        this.musicManager.play(MusicType.MENU);
        // this.gui.setScreen(new SaveSlotScreen(Client.getInstance()));

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

        this.musicManager.update();

        this.gui.render(this.graphics, this.input.getMouseX(), this.input.getMouseY());
    }

    public boolean isRunning() {
        return this.running;
    }

    public void stop() {
        this.running = false;
    }

    public void close(){
        this.settings.save();

        this.spriteManager.close();
        this.soundManager.close();
        this.musicManager.close();

        Raylib.CloseAudioDevice();
    }

    public static Client getInstance() {
        return client;
    }
}
