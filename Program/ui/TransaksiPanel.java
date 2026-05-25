package ui;

import entities.Transaksi;
import models.TransaksiModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class TransaksiPanel extends JPanel {
    private TransaksiModel model = new TransaksiModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTanggal, txtTotal, txtMetode;
    private JComboBox<String> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public TransaksiPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Tanggal", "Total", "Status", "Metode"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("Tanggal (YYYY-MM-DD HH:MM:SS):"));
        txtTanggal = new JTextField();
        formPanel.add(txtTanggal);
        formPanel.add(new JLabel("Total:"));
        txtTotal = new JTextField();
        formPanel.add(txtTotal);
        formPanel.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"PENDING", "SUCCESS", "FAILED"});
        formPanel.add(cbStatus);
        formPanel.add(new JLabel("Metode:"));
        txtMetode = new JTextField();
        formPanel.add(txtMetode);

        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Events
        btnRefresh.addActionListener(e -> refreshTable());
        btnAdd.addActionListener(e -> addTransaksi());
        btnUpdate.addActionListener(e -> updateTransaksi());
        btnDelete.addActionListener(e -> deleteTransaksi());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtTanggal.setText(tableModel.getValueAt(row, 1).toString());
                txtTotal.setText(tableModel.getValueAt(row, 2).toString());
                cbStatus.setSelectedItem(tableModel.getValueAt(row, 3));
                txtMetode.setText((String) tableModel.getValueAt(row, 4));
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Transaksi> list = model.getAll();
            for (Transaksi t : list) {
                tableModel.addRow(new Object[]{t.getIdTransaksi(), t.getTanggalTransaksi(), t.getTotalPembelian(), t.getStatusPembayaran(), t.getMetodePembayaran()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addTransaksi() {
        try {
            Transaksi t = new Transaksi();
            t.setTanggalTransaksi(Timestamp.valueOf(txtTanggal.getText()));
            t.setTotalPembelian(new BigDecimal(txtTotal.getText()));
            t.setStatusPembayaran(cbStatus.getSelectedItem().toString());
            t.setMetodePembayaran(txtMetode.getText());
            model.insert(t);
            refreshTable();
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateTransaksi() {
        if (selectedId == -1) return;
        try {
            Transaksi t = new Transaksi(selectedId, Timestamp.valueOf(txtTanggal.getText()), new BigDecimal(txtTotal.getText()), cbStatus.getSelectedItem().toString(), txtMetode.getText());
            model.update(t);
            refreshTable();
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteTransaksi() {
        if (selectedId == -1) return;
        try {
            model.delete(selectedId);
            refreshTable();
            selectedId = -1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Delete: " + e.getMessage());
        }
    }
}
