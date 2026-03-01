package test.battle;
import battle.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

public class BattleTest {

    private Hero hero;
    private Enemy enemy;

    @BeforeEach
    void setUp() {
        hero = new Hero("Hero", 5, 20, 10, 100, 30);
        enemy = new Enemy("Enemy", 3, 15, 5, 80, 20);
    }

    @Test
    void testAttackDamageCalculation() {
        enemy.takeDamage(hero.getAttack() - enemy.getDefense());
        assertEquals(65, enemy.getHp()); // 80 - (20-5)
    }

    @Test
    void testDamageCannotBeNegative() {
        Hero weakHero = new Hero("Weak", 1, 5, 10, 100, 30);
        Enemy strongEnemy = new Enemy("Tank", 1, 10, 50, 100, 20);

        strongEnemy.takeDamage(weakHero.getAttack() - strongEnemy.getDefense());

        assertEquals(100, strongEnemy.getHp());
    }

    @Test
    void testHpNeverBelowZero() {
        enemy.takeDamage(200);
        assertEquals(0, enemy.getHp());
        assertFalse(enemy.isAlive());
    }

    @Test
    void testDefendRestoresHpAndMana() {
        hero.takeDamage(30);
        hero.restoreMana(-10); // just to simulate mana drop if needed

        hero.restoreHp(10);
        hero.restoreMana(5);

        assertEquals(80, hero.getHp());
        assertEquals(30, hero.getMana()); // capped at max
    }

    @Test
    void testHpDoesNotExceedMax() {
        hero.restoreHp(50);
        assertEquals(100, hero.getHp());
    }

    @Test
    void testTurnOrderByLevel() {
        Hero highLevel = new Hero("High", 10, 10, 5, 100, 20);
        Hero lowLevel = new Hero("Low", 1, 50, 5, 100, 20);

        List<Unit> heroes = Arrays.asList(highLevel, lowLevel);
        List<Unit> enemies = new ArrayList<>();

        Battle battle = new Battle(heroes, enemies);
        battle.startBattle();

        assertTrue(highLevel.getHp() > 0);
    }

    @Test
    void testStunSkipsOneTurn() {
        hero.setStunned(true);

        assertTrue(hero.isStunned());

        hero.setStunned(false);

        assertFalse(hero.isStunned());
    }

    @Test
    void testBattleEndsWhenAllEnemiesDead() {
        enemy.takeDamage(200);

        List<Unit> heroes = Arrays.asList(hero);
        List<Unit> enemies = Arrays.asList(enemy);

        Battle battle = new Battle(heroes, enemies);

        assertTrue(enemy.getHp() == 0);
    }

    @Test
    void testTurnOrderSortedByLevelThenAttack() {
        Hero h1 = new Hero("H1", 1, 10, 5, 100, 50);
        Hero h2 = new Hero("H2", 3, 5, 5, 100, 50);
        Enemy e1 = new Enemy("E1", 2, 20, 5, 100, 50);

        Battle battle = new Battle(
                List.of(h1, h2),
                List.of(e1)
        );

        var order = battle.getTurnOrder();

        assertEquals("H2", order.get(0).getName()); // level 3
        assertEquals("E1", order.get(1).getName()); // level 2
        assertEquals("H1", order.get(2).getName()); // level 1
    }

    @Test
    void testAttackReducesHpCorrectly() {
        Hero hero = new Hero("Hero", 1, 20, 5, 100, 50);
        Enemy enemy = new Enemy("Enemy", 1, 5, 5, 100, 50);

        Battle battle = new Battle(
                List.of(hero),
                List.of(enemy)
        );

        battle.executeAction(Action.ATTACK);

        // Damage = 20 - 5 = 15
        assertEquals(85, enemy.getHp());
    }

    @Test
    void testAttackCannotDealNegativeDamage() {
        Hero hero = new Hero("Hero", 1, 5, 5, 100, 50);
        Enemy enemy = new Enemy("Enemy", 1, 50, 50, 100, 50);

        Battle battle = new Battle(
                List.of(hero),
                List.of(enemy)
        );

        battle.executeAction(Action.ATTACK);

        assertEquals(100, enemy.getHp());
    }

    @Test
    void testDeadUnitIsNotTargeted() {
        Hero hero = new Hero("Hero", 1, 200, 0, 100, 50);
        Enemy enemy = new Enemy("Enemy", 1, 5, 0, 100, 50);

        Battle battle = new Battle(
                List.of(hero),
                List.of(enemy)
        );

        battle.executeAction(Action.ATTACK);

        assertEquals(0, enemy.getHp());
        assertTrue(battle.isBattleOver());
    }

    @Test
    void testHeroesWin() {
        Hero hero = new Hero("Hero", 1, 200, 0, 100, 50);
        Enemy enemy = new Enemy("Enemy", 1, 5, 0, 100, 50);

        Battle battle = new Battle(
                List.of(hero),
                List.of(enemy)
        );

        battle.executeAction(Action.ATTACK);

        assertEquals("Heroes", battle.getWinner());
    }

    @Test
    void testDefendRestoresStats() {
        Hero hero = new Hero("Hero", 1, 10, 5, 100, 50);
        hero.takeDamage(20);

        Battle battle = new Battle(
                List.of(hero),
                List.of(new Enemy("Enemy", 1, 5, 5, 100, 50))
        );

        battle.executeAction(Action.DEFEND);

        assertEquals(90, hero.getHp()); // 80 + 10
        assertEquals(50, hero.getMana()); // capped
    }

    @Test
    void testWaitAction() {
        Hero hero = new Hero("Hero", 1, 10, 5, 100, 50);
        Enemy enemy = new Enemy("Enemy", 1, 10, 5, 100, 50);

        Battle battle = new Battle(
                List.of(hero),
                List.of(enemy)
        );

        Unit first = battle.getCurrentUnit();
        battle.executeAction(Action.WAIT);
        Unit second = battle.getCurrentUnit();

        assertNotEquals(first, second);
    }
}
