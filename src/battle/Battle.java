package battle;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Battle {
    private List<Unit> playerParty;
    private List<Unit> enemyParty;

    private Queue<Unit> turnQueue;
    private Queue<Unit> waitQueue;

    private boolean battleOver;

    public Battle(List<Unit> playerParty, List<Unit> enemyParty) {
        this.playerParty = playerParty;
        this.enemyParty = enemyParty;
        this.battleOver = false;
        this.turnQueue = new LinkedList<>();
        this.waitQueue = new LinkedList<>();
    }

    public void startBattle() {
        initializeTurnOrder();
        while (!isBattleOver()) {
            if (turnQueue.isEmpty()) {
                turnQueue.addAll(waitQueue);
                waitQueue.clear();
            }
            Unit current = turnQueue.poll();
            if (current != null) {
                processTurn(current);
            }
        }
        endBattle();
    }

    private void initializeTurnOrder() {
        waitQueue.clear();
        turnQueue.clear();

        List<Unit> allUnits = new LinkedList<>();

        for (Unit u : playerParty) {
            if (u.isAlive()) allUnits.add(u);
        }

        for (Unit u : enemyParty) {
            if (u.isAlive()) allUnits.add(u);
        }
        allUnits.sort((u1, u2) -> {
            if (u2.getLevel() != u1.getLevel()) {
                return u2.getLevel() - u1.getLevel();
            }
            return u2.getAttack() - u1.getAttack();
        });

        turnQueue.addAll(allUnits);

    }

    private void processTurn(Unit unit) {
        if (!unit.isAlive()) {
            return;
        }
        if (unit.isStunned()) {
            unit.setStunned(false);
            waitQueue.add(unit);
            return;
        }

        // Step 3: Decide action (Deliverable 1: always attack)
        Action action = Action.ATTACK;

        // Step 4 & 5: Execute action
        if (action == Action.ATTACK) {
            Unit target = null;

            // Enemy attacks hero
            if (enemyParty.contains(unit)) {
                for (Unit u : playerParty) {
                    if (u.isAlive()) {
                        target = u;
                        break;
                    }
                }
            }
            // Hero attacks enemy
            else {
                for (Unit u : enemyParty) {
                    if (u.isAlive()) {
                        target = u;
                        break;
                    }
                }
            }

            if (target != null) {
                handleAttack(unit, target);
            }
        }

        // Step 6: Re-queue unit if still alive
        if (unit.isAlive()) {
            waitQueue.add(unit);
        }
    }

    private void handleAttack(Unit attacker, Unit defender) {
        int damage = attacker.getAttack() - defender.getDefense();
        if (damage < 0) {
            damage = 0;
        }
        defender.takeDamage(damage);
    }

    private void handleDefend(Unit unit) {
        unit.restoreHp(10);
        unit.restoreMana(5);
    }

    private void handleWait(Unit unit) {

    }

    private boolean isBattleOver() {
        boolean heroesAlive = false;
        boolean enemiesAlive = false;

        for (Unit u : playerParty) {
            if (u.isAlive()) {
                heroesAlive = true;
                break;
            }
        }
        for (Unit u : enemyParty) {
            if (u.isAlive()) {
                enemiesAlive = true;
                break;
            }
        }
        return !heroesAlive || !enemiesAlive;
    }

    private void endBattle() {
        boolean heroesAlive = false;

        for (Unit u : playerParty) {
            if (u.isAlive()) {
                heroesAlive = true;
                break;
            }
        }
        if (heroesAlive) {
            System.out.println("Heroes win!");
        } else {
            System.out.println("Enemies win!");
        }
    }
}
