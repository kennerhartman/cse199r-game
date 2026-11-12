
package org.example.client.gui.screen;

import org.example.client.Client;
import org.example.client.gui.widgets.ButtonWidget;
import org.example.client.gui.widgets.BorderWidget;
import org.example.client.gui.widgets.SliderWidget;
import org.example.client.gui.widgets.TextWidget;
import org.example.client.gui.widgets.DynamicTextWidget;

public class SettingsScreen extends Screen {

    private final Screen previousScreen;

    public SettingsScreen(Client client, Screen previousScreen) {
        super(client);
        this.previousScreen = previousScreen;
    }

    @Override
    public void init() {
        int borderWidth = 350;
        int borderHeight = 300;

        int borderX = this.width / 2 - borderWidth / 2;
        int borderY = this.height / 2 - borderHeight / 2;

        // Title
        this.addElement(
                new TextWidget(
                        "Settings",
                        borderX + 125,
                        borderY + 20
                )
        );

        // Music volume label
        this.addElement(
                new TextWidget(
                        "Music Volume",
                        borderX + 40,
                        borderY + 65
                )
        );

//        Music Volume Percentage
        this.addElement(
                new DynamicTextWidget(
                        borderX + 260,
                        borderY + 65,
                        () -> Math.round(
                                this.client.settings.getMusicVolume() * 100
                        ) + "%"
                )
        );

        // Music volume slider
        this.addElement(
                new SliderWidget(
                        borderX + 40,
                        borderY + 95,
                        270,
                        30,
                        this.client.settings.getMusicVolume(),
                        volume -> {
                            this.client.settings.setMusicVolume(volume);
                            this.client.musicManager.setVolume(volume);
                        }
                )
        );

        // Sound effects volume label
        this.addElement(
                new TextWidget(
                        "Sound Effects",
                        borderX + 40,
                        borderY + 140
                )
        );

//        Sound effects volume percentage
        this.addElement(
                new DynamicTextWidget(
                        borderX + 260,
                        borderY + 140,
                        () -> Math.round(
                                this.client.settings.getSoundVolume() * 100
                        ) + "%"
                )
        );

        // Sound effects slider
        this.addElement(
                new SliderWidget(
                        borderX + 40,
                        borderY + 170,
                        270,
                        30,
                        this.client.settings.getSoundVolume(),
                        volume -> {
                            this.client.settings.setSoundVolume(volume);
                            this.client.soundManager.setVolume(volume);
                        }
                )
        );

        // Back button
        this.addElement(
                new ButtonWidget(
                        "Back",
                        borderX + (borderWidth - 125) / 2,
                        borderY + 230,
                        125,
                        45,
                        () -> {
                            this.client.settings.save();
                            this.client.gui.setScreen(previousScreen);
                        }
                )
        );

        // Panel border
        this.addElement(
                new BorderWidget(
                        borderX,
                        borderY,
                        borderWidth,
                        borderHeight
                )
        );
    }
}
