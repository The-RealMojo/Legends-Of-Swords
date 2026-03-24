package battle.strategy.impl;

import battle.Battle;
import battle.Unit;
import battle.strategy.BattleActionStrategy;

public class WaitStrategy implements BattleActionStrategy {
    @Override
    public void execute(Battle battle, Unit actor) {
        // WAIT: no action. Queue movement is handled by Battle.executeAction (re-offer)
        // and by Battle.startBattle rotation logic.
    }
}

