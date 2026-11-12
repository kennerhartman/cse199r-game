package org.example.client.gui.widgets;

import java.util.List;

import com.raylib.Colors;
import com.raylib.Raylib;

import org.example.client.gui.GuiGraphics;
import org.example.client.texture.AnimatedSprite;

public class SpriteSelectorWidget extends AbstractWidget {

    private final List<AnimatedSprite> sprites;
    private int selectedIndex = 0;
    private final List<String> spriteIds;


    public SpriteSelectorWidget(
            int x,
            int y,
            int width,
            int height,
            List<AnimatedSprite> sprites,
            List<String> spriteIds
    ) {
        super(x, y, width, height);

        if (sprites == null || sprites.isEmpty()) {
            throw new IllegalArgumentException(
                    "Sprite selector requires at least one sprite"
            );
        }

        if (spriteIds == null || sprites.size() != spriteIds.size()) {
            throw new IllegalArgumentException(
                    "Every sprite must have an identifier"
            );
        }

        this.sprites = List.copyOf(sprites);
        this.spriteIds = List.copyOf(spriteIds);
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        AnimatedSprite currentSprite = sprites.get(selectedIndex);

        if (this.isActive) {
            currentSprite.update(Raylib.GetFrameTime());
        }

        // Draw current sprite centered in the widget
        float scale = Math.min(
                width / 100.0f,
                height / 100.0f
        );

        int spriteSize = Math.round(100 * scale);

        int spriteX = x + (width - spriteSize) / 2;
        int spriteY = y + (height - spriteSize) / 2;

        currentSprite.render(spriteX, spriteY, scale);
    }

    public void nextSprite() {
        selectedIndex = (selectedIndex + 1) % sprites.size();
        sprites.get(selectedIndex).reset();
    }

    public void previousSprite() {
        selectedIndex =
                (selectedIndex - 1 + sprites.size()) % sprites.size();

        sprites.get(selectedIndex).reset();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public AnimatedSprite getSelectedSprite() {
        return sprites.get(selectedIndex);
    }

    public String getSelectedSpriteId() {
        return spriteIds.get(selectedIndex);
    }
}