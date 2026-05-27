package ui;

import models.ReportModel;
import helpers.GUIHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private ReportModel reportModel;
    private JTable productTable, customerTable;
    private DefaultTableModel productModel, customerModel;

    public DashboardPanel() {
        reportModel = new ReportModel();
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel pPanel = new JPanel(new BorderLayout());
        pPanel.setBorder(BorderFactory.createTitledBorder("Top 5 Best Selling Products"));

        productModel = new DefaultTableModel(new String[]{"Product Name", "Qty Sold"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        productTable = new JTable(productModel);
        pPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel cPanel = new JPanel(new BorderLayout());
        cPanel.setBorder(BorderFactory.createTitledBorder("Top 5 Customers by Spending"));

        customerModel = new DefaultTableModel(new String[]{"Customer Name", "Total Spent"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        customerTable = new JTable(customerModel);
        cPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        mainPanel.add(pPanel);
        mainPanel.add(cPanel);
        add(mainPanel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Analytics");
        refreshBtn.addActionListener(e -> refreshReports());
        add(refreshBtn, BorderLayout.SOUTH);

        refreshReports();
    }

    private void refreshReports() {
        productModel.setRowCount(0);
        List<Object[]> products = reportModel.getTop5SellingProducts();
        for (Object[] row : products) productModel.addRow(row);

        customerModel.setRowCount(0);
        List<Object[]> customers = reportModel.getTop5SpendingCustomers();
        for (Object[] row : customers) customerModel.addRow(row);
    }
}
