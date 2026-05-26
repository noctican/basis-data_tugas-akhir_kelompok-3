package ui;

import entities.Transaksi;
import helpers.GUIHelper;
import models.OrderModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderAdminPanel extends JPanel {
    private OrderModel orderModel;
    private JTable orderTable;
    private DefaultTableModel orderModelT;

    public OrderAdminPanel() {
        orderModel = new OrderModel();
        setLayout(new BorderLayout());
        add(GUIHelper.createContentHeader("Order & Shipping Management"), BorderLayout.NORTH);

        orderModelT = new DefaultTableModel(new String[]{"ID", "Date", "Total", "Status", "Payment"}, 0);
        orderTable = new JTable(orderModelT);
        add(new JScrollPane(orderTable), BorderLayout.CENTER);

        JButton shipBtn = new JButton("Mark as Shipped");
        shipBtn.addActionListener(e -> handleShip());
        add(shipBtn, BorderLayout.SOUTH);

        refreshOrders();
    }

    private void refreshOrders() {
        orderModelT.setRowCount(0);
        List<Transaksi> list = orderModel.getAllOrders();
        for (Transaksi t : list) {
            orderModelT.addRow(new Object[]{t.getIdTransaksi(), t.getTanggalTransaksi(), t.getTotalPembelian(), t.getStatusPembayaran(), t.getMetodePembayaran()});
        }
    }

    private void handleShip() {
        int row = orderTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) orderModelT.getValueAt(row, 0);
        String eks = JOptionPane.showInputDialog(this, "Enter Expedition Name:");
        if (eks != null && orderModel.shipOrder(id, eks)) {
            GUIHelper.showInfo(this, "Order shipped successfully!");
            refreshOrders();
        }
    }
}
