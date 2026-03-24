package game.battle.abilities;
import game.battle.Hero;
/** Decorator: grants +5 defense (guardian blessing). */
public class GuardianBlessing extends HeroDecorator {
    public GuardianBlessing(Hero inner) { super(inner); }
    @Override public int getDefense() { return inner.getDefense() + 5; }
    @Override public int getAttack()  { return inner.getAttack(); }
}
