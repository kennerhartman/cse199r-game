
package org.example.client.animation;

import java.util.ArrayDeque;
import java.util.Queue;

import org.example.client.texture.AnimationType;
import org.example.client.texture.CharacterAnimator;

public class CombatAnimationQueue {

    private final Queue<AnimationAction> actions =
            new ArrayDeque<>();

    private AnimationAction currentAction = null;

    private boolean playing = false;

    public void queue(
            CharacterAnimator animator,
            AnimationType type
    ) {
        actions.add(new AnimationAction(animator, type));
    }

    public void start() {
        if (playing) {
            return;
        }

        playing = true;
        playNext();
    }

    public void update() {
        if (!playing || currentAction == null) {
            return;
        }

        if (currentAction.animator().isActionComplete()) {
            playNext();
        }
    }

    private void playNext() {
        currentAction = actions.poll();

        if (currentAction == null) {
            playing = false;
            return;
        }

        currentAction.animator().play(currentAction.type());
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isFinished() {
        return !playing && actions.isEmpty();
    }

    public void clear() {
        actions.clear();
        currentAction = null;
        playing = false;
    }

    private record AnimationAction(
            CharacterAnimator animator,
            AnimationType type
    ) {}
}
