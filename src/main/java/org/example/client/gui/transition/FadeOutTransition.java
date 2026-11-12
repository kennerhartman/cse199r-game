package org.example.client.gui.transition;

import org.example.client.gui.Gui;
import org.example.client.gui.GuiGraphics;

import com.raylib.Raylib;

public class FadeOutTransition extends Transition {
    public FadeOutTransition(double duration, Runnable action) {
        super(duration, action);
    }

    @Override
    public void animation(GuiGraphics graphics, float deltaTime) {
        super.animation(graphics, deltaTime);

        float progress = (float) Math.min(this.currentElapsedTime / this.duration, 1.0f);

        byte colorInt = (byte) 0;
        int alphaInt = (int) (progress * 255);

        Raylib.Color color = new Raylib.Color()
                .r(colorInt)
                .g(colorInt)
                .b(colorInt)
                .a((byte) alphaInt);

        graphics.rectangle(0, 0, Gui.VIRTUAL_WIDTH, Gui.VIRTUAL_HEIGHT, color);
    }
}
