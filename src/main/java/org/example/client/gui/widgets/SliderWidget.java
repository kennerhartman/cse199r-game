package org.example.client.gui.widgets;

import java.util.function.Consumer;

import org.example.client.gui.GuiGraphics;

import com.raylib.Colors;
import com.raylib.Raylib;

public class SliderWidget extends AbstractWidget {

    private float value;
    private final Consumer<Float> onValueChanged;
    private boolean dragging = false;
    private Runnable onRelease = null;

    public SliderWidget(
            int x, int y, int width, int height,
            float initialValue,
            Consumer<Float> onValueChanged
    ) {
        super(x, y, width, height);

        this.value = Math.max(0.0f, Math.min(1.0f, initialValue));
        this.onValueChanged = onValueChanged;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {

        // Draw the slider track
        int trackY = this.y + this.height / 2;

        Raylib.DrawRectangle(
                this.x,
                trackY - 3,
                this.width,
                6,
                Colors.DARKGRAY
        );

        // Draw the filled portion
        int filledWidth = (int) (this.width * this.value);

        if (filledWidth > 0) {
            Raylib.DrawRectangle(
                    this.x,
                    trackY - 3,
                    filledWidth,
                    6,
                    Colors.BLUE
            );
        }

        // Draw the slider handle
        int handleX = this.x + filledWidth;

        Raylib.DrawCircle(
                handleX,
                trackY,
                10,
                this.isHovered() || dragging
                        ? Colors.WHITE
                        : Colors.LIGHTGRAY
        );

        // Continue dragging while mouse is held and widget is active
        if (!this.isActive) {
            dragging = false;
        } else if (dragging) {
            if (Raylib.IsMouseButtonDown(Raylib.MOUSE_BUTTON_LEFT)) {
                updateValue(mouseX);
            } else {
                dragging = false;

                if (onRelease != null) {
                    onRelease.run();
                }
            }
        }
    }

    /** Called once when the user lets go of the slider. */
    public SliderWidget onRelease(Runnable onRelease) {
        this.onRelease = onRelease;
        return this;
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        dragging = true;
        updateValue(mouseX);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        if (!active) {
            this.dragging = false;
        }
    }

    private void updateValue(int mouseX) {
        if (!this.isActive) {
            return;
        }

        float newValue = (float) (mouseX - this.x) / this.width;

        newValue = Math.max(0.0f, Math.min(1.0f, newValue));

        if (newValue != this.value) {
            this.value = newValue;
            this.onValueChanged.accept(this.value);
        }
    }

    public float getValue() {
        return this.value;
    }
}