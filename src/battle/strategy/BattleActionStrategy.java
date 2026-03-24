package battle.strategy;

import battle.Battle;
import battle.Unit;

/**
 * Strategy Pattern: encapsulates a battle action implementation.
 */
public interface BattleActionStrategy {
    void execute(Battle battle, Unit actor);
}

