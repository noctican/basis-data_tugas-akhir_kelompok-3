package ui;

import entities.Transaksi;
import helpers.GUIHelper;
import models.OrderModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AllTransactionsPanel extends JPanel {
    private OrderModel orderModel;
    private JTable transTable;
    private DefaultTableModel transModel;
    private JComboBox<String> statusFilter;

    public AllTransactionsPanel() {
        orderModel = new OrderModel();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setupTopPanel();
        setupTable();
        
        refreshTransactions();
    }

    private void setupTopPanel() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.add(new JLabel("Filter by Status:"));
        
        String[] statuses = {"ALL", "PENDING", "SHIPPED", "SUCCESS", "FAILED"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.addActionListener(e -> refreshTransactions());
        top.add(statusFilter);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTransactions());
        top.add(refreshBtn);

        add(top, BorderLayout.NORTH);
    }

    private void setupTable() {
        transModel = new DefaultTableModel(new String[]{"ID", "Date", "Total", "Status", "Payment"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        transTable = new JTable(transModel);
        add(new JScrollPane(transTable), BorderLayout.CENTER);
    }

    private void refreshTransactions() {
        transModel.setRowCount(0);
        String selectedStatus = (String) statusFilter.getSelectedItem();
        List<Transaksi> list = orderModel.getAllOrdersFiltered(selectedStatus);
        for (Transaksi t : list) {
            transModel.addRow(new Object[]{t.getIdTransaksi(), t.getTanggalTransaksi(), t.getTotalPembelian(), t.getStatusPembayaran(), t.getMetodePembayaran()});
        }
    }
}
