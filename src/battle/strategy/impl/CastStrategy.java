package battle.strategy.impl;

import battle.Battle;
import battle.Unit;
import battle.strategy.BattleActionStrategy;

/**
 * Simple CAST implementation:
 * - Costs 10 mana
 * - Deals (attack + 5) - defense damage to the first living target
 */
public class CastStrategy implements BattleActionStrategy {
    private static final int COST = 10;

    @Override
    public void execute(Battle battle, Unit actor) {
        if (!actor.spendMana(COST)) {
            // fallback: basic attack if insufficient mana
            battle.doAttack(actor);
            return;
        }

        var targetParty = battle.getEnemyParty().contains(actor) ? battle.getPlayerParty() : battle.getEnemyParty();
        Unit target = null;
        for (Unit u : targetParty) {
            if (u.isAlive()) {
                target = u;
                break;
            }
        }
        if (target == null) return;

        int raw = actor.getAttack() + 5;
        int damage = raw - target.getDefense();
        if (damage < 0) damage = 0;
        target.takeDamage(damage);
        battle.notifyDamage(actor, target, damage);
    }
}

