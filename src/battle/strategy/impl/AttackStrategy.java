package battle.strategy.impl;

import battle.Battle;
import battle.strategy.BattleActionStrategy;
import battle.Unit;

public class AttackStrategy implements BattleActionStrategy {
    @Override
    public void execute(Battle battle, Unit actor) {
        battle.doAttack(actor);
    }
}

