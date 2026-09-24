package org.example.client.gui.widgets;

import org.example.client.gui.GuiGraphics;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class TextWidget extends AbstractWidget {
    private final String text;

    public TextWidget(String text, int x, int y) {
        GlyphLayout layout = new GlyphLayout();
        BitmapFont font = new BitmapFont();
        layout.setText(font, text);

        super(x, y, (int) layout.width, (int) layout.height);

        this.text = text;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        int color;

        if (this.isHovered()) {
            color = -32513;
        } else {
            color = -1;
        }

        graphics.text(
                this.text,
                this.getX(),
                this.getY(),
                color
        );
    }
}
