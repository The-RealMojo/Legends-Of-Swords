package battle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Battle {
    private List<Unit> playerParty;
    private List<Unit> enemyParty;

    private Queue<Unit> turnQueue;
    private Queue<Unit> waitQueue;

    private boolean battleOver;
    private List<battle.observer.BattleObserver> observers = new ArrayList<>();

    public void addObserver(battle.observer.BattleObserver o) {
        observers.add(o);
    }

    public void notifyTurnStart(Unit unit) {
        for (var o : observers) o.onTurnStart(unit);
    }

    public void notifyAction(Unit unit, Action action) {
        for (var o : observers) o.onAction(unit, action);
    }

    public void notifyDamage(Unit attacker, Unit target, int damage) {
        for (var o : observers) o.onDamage(attacker, target, damage);
    }

    public void notifyBattleEnd(String winner) {
        for (var o : observers) o.onBattleEnd(winner);
    }

    public List<Unit> getPlayerParty() { return playerParty; }
    public List<Unit> getEnemyParty() { return enemyParty; }

    public Battle(List<Unit> playerParty, List<Unit> enemyParty) {
        this.playerParty = playerParty;
        this.enemyParty = enemyParty;
        this.battleOver = false;
        this.turnQueue = new LinkedList<>();
        this.waitQueue = new LinkedList<>();
        initializeTurnOrder();
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

        List<Unit> allUnits = new ArrayList<>();

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

    public Unit getCurrentUnit() {
        if (turnQueue.isEmpty()) return null;
        return turnQueue.peek();
    }

    public void executeAction(Action action) {
        if (battleOver) return;

        Unit current = turnQueue.poll();
        if (current == null || !current.isAlive()) {
            return;
        }

        switch (action) {
            case ATTACK:
                new battle.strategy.impl.AttackStrategy().execute(this, current);
                break;
            case DEFEND:
                new battle.strategy.impl.DefendStrategy().execute(this, current);
                break;
            case CAST:
                new battle.strategy.impl.CastStrategy().execute(this, current);
                break;
            case WAIT:
                new battle.strategy.impl.WaitStrategy().execute(this, current);
                break;
        }

        if (isBattleOver()) {
            battleOver = true;
            return;
        }

        if (current.isAlive()) {
            turnQueue.offer(current);
        }
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

        notifyTurnStart(unit);

        battle.strategy.BattleActionStrategy strategy;
        Action action;

        // Simplified auto-battle logic:
        if (unit.getMana() >= 10 && Math.random() < 0.3) {
            strategy = new battle.strategy.impl.CastStrategy();
            action = Action.CAST;
        } else if (unit.getHp() < unit.getMaxHp() / 3 && Math.random() < 0.5) {
            strategy = new battle.strategy.impl.DefendStrategy();
            action = Action.DEFEND;
        } else {
            strategy = new battle.strategy.impl.AttackStrategy();
            action = Action.ATTACK;
        }

        notifyAction(unit, action);
        strategy.execute(this, unit);

        if (unit.isAlive() && action != Action.WAIT) {
            waitQueue.add(unit);
        } else if (unit.isAlive() && action == Action.WAIT) {
            waitQueue.add(unit);
        }
    }

    public void doAttack(Unit attacker) {
        List<Unit> targetParty = enemyParty.contains(attacker)
                ? playerParty
                : enemyParty;

        Unit target = null;
        for (Unit u : targetParty) {
            if (u.isAlive()) {
                target = u;
                break;
            }
        }

        if (target == null) return;

        int damage = attacker.getAttack() - target.getDefense();
        if (damage < 0) damage = 0;

        target.takeDamage(damage);
        notifyDamage(attacker, target, damage);
    }

    public void doDefend(Unit unit) {
        unit.restoreHp(10);
        unit.restoreMana(5);
    }

    public boolean isBattleOver() {
        return noLivingUnits(playerParty) || noLivingUnits(enemyParty);
    }

    private boolean noLivingUnits(List<Unit> party) {
        for (Unit u : party) {
            if (u.isAlive()) return false;
        }
        return true;
    }

    public String getWinner() {
        if (!isBattleOver()) return null;

        if (noLivingUnits(enemyParty)) return "Heroes";
        if (noLivingUnits(playerParty)) return "Enemies";

        return null;
    }

    public List<Unit> getTurnOrder() {
        return new ArrayList<>(turnQueue);
    }

    private void endBattle() {
        boolean heroesAlive = false;

        for (Unit u : playerParty) {
            if (u.isAlive()) {
                heroesAlive = true;
                break;
            }
        }
        
        String winner = heroesAlive ? "Heroes" : "Enemies";
        System.out.println(winner + " win!");
        notifyBattleEnd(winner);
    }
}
