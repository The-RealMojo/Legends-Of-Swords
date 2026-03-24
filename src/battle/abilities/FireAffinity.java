package battle.abilities;

import battle.Hero;

/**
 * +5 attack.
 */
public class FireAffinity extends HeroDecorator {
    public FireAffinity(Hero inner) {
        super(inner);
    }

    @Override
    public int getAttack() {
        return inner.getAttack() + 5;
    }

    @Override
    public int getDefense() {
        return inner.getDefense();
    }
}

