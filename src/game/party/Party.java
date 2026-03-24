package game.party;
import game.battle.Hero;
import java.util.*;
/** Holds heroes and shared gold for a PvE campaign or PvP match. */
public class Party {
    private final List<Hero> heroes;
    private int gold;

    public Party(List<Hero> heroes) { this.heroes = new ArrayList<>(heroes); this.gold = 500; }

    public List<Hero> getHeroes()   { return heroes; }
    public void addHero(Hero h)     { if (h!=null&&heroes.size()<5) heroes.add(h); }
    public int getTotalLevel()      { int s=0; for(Hero h:heroes) s+=h.getLevel(); return s; }
    public int  getGold()           { return gold; }
    public void addGold(int amt)    { gold=Math.max(0, gold+amt); }
    public void setGold(int gold)   { this.gold=Math.max(0,gold); }
}
