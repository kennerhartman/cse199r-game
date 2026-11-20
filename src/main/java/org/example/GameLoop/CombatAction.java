package org.example.GameLoop;

import org.example.Character.PlayerCharacter;
import org.example.card.Card;
import org.example.enemies.Enemy;

public class CombatAction {

    private final PlayerCharacter player;
    private final Card card;
    private final Enemy target;

    public CombatAction(
            PlayerCharacter player,
            Card card,
            Enemy target
    ) {
        this.player = player;
        this.card = card;
        this.target = target;
    }

    public PlayerCharacter getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }

    public Enemy getTarget() {
        return target;
    }
}