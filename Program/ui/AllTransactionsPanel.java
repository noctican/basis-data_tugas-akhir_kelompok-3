package ui;

import entities.Transaksi;
import helpers.GUIHelper;
import helpers.NumberHelper;
import models.OrderModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AllTransactionsPanel extends JPanel {
    private OrderModel orderModel;
    private JTable transTable;
    private DefaultTableModel transModel;
    private JComboBox<String> statusPembayaranFilter;
    private JComboBox<String> statusPengirimanFilter;

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
        
        top.add(new JLabel("Status Pembayaran:"));
        String[] statusPembayaran = {"All", "Pending", "Paid", "Failed"};
        statusPembayaranFilter = new JComboBox<>(statusPembayaran);
        statusPembayaranFilter.addActionListener(e -> refreshTransactions());
        top.add(statusPembayaranFilter);

        top.add(new JLabel("Status Pengiriman:"));
        String[] statusPengiriman = {"All", "Process", "Delivered", "Completed", "Canceled"};
        statusPengirimanFilter = new JComboBox<>(statusPengiriman);
        statusPengirimanFilter.addActionListener(e -> refreshTransactions());
        top.add(statusPengirimanFilter);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTransactions());
        top.add(refreshBtn);

        add(top, BorderLayout.NORTH);
    }

    private void setupTable() {
        transModel = new DefaultTableModel(new String[]{
            "ID", "Date", "Total", "Metode Pembayaran", "Status Pembayaran", "Status Pengiriman"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        transTable = new JTable(transModel);
        transTable.getTableHeader().setReorderingAllowed(false);
        
        NumberHelper.setRupiah(transTable, 2); 
        
        add(new JScrollPane(transTable), BorderLayout.CENTER);
    }

    private void refreshTransactions() {
        transModel.setRowCount(0);
        String selPembayaran = (String) statusPembayaranFilter.getSelectedItem();
        String selPengiriman = (String) statusPengirimanFilter.getSelectedItem();
        
        List<Transaksi> list = orderModel.getAllOrdersFiltered(selPembayaran, selPengiriman);
        
        for (Transaksi t : list) {
            String statusKirim = "-";
            if (t.getPengiriman() != null && t.getPengiriman().getStatusPengiriman() != null) {
                statusKirim = t.getPengiriman().getStatusPengiriman();
            }
            
            transModel.addRow(new Object[]{
                t.getIdTransaksi(), 
                t.getTanggalTransaksi(), 
                t.getTotalPembelian(),
                t.getMetodePembayaran(),
                t.getStatusPembayaran(), 
                statusKirim 
            });
        }
    }
}