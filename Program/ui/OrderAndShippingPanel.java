package ui;

import entities.Transaksi;
import helpers.GUIHelper;
import helpers.NumberHelper;
import models.OrderModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class OrderAndShippingPanel extends JPanel {
    private OrderModel orderModel;
    private JTable orderTable;
    private DefaultTableModel orderModelT;
    private String currentFilter = "All";
    private JPanel actionPanel;

    public OrderAndShippingPanel() {
        orderModel = new OrderModel();
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(GUIHelper.createContentHeader("Order & Shipping Management"), BorderLayout.NORTH);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter Status Pembayaran: "));
        
        JButton btnAll = new JButton("All");
        JButton btnPending = new JButton("Pending");
        JButton btnPaid = new JButton("Paid");
        JButton btnFailed = new JButton("Failed");
        
        filterPanel.add(btnAll);
        filterPanel.add(btnPending);
        filterPanel.add(btnPaid);
        filterPanel.add(btnFailed);
        
        btnAll.addActionListener(e -> applyFilter("All"));
        btnPending.addActionListener(e -> applyFilter("Pending"));
        btnPaid.addActionListener(e -> applyFilter("Paid"));
        btnFailed.addActionListener(e -> applyFilter("Failed"));
        
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        orderModelT = new DefaultTableModel(new String[]{"ID Transaksi", "Tanggal", "Total", "Status Pembayaran", "Metode", "Status Pengiriman"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        orderTable = new JTable(orderModelT);
        orderTable.getTableHeader().setReorderingAllowed(false);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        
        NumberHelper.setRupiah(orderTable, 2);
        
        add(new JScrollPane(orderTable), BorderLayout.CENTER);

        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton btnKirim = new JButton("Delivered");
        JButton btnCancel = new JButton("Cancel");
        
        btnKirim.setBackground(new Color(40, 167, 69));  
        btnKirim.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(220, 53, 69)); 
        btnCancel.setForeground(Color.WHITE);

        btnKirim.addActionListener(e -> processShipping("Delivered"));
        btnCancel.addActionListener(e -> processShipping("Canceled"));
        
        actionPanel.add(btnKirim);
        actionPanel.add(btnCancel);
        
        add(actionPanel, BorderLayout.SOUTH);

        applyFilter("All");
    }

    private void applyFilter(String filter) {
        this.currentFilter = filter;
        actionPanel.setVisible("Paid".equals(filter));
        refreshOrders();
    }

    private void refreshOrders() {
        orderModelT.setRowCount(0);
        List<Transaksi> list = orderModel.getAllOrders(); 
        
        if (!"All".equals(currentFilter)) {
            list = list.stream()
                       .filter(t -> currentFilter.equalsIgnoreCase(t.getStatusPembayaran()))
                       .collect(Collectors.toList());
        }
        
        for (Transaksi t : list) {
            String statusKirim = "-";
            if (t.getPengiriman() != null && t.getPengiriman().getStatusPengiriman() != null) {
                statusKirim = t.getPengiriman().getStatusPengiriman();
            }

            orderModelT.addRow(new Object[]{
                t.getIdTransaksi(), 
                t.getTanggalTransaksi(), 
                t.getTotalPembelian(),
                t.getStatusPembayaran(), 
                t.getMetodePembayaran(),
                statusKirim 
            });
        }
    }

    private void processShipping(String statusPengiriman) {
        int selectedRow = orderTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Choose an order from the table first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentShipStatus = (String) orderModelT.getValueAt(selectedRow, 5);
        if ("Delivered".equals(currentShipStatus) || "Canceled".equals(currentShipStatus)) {
            JOptionPane.showMessageDialog(this, "This order has already been " + currentShipStatus + "!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTransaksi = (int) orderModelT.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to update order ID " + idTransaksi + " to status '" + statusPengiriman + "'?", 
            "Confirmation", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            String resiOtomatis = "RE-" + System.currentTimeMillis();
            
            boolean success = orderModel.updateShippingStatus(idTransaksi, resiOtomatis, statusPengiriman);
            
            if (success) {
                GUIHelper.showInfo(this, "Successfully! Shipping status updated to " + statusPengiriman);
                refreshOrders(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to process the order.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}