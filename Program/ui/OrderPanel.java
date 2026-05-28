package ui;

import helpers.GUIHelper;
import models.TransactionModel;
import session.UserSession;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderPanel extends JPanel {
    private TransactionModel transactionModel;
    
    // Komponen Master (Transaksi Utama)
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    
    // Komponen Detail (Produk dalam Transaksi / "Anak")
    private JTable detailTable;
    private DefaultTableModel detailTableModel;

    public OrderPanel() {
        transactionModel = new TransactionModel();
        setLayout(new BorderLayout());
        
        add(GUIHelper.createContentHeader("My Orders"), BorderLayout.NORTH);

        // 1. Setup Tabel Transaksi (Master)
        transactionTableModel = new DefaultTableModel(
                new String[]{"Order ID", "Date", "Total (Rp)", "Status", "Payment Method"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener saat baris transaksi diklik
        transactionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && transactionTable.getSelectedRow() != -1) {
                    loadTransactionDetails();
                }
            }
        });
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Daftar Pesanan (Transaksi)"));
        topPanel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        // 2. Setup Tabel Detail Produk (Detail)
        detailTableModel = new DefaultTableModel(
                new String[]{"Product ID", "SKU", "Price (Rp)", "Qty", "Subtotal (Rp)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        detailTable = new JTable(detailTableModel);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Detail Produk (Isi Pesanan)"));
        bottomPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        // 3. Gabungkan Keduanya menggunakan JSplitPane (Bisa di-resize oleh user)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setResizeWeight(0.5); // Membagi porsi layar 50:50
        splitPane.setDividerLocation(250);
        
        add(splitPane, BorderLayout.CENTER);

        // Panel Tombol Bawah (Opsional, untuk Refresh)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.addActionListener(e -> refreshTransactions());
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data saat panel pertama kali dibuka
        refreshTransactions();
    }

    private void refreshTransactions() {
        // Kosongkan tabel
        transactionTableModel.setRowCount(0);
        detailTableModel.setRowCount(0); // Kosongkan detail juga

        // Ambil ID pengguna yang sedang login
        int currentUserId = UserSession.getCurrentUser().getIdPengguna();
        
        List<Object[]> orders = transactionModel.getTransactionsByUser(currentUserId);
        for (Object[] row : orders) {
            transactionTableModel.addRow(row);
        }
    }

    private void loadTransactionDetails() {
        detailTableModel.setRowCount(0);
        int selectedRow = transactionTable.getSelectedRow();
        
        if (selectedRow != -1) {
            // Ambil Order ID dari kolom indeks ke-0
            int idTransaksi = (int) transactionTable.getValueAt(selectedRow, 0);
            
            List<Object[]> details = transactionModel.getTransactionDetails(idTransaksi);
            for (Object[] row : details) {
                detailTableModel.addRow(row);
            }
        }
    }
}