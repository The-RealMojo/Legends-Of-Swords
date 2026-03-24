package battle.observer;

import battle.Action;
import battle.Unit;

/**
 * Observer Pattern: battle publishes events; UI/controllers can subscribe.
 */
public interface BattleObserver {
    default void onTurnStart(Unit unit) {}
    default void onAction(Unit actor, Action action) {}
    default void onDamage(Unit attacker, Unit target, int damage) {}
    default void onBattleEnd(String winner) {}
}

