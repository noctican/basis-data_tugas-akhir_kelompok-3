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

        orderModelT = new DefaultTableModel(new String[]{"ID", "Date", "Total", "Status", "Payment"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        orderTable = new JTable(orderModelT);
        orderTable.getTableHeader().setReorderingAllowed(false);
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
            orderModelT.addRow(new Object[]{
                t.getIdTransaksi(), 
                t.getTanggalTransaksi(), 
                t.getTotalPembelian(), 
                t.getStatusPembayaran(), 
                t.getMetodePembayaran()
            });
        }
    }

    private void handleShip() {
        String idStr = JOptionPane.showInputDialog(this, "Masukkan ID Transaksi yang akan dikirim:");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            return;
        }

        try {
            int idTransaksi = Integer.parseInt(idStr.trim());
            
            String resiOtomatis = "RE-" + System.currentTimeMillis();
            
            if (orderModel.shipOrder(idTransaksi, resiOtomatis)) {
                GUIHelper.showInfo(this, "Pesanan berhasil diubah menjadi Shipped!");
                refreshOrders(); 
            } else {
                JOptionPane.showMessageDialog(this, "Gagal! Pastikan ID Transaksi yang kamu masukkan benar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID Transaksi harus berupa angka!", "Input Salah", JOptionPane.ERROR_MESSAGE);
        }
    }
}