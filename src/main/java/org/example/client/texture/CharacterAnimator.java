package org.example.client.texture;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class CharacterAnimator {

    private final Map<AnimationType, AnimatedSprite> animations =
            new EnumMap<>(AnimationType.class);

    private final Random random = new Random();

    private AnimationType currentType = AnimationType.IDLE;
    private boolean actionComplete = true;

    public void addAnimation(
            AnimationType type,
            AnimatedSprite animation
    ) {
        animations.put(type, animation);
    }

    public void play(AnimationType type) {

        // Ignore animations this character doesn't have
        if (!animations.containsKey(type)) {
            return;
        }

        // Death cannot be interrupted
        if (currentType == AnimationType.DEATH) {
            return;
        }

        // Don't restart an animation that's already playing
        if (currentType == type) {
            return;
        }

        // Lower-priority animations cannot interrupt
        if (getPriority(type) < getPriority(currentType)) {
            return;
        }

        // Switch to the requested animation
        currentType = type;
        animations.get(type).reset();

        actionComplete = switch (type) {
            case IDLE, WALK -> true;
            default -> false;
        };
    }

    public void playRandomAttack() {

        if (currentType == AnimationType.DEATH) {
            return;
        }

        // Don't interrupt a higher-priority animation
        if (getPriority(currentType) > 2) {
            return;
        }

        List<AnimationType> available = new ArrayList<>();

        for (AnimationType type : List.of(
                AnimationType.ATTACK_1,
                AnimationType.ATTACK_2,
                AnimationType.ATTACK_3
        )) {
            if (animations.containsKey(type)) {
                available.add(type);
            }
        }

        if (available.isEmpty()) {
            return;
        }

        AnimationType selected =
                available.get(random.nextInt(available.size()));

        // Allow an attack to restart, even if selected twice
        currentType = selected;
        animations.get(selected).reset();

        actionComplete = false;
    }

    public void update(float deltaTime) {
        AnimatedSprite current = animations.get(currentType);

        if (current == null) {
            return;
        }

        current.update(deltaTime);

        // One-shot animations return to Idle
        if (current.isFinished()) {
            actionComplete = true;

            if (currentType != AnimationType.DEATH) {
                currentType = AnimationType.IDLE;
                animations.get(AnimationType.IDLE).reset();
            }
        }

        // Death remains on its final frame
        if (currentType == AnimationType.DEATH) {
            return;
        }
    }

    public void stopWalking() {
        if (currentType == AnimationType.WALK) {
            currentType = AnimationType.IDLE;
            animations.get(AnimationType.IDLE).reset();
        }
    }

    public void render(int x, int y, float scale) {
        AnimatedSprite current = animations.get(currentType);

        if (current != null) {
            current.render(x, y, scale);
        }
    }

    public AnimationType getCurrentType() {
        return currentType;
    }

    private int getPriority(AnimationType type) {
        return switch (type) {
            case IDLE -> 0;
            case WALK -> 1;

            case ATTACK_1, ATTACK_2, ATTACK_3 -> 2;

            case HURT -> 3;
            case DEATH -> 4;
        };
    }

    public boolean isActionComplete() {
        return actionComplete;
    }

    public boolean tryPlay(AnimationType type) {
        if (!animations.containsKey(type)) {
            return false;
        }

        if (currentType == AnimationType.DEATH) {
            return false;
        }

        if (currentType == type) {
            return false;
        }

        if (getPriority(type) < getPriority(currentType)) {
            return false;
        }

        currentType = type;
        animations.get(type).reset();

        actionComplete = switch (type) {
            case IDLE, WALK -> true;
            default -> false;
        };

        return true;
    }
}