package ui;

import models.ReportModel;
import models.ProductAnalysisModel;
import helpers.GUIHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private ReportModel reportModel;
    private ProductAnalysisModel analysisModel;
    private JTable productTable, customerTable;
    private JTable basketTargetTable, basketGlobalTable;
    private DefaultTableModel productModel, customerModel;
    private DefaultTableModel basketTargetModel, basketGlobalModel;
    private JTextField txtProductIdTarget;
    private JButton btnAnalyzeTarget;
    
    public DashboardPanel() {
        reportModel = new ReportModel();
        analysisModel = new ProductAnalysisModel();
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 20, 20));
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

        JPanel basketTargetPanel = new JPanel(new BorderLayout());
        basketTargetPanel.setBorder(BorderFactory.createTitledBorder("Top 3 Items Bought With Target"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Target Product ID:"));
        txtProductIdTarget = new JTextField("9", 5);
        btnAnalyzeTarget = new JButton("Analyze");
        inputPanel.add(txtProductIdTarget);
        inputPanel.add(btnAnalyzeTarget);
        basketTargetPanel.add(inputPanel, BorderLayout.NORTH);

        basketTargetModel = new DefaultTableModel(new String[]{"Companion Product", "Times Bought Together"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        basketTargetTable = new JTable(basketTargetModel);
        basketTargetPanel.add(new JScrollPane(basketTargetTable), BorderLayout.CENTER);

        btnAnalyzeTarget.addActionListener(e -> runTargetAnalysis());

        JPanel basketGlobalPanel = new JPanel(new BorderLayout());
        basketGlobalPanel.setBorder(BorderFactory.createTitledBorder("Top 3 Core Products in Multi-item Orders"));
        basketGlobalModel = new DefaultTableModel(new String[]{"Product Name", "Bundle Appearance Frequency"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        basketGlobalTable = new JTable(basketGlobalModel);
        basketGlobalPanel.add(new JScrollPane(basketGlobalTable), BorderLayout.CENTER);

        mainPanel.add(pPanel);
        mainPanel.add(cPanel);
        mainPanel.add(basketTargetPanel);
        mainPanel.add(basketGlobalPanel);
        add(mainPanel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Analytics");
        refreshBtn.addActionListener(e -> refreshReports());
        add(refreshBtn, BorderLayout.SOUTH);

        refreshReports();
    }

    private void runTargetAnalysis() {
        try {
            basketTargetModel.setRowCount(0);
            int targetId = Integer.parseInt(txtProductIdTarget.getText().trim());
            List<Object[]> analysisTargetData = analysisModel.getTop3ProductsBoughtTogether(targetId);
            
            if (analysisTargetData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No companion products found or invalid ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Object[] row : analysisTargetData) {
                    basketTargetModel.addRow(row);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Numeric Product ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshReports() {
        productModel.setRowCount(0);
        List<Object[]> products = reportModel.getTop5SellingProducts();
        for (Object[] row : products) productModel.addRow(row);

        customerModel.setRowCount(0);
        List<Object[]> customers = reportModel.getTop5SpendingCustomers();
        for (Object[] row : customers) customerModel.addRow(row);
    
        basketGlobalModel.setRowCount(0);
        List<Object[]> globalTogether = analysisModel.getTop3GlobalTogether();
        for (Object[] row : globalTogether) basketGlobalModel.addRow(row);

        runTargetAnalysis();
    }
}
