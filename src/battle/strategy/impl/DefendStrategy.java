package battle.strategy.impl;

import battle.Battle;
import battle.Unit;
import battle.strategy.BattleActionStrategy;

public class DefendStrategy implements BattleActionStrategy {
    @Override
    public void execute(Battle battle, Unit actor) {
        battle.doDefend(actor);
    }
}

