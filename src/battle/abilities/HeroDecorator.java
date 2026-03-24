package battle.abilities;

import battle.Hero;

/**
 * Decorator Pattern: wraps a Hero and can modify stats/behavior.
 *
 * <p>All state changes delegate to the wrapped hero so the hero stays consistent in battle and persistence.</p>
 */
public abstract class HeroDecorator extends Hero {
    protected final Hero inner;

    protected HeroDecorator(Hero inner) {
        super(inner.getName(), inner.getLevel(), inner.getAttack(), inner.getDefense(), inner.getMaxHp(), inner.getMaxMana());
        this.inner = inner;
    }

    @Override public boolean isAlive() { return inner.isAlive(); }
    @Override public boolean isStunned() { return inner.isStunned(); }
    @Override public void setStunned(boolean stunned) { inner.setStunned(stunned); }
    @Override public void takeDamage(int damage) { inner.takeDamage(damage); }
    @Override public void restoreHp(int amount) { inner.restoreHp(amount); }
    @Override public void restoreMana(int amount) { inner.restoreMana(amount); }
    @Override public boolean spendMana(int amount) { return inner.spendMana(amount); }
    @Override public int getHp() { return inner.getHp(); }
    @Override public int getMana() { return inner.getMana(); }
    @Override public int getMaxHp() { return inner.getMaxHp(); }
    @Override public int getMaxMana() { return inner.getMaxMana(); }
    @Override public void setHp(int hp) { inner.setHp(hp); }
    @Override public void setMana(int mana) { inner.setMana(mana); }

    @Override public String getName() { return inner.getName(); }
    @Override public int getLevel() { return inner.getLevel(); }
}

