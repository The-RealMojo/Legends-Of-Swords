package ui.dashboard;

import db.GameSaveDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HallOfFameUI extends JFrame {

    private final GameSaveDAO dao;
    private DefaultListModel<String> listModel;

    public HallOfFameUI(GameSaveDAO dao) {
        this.dao = dao;

        setTitle("Hall of Fame");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Hall of Fame", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        JList<String> scoreList = new JList<>(listModel);
        scoreList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        add(new JScrollPane(scoreList), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton refresh = new JButton("Refresh");
        JButton close   = new JButton("Close");
        refresh.addActionListener(e -> loadScores());
        close  .addActionListener(e -> dispose());
        bottom.add(refresh); bottom.add(close);
        add(bottom, BorderLayout.SOUTH);

        loadScores();
    }

    private void loadScores() {
        listModel.clear();
        List<String> scores = dao.getTopScores(10);
        if (scores.isEmpty()) { listModel.addElement("No scores recorded yet."); return; }
        int rank = 1;
        for (String score : scores) listModel.addElement(rank++ + ". " + score);
    }
}