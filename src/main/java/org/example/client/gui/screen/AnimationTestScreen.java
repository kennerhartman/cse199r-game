
package org.example.client.gui.screen;

import com.raylib.Raylib;

import org.example.client.Client;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.widgets.ButtonWidget;
import org.example.client.gui.widgets.TextWidget;

import org.example.client.texture.AnimationType;
import org.example.client.texture.CharacterAnimator;
import org.example.client.texture.CharacterAnimationFactory;
import org.example.client.animation.CombatAnimationQueue;

public class AnimationTestScreen extends Screen {

    private final Screen parent;

    private CharacterAnimator knightAnimator;
    private CharacterAnimator orcAnimator;
    private CharacterAnimator currentAnimator;
    private final CombatAnimationQueue animationQueue =
            new CombatAnimationQueue();

    private String currentCharacter = "Knight";

    public AnimationTestScreen(Client client, Screen parent) {
        super(client);
        this.parent = parent;

        // Create independent animation controllers.
        // SpriteManager reuses the underlying textures.
        this.knightAnimator =
                CharacterAnimationFactory.createKnight(
                        client.spriteManager
                );

        this.orcAnimator =
                CharacterAnimationFactory.createOrc(
                        client.spriteManager
                );

        this.currentAnimator = knightAnimator;
    }

    @Override
    public void init() {

        // Back button
        this.addElement(
                new ButtonWidget(
                        "Back",
                        15,
                        15,
                        100,
                        40,
                        () -> this.client.gui.setScreen(parent)
                )
        );

        // Character selection
        this.addElement(
                new ButtonWidget(
                        "Knight",
                        this.width / 2 - 130,
                        55,
                        120,
                        40,
                        () -> selectCharacter("Knight")
                )
        );

        this.addElement(
                new ButtonWidget(
                        "Orc",
                        this.width / 2 + 10,
                        55,
                        120,
                        40,
                        () -> selectCharacter("Orc")
                )
        );

        // Animation controls
        int buttonY = 365;
        int buttonWidth = 125;
        int spacing = 10;

        int totalWidth = (buttonWidth * 5) + (spacing * 4);
        int startX = (this.width - totalWidth) / 2;

        this.addElement(
                new ButtonWidget(
                        "Idle",
                        startX,
                        buttonY,
                        buttonWidth,
                        45,
                        () -> currentAnimator.play(AnimationType.IDLE)
                )
        );

        this.addElement(
                new ButtonWidget(
                        "Walk",
                        startX + (buttonWidth + spacing),
                        buttonY,
                        buttonWidth,
                        45,
                        () -> currentAnimator.play(AnimationType.WALK)
                )
        );

        this.addElement(
                new ButtonWidget(
                        "Attack",
                        startX + 2 * (buttonWidth + spacing),
                        buttonY,
                        buttonWidth,
                        45,
                        () -> currentAnimator.playRandomAttack()
                )
        );

        this.addElement(
                new ButtonWidget(
                        "Hurt",
                        startX + 3 * (buttonWidth + spacing),
                        buttonY,
                        buttonWidth,
                        45,
                        () -> currentAnimator.play(AnimationType.HURT)
                )
        );

        this.addElement(
                new ButtonWidget(
                        "Death",
                        startX + 4 * (buttonWidth + spacing),
                        buttonY,
                        buttonWidth,
                        45,
                        () -> currentAnimator.play(AnimationType.DEATH)
                )
        );

//        Testing Sequence
        this.addElement(
                new ButtonWidget(
                        "Test Sequence",
                        this.width / 2 - 90,
                        420,
                        180,
                        45,
                        this::testCombatSequence
                )
        );

        // Reset button
        this.addElement(
                new ButtonWidget(
                        "Reset",
                        this.width / 2 - 60,
                        470,
                        120,
                        40,
                        this::resetAnimator
                )
        );
    }

    private void selectCharacter(String character) {
        this.currentCharacter = character;

        if (character.equals("Knight")) {
            this.currentAnimator = knightAnimator;
        } else {
            this.currentAnimator = orcAnimator;
        }

        resetAnimator();
    }

    private void resetAnimator() {
        // Recreate the animator so Death is no longer locked.
        if (currentCharacter.equals("Knight")) {
            knightAnimator =
                    CharacterAnimationFactory.createKnight(
                            client.spriteManager
                    );

            currentAnimator = knightAnimator;
        } else {
            orcAnimator =
                    CharacterAnimationFactory.createOrc(
                            client.spriteManager
                    );

            currentAnimator = orcAnimator;
        }
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        super.render(graphics, mouseX, mouseY);

        float deltaTime = Raylib.GetFrameTime();

        knightAnimator.update(deltaTime);
        orcAnimator.update(deltaTime);

        animationQueue.update();

        knightAnimator.render(
                this.width / 2 - 250,
                140,
                2.0f
        );

        orcAnimator.render(
                this.width / 2 + 50,
                140,
                2.0f
        );

        graphics.text(
                "Character: " + currentCharacter,
                15,
                105,
                com.raylib.Colors.WHITE
        );

        graphics.text(
                "Animation: " + knightAnimator.getCurrentType(),
                15,
                135,
                com.raylib.Colors.WHITE
        );
    }

    private void testCombatSequence() {
        if (animationQueue.isPlaying()) {
            return;
        }

        // Reset both animators for a fresh test
        knightAnimator = CharacterAnimationFactory.createKnight(
                client.spriteManager
        );

        orcAnimator = CharacterAnimationFactory.createOrc(
                client.spriteManager
        );

        animationQueue.clear();

        animationQueue.queue(knightAnimator, AnimationType.ATTACK_1);
        animationQueue.queue(orcAnimator, AnimationType.HURT);
        animationQueue.queue(knightAnimator, AnimationType.ATTACK_2);
        animationQueue.queue(orcAnimator, AnimationType.DEATH);

        animationQueue.start();
    }
}
