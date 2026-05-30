package ui;

import helpers.GUIHelper;
import models.ReturnModel;
import models.TransactionModel;
import session.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerReturnPanel extends JPanel {
    private ReturnModel returnModel;
    private TransactionModel transactionModel;

    private JTable returnsTable;
    private DefaultTableModel returnsTableModel;

    private JTable eligibleTable;
    private DefaultTableModel eligibleTableModel;

    private JSpinner spQuantity;
    private JTextArea taAlasan;
    private JButton btnSubmit;

    public CustomerReturnPanel() {
        returnModel = new ReturnModel();
        transactionModel = new TransactionModel();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(GUIHelper.createContentHeader("My Returns & Help"), BorderLayout.NORTH);

        JPanel topSplit = new JPanel(new BorderLayout(10, 10));

        eligibleTableModel = new DefaultTableModel(
                new String[]{"Trx ID", "Product Name", "Product ID", "SKU", "Qty Purchased", "Eligible Qty"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        eligibleTable = new JTable(eligibleTableModel);
        eligibleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eligibleTable.getTableHeader().setReorderingAllowed(false);
        eligibleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && eligibleTable.getSelectedRow() != -1) {
                int maxQty = (int) eligibleTable.getValueAt(eligibleTable.getSelectedRow(), 5);
                spQuantity.setModel(new SpinnerNumberModel(1, 1, maxQty, 1));
            }
        });

        JPanel eligiblePanel = new JPanel(new BorderLayout());
        eligiblePanel.setBorder(BorderFactory.createTitledBorder("Select Item to Return"));
        eligiblePanel.add(new JScrollPane(eligibleTable), BorderLayout.CENTER);
        
        topSplit.add(eligiblePanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Request Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Return Qty:"), gbc);
        spQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        gbc.gridx = 1; formPanel.add(spQuantity, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Reason:"), gbc);
        taAlasan = new JTextArea(4, 20);
        taAlasan.setLineWrap(true);
        JScrollPane spAlasan = new JScrollPane(taAlasan);
        gbc.gridx = 1; formPanel.add(spAlasan, gbc);

        btnSubmit = new JButton("Submit Request");
        btnSubmit.addActionListener(e -> submitReturn());
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(btnSubmit, gbc);

        topSplit.add(formPanel, BorderLayout.EAST);

        returnsTableModel = new DefaultTableModel(
                new String[]{"Return ID", "Trx ID", "Date", "Status", "Product ID", "SKU", "Qty", "Reason"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        returnsTable = new JTable(returnsTableModel);
        returnsTable.getTableHeader().setReorderingAllowed(false);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("My Return History"));
        tablePanel.add(new JScrollPane(returnsTable), BorderLayout.CENTER);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, tablePanel);
        mainSplit.setResizeWeight(0.5);
        mainSplit.setDividerLocation(300);

        add(mainSplit, BorderLayout.CENTER);

        refreshData();
    }

    private void submitReturn() {
        int selectedRow = eligibleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the table first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idTransaksi = (int) eligibleTable.getValueAt(selectedRow, 0);
            int idProduk = (int) eligibleTable.getValueAt(selectedRow, 2);
            String sku = (String) eligibleTable.getValueAt(selectedRow, 3);
            int qty = (int) spQuantity.getValue();
            String alasan = taAlasan.getText().trim();

            if (alasan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide a reason for the return.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idPengguna = UserSession.getCurrentUser().getIdPengguna();

            String response = returnModel.requestReturn(idPengguna, idTransaksi, idProduk, sku, qty, alasan);

            if (response != null && response.startsWith("SUCCESS")) {
                JOptionPane.showMessageDialog(this, response, "Success", JOptionPane.INFORMATION_MESSAGE);
                spQuantity.setValue(1);
                taAlasan.setText("");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, response, "Failed to submit request", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while submitting the return.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshData() {
        eligibleTableModel.setRowCount(0);
        returnsTableModel.setRowCount(0);
        
        if (UserSession.isLoggedIn()) {
            int idPengguna = UserSession.getCurrentUser().getIdPengguna();
            
            List<Object[]> eligibleItems = transactionModel.getEligibleReturnItems(idPengguna);
            for (Object[] row : eligibleItems) {
                eligibleTableModel.addRow(row);
            }
            
            List<Object[]> returns = returnModel.getReturnsByCustomer(idPengguna);
            for (Object[] row : returns) {
                returnsTableModel.addRow(row);
            }
        }
    }
}
