package game.battle.abilities;
import game.battle.Hero;
/** Decorator: grants +5 attack (fire affinity). */
public class FireAffinity extends HeroDecorator {
    public FireAffinity(Hero inner) { super(inner); }
    @Override public int getAttack()  { return inner.getAttack() + 5; }
    @Override public int getDefense() { return inner.getDefense(); }
}
