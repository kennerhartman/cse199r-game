package org.example.client.gui;

import org.example.client.Client;
import org.example.client.texture.UploadedTexture;
import org.example.util.Identifier;

import com.raylib.Colors;
import com.raylib.Raylib;

public class GuiGraphics {
    Client client;

    private final UploadedTexture texture = Client.getInstance().guiTextures.getTexture(Identifier.ofDefault("button.png"));

    public GuiGraphics(Client client) {
        this.client = client;
    }

    public void text(String text, int x, int y, Raylib.Color color) {
        Raylib.DrawText(text, x, y, 24, color);
    }

    public void blitNineSliced(int x, int y, int width, int height, int textureWidth, int textureHeight) {
        int srcW, srcH, cLeft, cTop, cRight, cBottom, textureScale;

        if (this.texture.metadata() != null) {
            srcW = this.texture.metadata().width();
            srcH = this.texture.metadata().height();
            cLeft = this.texture.metadata().corner();
            cTop = this.texture.metadata().corner();
            cRight = this.texture.metadata().corner();
            cBottom = this.texture.metadata().corner();
            textureScale = this.texture.metadata().scale();
        } else {
            srcW = textureWidth;
            srcH = textureHeight;
            cLeft = 4;
            cTop = 4;
            cRight = 4;
            cBottom = 4;
            textureScale = 1;
        }

        int outLeft = Math.round(cLeft * textureScale);
        int outTop = Math.round(cTop * textureScale);
        int outRight = Math.round(cRight * textureScale);
        int outBottom = Math.round(cBottom * textureScale);

        float[] srcX = { 0, cLeft, srcW - cRight, srcW };
        float[] dstX = { x, x + outLeft, x + width - outRight, x + width };

        float[] srcY = { 0, cTop, srcH - cBottom, srcH };
        float[] dstY = { y, y + outTop, y + height - outBottom, y + height };

        Raylib.Vector2 origin = new Raylib.Vector2().x(0).y(0);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Raylib.Rectangle srcRec = new Raylib.Rectangle()
                        .x(srcX[col])
                        .y(srcY[row])
                        .width(srcX[col + 1] - srcX[col])
                        .height(srcY[row + 1] - srcY[row]);

                Raylib.Rectangle dstRec = new Raylib.Rectangle()
                        .x(dstX[col])
                        .y(dstY[row])
                        .width(dstX[col + 1] - dstX[col])
                        .height(dstY[row + 1] - dstY[row]);

                if (srcRec.width() > 0 && srcRec.height() > 0 && dstRec.width() > 0 && dstRec.height() > 0) {
                    Raylib.DrawTexturePro(this.texture.texture(), srcRec, dstRec, origin, 0.0f, Colors.WHITE);
                }
            }
        }
    }

    private final Raylib.Vector2 lineStart = new Raylib.Vector2();
    private final Raylib.Vector2 lineEnd = new Raylib.Vector2();

    public void border(int x, int y, int width, int height) {
        this.lineStart.x(x).y(y);
        this.lineEnd.x(x).y(y + height);

        Raylib.DrawLineEx(
                this.lineStart.x(x).y(y),
                this.lineEnd.x(x).y(y + height),
                3,
                Colors.WHITE
        );

        this.lineStart.x(x).y(y);
        this.lineEnd.x(x + width).y(y);

        Raylib.DrawLineEx(
                this.lineStart,
                this.lineEnd,
                3,
                Colors.WHITE
        );

        this.lineStart.x(x + width).y(y);
        this.lineEnd.x(x + width).y(y + height);

        Raylib.DrawLineEx(
                this.lineStart,
                this.lineEnd,
                3,
                Colors.WHITE
        );

        this.lineStart.x(x).y(y + height);
        this.lineEnd.x(x + width).y(y + height);

        Raylib.DrawLineEx(
                this.lineStart,
                this.lineEnd,
                3,
                Colors.WHITE
        );
    }
}
