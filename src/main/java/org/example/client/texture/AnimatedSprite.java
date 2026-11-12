package org.example.client.texture;

public class AnimatedSprite {

    private final SpriteSheet sheet;
    private final float frameDuration;
    private final boolean looping;

    private float elapsedTime = 0.0f;
    private int currentFrame = 0;
    private boolean finished = false;

    public AnimatedSprite(
            SpriteSheet sheet,
            float framesPerSecond,
            boolean looping
    ) {
        if (framesPerSecond <= 0) {
            throw new IllegalArgumentException(
                    "Frames per second must be positive"
            );
        }

        this.sheet = sheet;
        this.frameDuration = 1.0f / framesPerSecond;
        this.looping = looping;
    }

    // Keep compatibility with existing code
    public AnimatedSprite(SpriteSheet sheet, float framesPerSecond) {
        this(sheet, framesPerSecond, true);
    }

    public void update(float deltaTime) {
        if (finished) {
            return;
        }

        elapsedTime += deltaTime;

        while (elapsedTime >= frameDuration) {
            elapsedTime -= frameDuration;

            if (currentFrame < sheet.getFrameCount() - 1) {
                currentFrame++;
            } else if (looping) {
                currentFrame = 0;
            } else {
                finished = true;
                break;
            }
        }
    }

    public void render(int x, int y, float scale) {
        sheet.drawFrame(currentFrame, x, y, scale);
    }

    public void reset() {
        currentFrame = 0;
        elapsedTime = 0.0f;
        finished = false;
    }

    public boolean isFinished() {
        return finished;
    }
}