package ui;

import entities.Pelanggan;
import entities.Pengguna;
import models.TransactionModel;
import models.UserModel;
import session.UserSession;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPelanggan extends JPanel {
    private TransactionModel transactionModel;
    private UserModel userModel;

    private JTable topProductsTable;
    private DefaultTableModel tableModel;

    public DashboardPelanggan() {
        transactionModel = new TransactionModel();
        userModel = new UserModel();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(Box.createVerticalStrut(20)); 
        setupTopProductsPanel();

        loadTopProducts();
    }

    private void setupTopProductsPanel() {
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "5 Top Purchased Items", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        tableModel = new DefaultTableModel(new String[]{"Product Name", "Category", "Total Quantity", "Unit Price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        topProductsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(topProductsTable);
        scrollPane.setPreferredSize(new Dimension(500, 150)); 
        
        productsPanel.add(scrollPane, BorderLayout.CENTER);
        add(productsPanel);
    }

    private void loadTopProducts() {
        tableModel.setRowCount(0);
        Pengguna currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            List<Object[]> items = transactionModel.getTop5PurchasedItems(currentUser.getIdPengguna());
            for (Object[] row : items) {
                tableModel.addRow(row);
            }
        }
    }
}