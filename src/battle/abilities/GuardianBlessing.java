package battle.abilities;

import battle.Hero;

/**
 * +5 defense.
 */
public class GuardianBlessing extends HeroDecorator {
    public GuardianBlessing(Hero inner) {
        super(inner);
    }

    @Override
    public int getDefense() {
        return inner.getDefense() + 5;
    }

    @Override
    public int getAttack() {
        return inner.getAttack();
    }
}

