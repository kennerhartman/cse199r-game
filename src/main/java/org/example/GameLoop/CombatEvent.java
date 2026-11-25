
package org.example.GameLoop;

public class CombatEvent {

    private final CombatEventType type;
    private final Object source;
    private final Object target;
    private final int amount;

    public CombatEvent(
            CombatEventType type,
            Object source,
            Object target,
            int amount
    ) {
        this.type = type;
        this.source = source;
        this.target = target;
        this.amount = amount;
    }

    public CombatEventType getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }

    public Object getTarget() {
        return target;
    }

    public int getAmount() {
        return amount;
    }
}
