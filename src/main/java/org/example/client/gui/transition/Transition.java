package org.example.client.gui.transition;

import org.example.client.Client;
import org.example.client.gui.GuiGraphics;

public abstract class Transition {
    final double duration;
    final Runnable action;

    protected double currentElapsedTime = 0;

    public Transition(double duration, Runnable action) {
        this.duration = duration;
        this.action = action;

        this.setupTransition();
    }

    protected void setupTransition() {
        Client.getInstance().input.lock(true);
    }

    public void animation(GuiGraphics graphics, float deltaTime) {
        this.currentElapsedTime += deltaTime;
    }

    public boolean completed() {
        return this.currentElapsedTime >= this.duration;
    }

    public void cleanupTransition() {
        this.action.run();

        Client.getInstance().input.lock(false);
    }
}
