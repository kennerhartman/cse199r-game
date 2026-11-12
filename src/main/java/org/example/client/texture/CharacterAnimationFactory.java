package org.example.client.texture;

public class CharacterAnimationFactory {

    public static CharacterAnimator createKnight(SpriteManager manager) {
        CharacterAnimator animator = new CharacterAnimator();

        animator.addAnimation(
                AnimationType.IDLE,
                new AnimatedSprite(
                        manager.getSheet("sprites/knight/Soldier_Idle.png"),
                        8.0f,
                        true
                )
        );

        animator.addAnimation(
                AnimationType.WALK,
                new AnimatedSprite(
                        manager.getSheet("sprites/knight/Soldier_Walk.png"),
                        10.0f,
                        true
                )
        );

        animator.addAnimation(
                AnimationType.ATTACK_1,
                new AnimatedSprite(
                        manager.getSheet("sprites/knight/Soldier_Attack01.png"),
                        12.0f,
                        false
                )
        );

        animator.addAnimation(
                AnimationType.ATTACK_2,
                new AnimatedSprite(
                        manager.getSheet("sprites/knight/Soldier_Attack02.png"),
                        12.0f,
                        false
                )
        );

        animator.addAnimation(
                AnimationType.ATTACK_3,
                new AnimatedSprite(
                        manager.getSheet("sprites/knight/Soldier_Attack03.png"),
                        12.0f,
                        false
                )
        );

        animator.addAnimation(
                AnimationType.HURT,
                new AnimatedSprite(
                        manager.getSheet("sprites/knight/Soldier_Hurt.png"),
                        10.0f,
                        false
                )
        );

        animator.addAnimation(
                AnimationType.DEATH,
                new AnimatedSprite(
                        manager.getSheet("sprites/knight/Soldier_Death.png"),
                        8.0f,
                        false
                )
        );

        return animator;
    }

    public static CharacterAnimator createOrc(SpriteManager manager) {
        CharacterAnimator animator = new CharacterAnimator();

        animator.addAnimation(
                AnimationType.IDLE,
                new AnimatedSprite(
                        manager.getSheet("sprites/orc/Orc_Idle.png"),
                        8.0f,
                        true
                )
        );

        animator.addAnimation(
                AnimationType.WALK,
                new AnimatedSprite(
                        manager.getSheet("sprites/orc/Orc_Walk.png"),
                        10.0f,
                        true
                )
        );

        animator.addAnimation(
                AnimationType.ATTACK_1,
                new AnimatedSprite(
                        manager.getSheet("sprites/orc/Orc_Attack01.png"),
                        12.0f,
                        false
                )
        );

        animator.addAnimation(
                AnimationType.ATTACK_2,
                new AnimatedSprite(
                        manager.getSheet("sprites/orc/Orc_Attack02.png"),
                        12.0f,
                        false
                )
        );

        animator.addAnimation(
                AnimationType.HURT,
                new AnimatedSprite(
                        manager.getSheet("sprites/orc/Orc_Hurt.png"),
                        10.0f,
                        false
                )
        );

        animator.addAnimation(
                AnimationType.DEATH,
                new AnimatedSprite(
                        manager.getSheet("sprites/orc/Orc_Death.png"),
                        8.0f,
                        false
                )
        );

        return animator;
    }
}