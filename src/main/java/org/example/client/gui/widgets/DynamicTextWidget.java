package org.example.client.gui.widgets;

import java.util.function.Supplier;

import com.raylib.Colors;
import org.example.client.gui.GuiGraphics;

public class DynamicTextWidget extends AbstractWidget {

    private final Supplier<String> textSupplier;

    public DynamicTextWidget(
            int x,
            int y,
            Supplier<String> textSupplier
    ) {
        super(x, y, 60, 30);
        this.textSupplier = textSupplier;
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        String text = textSupplier.get();

        int textWidth = GuiGraphics.getTextWidth(text);

        graphics.text(
                text,
                this.x + this.width - textWidth,
                this.y,
                Colors.WHITE
        );
    }
}