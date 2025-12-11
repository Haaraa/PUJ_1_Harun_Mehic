package financeapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FinanceTrackerForm {

    private JPanel mainPanel;
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<String> typeCombo;
    private JComboBox<String> categoryCombo;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton exportButton;
    private JTable transactionTable;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;

    private TransactionManager manager;
    private List<Transaction> currentTransactions = new ArrayList<>();
    private Transaction selectedTransaction = null;

    public FinanceTrackerForm() {
        manager = new TransactionManager();

        initCombos();
        loadDataIntoTable();
        updateSummary();
        initListeners();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }


    private void initCombos() {
        // prihod / rashod
        typeCombo.removeAllItems();
        typeCombo.addItem("Prihod");
        typeCombo.addItem("Rashod");

        // kategorije
        if (categoryCombo != null) {
            categoryCombo.removeAllItems();
            categoryCombo.addItem("Plata");
            categoryCombo.addItem("Hrana");
            categoryCombo.addItem("Racuni");
            categoryCombo.addItem("Zabava");
            categoryCombo.addItem("Prijevoz");
            categoryCombo.addItem("Ostalo");
        }
    }

    private void initListeners() {
        // add
        addButton.addActionListener(e -> onAdd());

        // update
        if (updateButton != null) {
            updateButton.addActionListener(e -> onUpdate());
        }

        // delete
        if (deleteButton != null) {
            deleteButton.addActionListener(e -> onDelete());
        }

        // export
        if (exportButton != null) {
            exportButton.addActionListener(e -> onExport());
        }

        // klik na tabelu promjena stanja
        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = transactionTable.getSelectedRow();
            if (row >= 0 && row < currentTransactions.size()) {
                selectedTransaction = currentTransactions.get(row);
                fillFormFromSelected();
            }
        });
    }


    private void onAdd() {
        try {
            String type = (String) typeCombo.getSelectedItem();
            String category = categoryCombo != null ? (String) categoryCombo.getSelectedItem() : "Ostalo";
            String amountText = amountField.getText().trim();
            String description = descriptionField.getText().trim();

            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Iznos ne može biti prazan!");
                return;
            }

            double amount = Double.parseDouble(amountText);

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Opis ne može biti prazan!");
                return;
            }

            Transaction t = new Transaction(type, category, amount, description);
            manager.addTransaction(t);

            loadDataIntoTable();
            updateSummary();
            clearForm();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Iznos mora biti broj!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainPanel, "Dogodila se greška: " + ex.getMessage());
        }
    }

    private void onUpdate() {
        if (selectedTransaction == null) {
            JOptionPane.showMessageDialog(mainPanel, "Molimo odaberite transakciju iz tabele za ažuriranje.");
            return;
        }

        try {
            String type = (String) typeCombo.getSelectedItem();
            String category = categoryCombo != null ? (String) categoryCombo.getSelectedItem() : "Ostalo";
            String amountText = amountField.getText().trim();
            String description = descriptionField.getText().trim();

            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Iznos ne može biti prazan!");
                return;
            }

            double amount = Double.parseDouble(amountText);

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Opis ne može biti prazan!");
                return;
            }

            // kreiranje novog objekta sa istm ID-em
            Transaction updated = new Transaction(
                    selectedTransaction.getId(),
                    type,
                    category,
                    amount,
                    description
            );

            manager.updateTransaction(updated);

            loadDataIntoTable();
            updateSummary();
            clearForm();
            selectedTransaction = null;
            transactionTable.clearSelection();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Iznos mora biti broj!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainPanel, "Dogodila se greška: " + ex.getMessage());
        }
    }

    private void onDelete() {
        if (selectedTransaction == null) {
            JOptionPane.showMessageDialog(mainPanel, "Molimo odaberite transakciju iz tabele za brisanje.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                mainPanel,
                "Jeste li sigurni da želite izbrisati ovu transakciju?",
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            manager.deleteTransaction(selectedTransaction.getId());

            loadDataIntoTable();
            updateSummary();
            clearForm();
            selectedTransaction = null;
            transactionTable.clearSelection();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainPanel, "Dogodila se greška: " + ex.getMessage());
        }
    }

    private void onExport() {
        try {
            List<Transaction> list = manager.getAllTransactions();

            double totalIncome = 0;
            double totalExpense = 0;

            // raspohi po kat
            Map<String, Double> expenseByCategory = new LinkedHashMap<>();
            expenseByCategory.put("Hrana", 0.0);
            expenseByCategory.put("Prijevoz", 0.0);
            expenseByCategory.put("Zabava", 0.0);
            expenseByCategory.put("Racuni", 0.0);
            expenseByCategory.put("Plata", 0.0);
            expenseByCategory.put("Ostalo", 0.0);

            for (Transaction t : list) {
                if ("Prihod".equals(t.getType())) {
                    totalIncome += t.getAmount();
                } else if ("Rashod".equals(t.getType())) {
                    totalExpense += t.getAmount();
                    // sabiranje po kat
                    String cat = t.getCategory();
                    if (!expenseByCategory.containsKey(cat)) {
                        expenseByCategory.put(cat, 0.0);
                    }
                    expenseByCategory.put(cat, expenseByCategory.get(cat) + t.getAmount());
                }
            }

            double balance = totalIncome - totalExpense;

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("finance_export.txt"));
            int result = chooser.showSaveDialog(mainPanel);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = chooser.getSelectedFile();

            try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                out.printf("Ukupni prihod: %.2f%n", totalIncome);
                out.printf("Ukupni rashod: %.2f%n", totalExpense);
                out.printf("Stanje: %.2f%n", balance);
                out.println("Rashodi po kategorijama:");
                for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
                    if (entry.getValue() > 0) {
                        out.printf("%s: %.2f%n", entry.getKey(), entry.getValue());
                    }
                }
            }

            JOptionPane.showMessageDialog(mainPanel,
                    "Podaci su uspješno exportovani u: " + file.getAbsolutePath());

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainPanel,
                    "Greška pri pisanju u datoteku: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainPanel,
                    "Dogodila se greška: " + ex.getMessage());
        }
    }


    private void loadDataIntoTable() {
        currentTransactions = manager.getAllTransactions();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Vrsta");
        model.addColumn("Kategorija");
        model.addColumn("Iznos");
        model.addColumn("Opis");

        for (Transaction t : currentTransactions) {
            model.addRow(new Object[]{
                    t.getType(),
                    t.getCategory(),
                    t.getAmount(),
                    t.getDescription()
            });
        }

        transactionTable.setModel(model);
    }

    private void updateSummary() {
        double income = manager.getTotalIncome();
        double expense = manager.getTotalExpense();
        double balance = income - expense;

        incomeLabel.setText("Prihod: " + income);
        expenseLabel.setText("Rashod: " + expense);
        balanceLabel.setText("Saldo: " + balance);
    }

    private void fillFormFromSelected() {
        if (selectedTransaction == null) return;

        amountField.setText(String.valueOf(selectedTransaction.getAmount()));
        descriptionField.setText(selectedTransaction.getDescription());
        typeCombo.setSelectedItem(selectedTransaction.getType());
        if (categoryCombo != null) {
            categoryCombo.setSelectedItem(selectedTransaction.getCategory());
        }
    }

    private void clearForm() {
        amountField.setText("");
        descriptionField.setText("");
        typeCombo.setSelectedIndex(0);
        if (categoryCombo != null && categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
    }


    private void createUIComponents() {
    }
}
