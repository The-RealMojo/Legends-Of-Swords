package test.battle;
import battle.Hero;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {
    @Test
    void testTakeDamageReducesHp() {
        Hero hero = new Hero("Hero", 1, 10, 5, 100, 50);
        hero.takeDamage(30);
        assertEquals(70, hero.getHp());
    }

    @Test
    void testTakeDamageCannotGoBelowZero() {
        Hero hero = new Hero("Hero", 1, 10, 5, 100, 50);
        hero.takeDamage(200);
        assertEquals(0, hero.getHp());
        assertFalse(hero.isAlive());
    }

    @Test
    void testTakeDamageNegativeDoesNothing() {
        Hero hero = new Hero("Hero", 1, 10, 5, 100, 50);
        hero.takeDamage(-50);
        assertEquals(100, hero.getHp());
    }

    @Test
    void testRestoreHpCannotExceedMax() {
        Hero hero = new Hero("Hero", 1, 10, 5, 100, 50);
        hero.takeDamage(20);
        hero.restoreHp(50);
        assertEquals(100, hero.getHp());
    }

    @Test
    void testRestoreManaCannotExceedMax() {
        Hero hero = new Hero("Hero", 1, 10, 5, 100, 50);
        hero.restoreMana(100);
        assertEquals(50, hero.getMana());
    }

    @Test
    void testStunnedFlagWorks() {
        Hero hero = new Hero("Hero", 1, 10, 5, 100, 50);
        hero.setStunned(true);
        assertTrue(hero.isStunned());
    }
}
