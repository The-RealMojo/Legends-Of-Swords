package game.inn;

import game.battle.Hero;
import game.campaign.Room;
import game.party.Party;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * On entry: all heroes are automatically healed and revived for free.
 * Player can buy items from the shop, recruit heroes (rooms 1–10), then leave.
 * execute() blocks the background thread via synchronized wait/notify.
 */
public class InnRoom extends Room {

    private static final Object[][] ITEMS = {
        {"Bread",  200,  20,   0, false},
        {"Cheese", 500,  50,   0, false},
        {"Steak",  1000, 200,  0, false},
        {"Water",  150,   0,  10, false},
        {"Juice",  400,   0,  30, false},
        {"Wine",   750,   0, 100, false},
        {"Elixir", 2000,  0,   0, true },
    };

    public InnRoom(int floor) { super(floor); }

    @Override
    public String execute(Party party) {
        StringBuilder log = new StringBuilder("🏠 All heroes have been healed.\n");
        for (Hero h : party.getHeroes()) {
            int hpG=h.getMaxHp()-h.getHp(), mG=h.getMaxMana()-h.getMana();
            if (!h.isAlive()) { h.setHp(1); hpG=h.getMaxHp()-1; }
            h.restoreHp(9999); h.restoreMana(9999);
            log.append(String.format("  %s: +%d HP, +%d Mana\n", h.getName(), hpG, mG));
        }

        final Object lock = new Object();
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = buildDialog(party, log, lock);
            dialog.setVisible(true);
            synchronized (lock) { lock.notifyAll(); }
        });
        synchronized (lock) { try { lock.wait(); } catch (InterruptedException ignored) {} }
        return log.toString();
    }

    private JDialog buildDialog(Party party, StringBuilder log, Object lock) {
        JDialog dialog = new JDialog((Frame)null, "Inn — Room " + floor, true);
        dialog.setSize(550, 530); dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout(8,8));
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                synchronized (lock) { lock.notifyAll(); }
            }
        });

        JLabel goldLabel = new JLabel("Gold: " + party.getGold() + "g");
        goldLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        goldLabel.setBorder(BorderFactory.createEmptyBorder(8,12,4,8));
        dialog.add(goldLabel, BorderLayout.NORTH);

        // Shop
        JPanel shopPanel = new JPanel();
        shopPanel.setLayout(new BoxLayout(shopPanel, BoxLayout.Y_AXIS));
        shopPanel.setBorder(BorderFactory.createTitledBorder("Shop"));
        for (Object[] item : ITEMS) {
            String  n      = (String)  item[0];
            int     cost   = (Integer) item[1];
            int     hp     = (Integer) item[2];
            int     mana   = (Integer) item[3];
            boolean elixir = (Boolean) item[4];
            String  desc   = elixir ? "Revive + Full HP + Mana" : (hp>0?"+"+hp+" HP ":"")+(mana>0?"+"+mana+" Mana":"");
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel(String.format("%-10s %4dg  %-26s", n, cost, desc)));
            String[] names = party.getHeroes().stream().map(Hero::getName).toArray(String[]::new);
            JComboBox<String> box = new JComboBox<>(names);
            JButton buyBtn = new JButton("Buy");
            buyBtn.addActionListener(e -> {
                if (party.getGold()<cost){JOptionPane.showMessageDialog(dialog,"Not enough gold!");return;}
                Hero t = party.getHeroes().get(box.getSelectedIndex());
                party.addGold(-cost);
                if (elixir) {
                    if (!t.isAlive()) {
                        t.setHp(1); // revive with 1 HP so restoreHp works
                    }
                    t.restoreHp(9999);
                    t.restoreMana(9999);
                }
                else{if(hp>0)t.restoreHp(hp);if(mana>0)t.restoreMana(mana);}
                t.addItemPurchaseScore((cost/2)*10);
                log.append("  Bought ").append(n).append(" for ").append(t.getName()).append(" (-").append(cost).append("g)\n");
                goldLabel.setText("Gold: "+party.getGold()+"g");
            });
            row.add(box); row.add(buyBtn); shopPanel.add(row);
        }

        // Recruit
        JPanel recruitPanel = new JPanel();
        recruitPanel.setLayout(new BoxLayout(recruitPanel, BoxLayout.Y_AXIS));
        recruitPanel.setBorder(BorderFactory.createTitledBorder("Available Heroes"));
        if (floor<=10&&party.getHeroes().size()<5) {
            Random rand=new Random();
            Hero.HeroClass[] classes={Hero.HeroClass.ORDER,Hero.HeroClass.CHAOS,Hero.HeroClass.WARRIOR,Hero.HeroClass.MAGE};
            for (int i=0;i<1+rand.nextInt(3);i++) {
                int lvl=rand.nextInt(4)+1, cost=lvl==1?0:lvl*200;
                Hero.HeroClass cls=classes[rand.nextInt(classes.length)];
                String cName="Recruit_"+cls.name().charAt(0)+lvl;
                JPanel row=new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.add(new JLabel(String.format("%-14s Lv%d %-9s %s",cName,lvl,cls.name(),cost==0?"Free!":cost+"g")));
                JButton btn=new JButton("Recruit");
                btn.addActionListener(e->{
                    if(party.getHeroes().size()>=5){JOptionPane.showMessageDialog(dialog,"Party full!");return;}
                    if(party.getGold()<cost){JOptionPane.showMessageDialog(dialog,"Not enough gold!");return;}
                    party.addGold(-cost);
                    Hero recruit = new Hero(cName, cls);

                    while (recruit.getLevel() < lvl) {
                        recruit.addExp(1000);
                    }

                    party.addHero(recruit);
                    log.append("  Recruited ").append(cName).append(" (Lv").append(lvl).append(", ").append(cls).append(")\n");
                    btn.setEnabled(false); goldLabel.setText("Gold: "+party.getGold()+"g");
                });
                row.add(btn); recruitPanel.add(row);
            }
        } else {
            recruitPanel.add(new JLabel(floor>10?"  No heroes available after room 10.":"  Party is full (5/5)."));
        }

        JPanel center=new JPanel(new GridLayout(2,1,0,8));
        center.add(new JScrollPane(shopPanel)); center.add(new JScrollPane(recruitPanel));
        dialog.add(center, BorderLayout.CENTER);
        JButton leave=new JButton("Leave Inn →");
        leave.addActionListener(e->dialog.dispose());
        dialog.add(leave, BorderLayout.SOUTH);
        return dialog;
    }
}
