package org.example.client.gui.widgets;

import com.raylib.Colors;
import com.raylib.Raylib;

import org.example.client.gui.GuiGraphics;
import org.example.client.texture.AnimatedSprite;

public class SpriteWidget extends AbstractWidget {

    private final AnimatedSprite sprite;
    private final Runnable onSelect;

    private boolean selected = false;

    public SpriteWidget(
            int x,
            int y,
            int width,
            int height,
            AnimatedSprite sprite,
            Runnable onSelect
    ) {
        super(x, y, width, height);

        this.sprite = sprite;
        this.onSelect = onSelect;
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        // Update the animation
        if (this.isActive) {
            sprite.update(Raylib.GetFrameTime());
        }

        // Draw a highlight around the selected sprite
        if (selected) {
            Raylib.DrawRectangleLinesEx(
                    new Raylib.Rectangle()
                            .x(x)
                            .y(y)
                            .width(width)
                            .height(height),
                    3,
                    Colors.GOLD
            );
        } else if (this.isHovered()) {
            Raylib.DrawRectangleLinesEx(
                    new Raylib.Rectangle()
                            .x(x)
                            .y(y)
                            .width(width)
                            .height(height),
                    2,
                    Colors.LIGHTGRAY
            );
        }

        // Draw the sprite
        sprite.render(x, y, width / 100.0f);
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}