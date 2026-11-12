package org.example.client.texture;

import com.raylib.Colors;
import com.raylib.Raylib;

import java.io.IOException;
import java.io.InputStream;

public class SpriteSheet {

    private static final int FRAME_SIZE = 100;

    private final Raylib.Texture texture;
    private final int frameCount;

    public SpriteSheet(String resourcePath) {

        try (InputStream stream =
                     SpriteSheet.class.getClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (stream == null) {
                throw new IllegalArgumentException(
                        "Sprite sheet not found: " + resourcePath
                );
            }

            byte[] bytes = stream.readAllBytes();

            Raylib.Image image = Raylib.LoadImageFromMemory(
                    ".png",
                    bytes,
                    bytes.length
            );

            if (image.width() % FRAME_SIZE != 0 ||
                    image.height() != FRAME_SIZE) {

                Raylib.UnloadImage(image);

                throw new IllegalArgumentException(
                        "Invalid sprite sheet dimensions: " + resourcePath
                );
            }

            this.frameCount = image.width() / FRAME_SIZE;

            this.texture = Raylib.LoadTextureFromImage(image);

            Raylib.UnloadImage(image);

            Raylib.SetTextureFilter(
                    this.texture,
                    Raylib.TEXTURE_FILTER_POINT
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load sprite sheet: " + resourcePath,
                    e
            );
        }
    }

    public void drawFrame(int frame, int x, int y, float scale) {

        frame = Math.floorMod(frame, frameCount);

        Raylib.Rectangle source = new Raylib.Rectangle()
                .x(frame * FRAME_SIZE)
                .y(0)
                .width(FRAME_SIZE)
                .height(FRAME_SIZE);

        Raylib.Rectangle destination = new Raylib.Rectangle()
                .x(x)
                .y(y)
                .width(FRAME_SIZE * scale)
                .height(FRAME_SIZE * scale);

        Raylib.DrawTexturePro(
                texture,
                source,
                destination,
                new Raylib.Vector2().x(0).y(0),
                0.0f,
                Colors.WHITE
        );
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void close() {
        Raylib.UnloadTexture(texture);
    }
}