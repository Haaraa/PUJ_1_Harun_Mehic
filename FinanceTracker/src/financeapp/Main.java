package financeapp;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // (nije obavezno, ali bolje je pokretati GUI na Event Dispatch Thread-u)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Praćenje ličnih finansija");
            frame.setContentPane(new FinanceTrackerForm().getMainPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 550);       // širina, visina
            frame.setLocationRelativeTo(null); // centriraj prozor
            frame.setVisible(true);
        });
    }
}
