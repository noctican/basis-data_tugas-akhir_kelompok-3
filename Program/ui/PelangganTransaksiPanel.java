package ui;

import entities.Pelanggan;
import entities.PelangganTransaksi;
import entities.Transaksi;
import models.PelangganModel;
import models.PelangganTransaksiModel;
import models.TransaksiModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PelangganTransaksiPanel extends JPanel {
    private PelangganTransaksiModel model = new PelangganTransaksiModel();
    private PelangganModel pelangganModel = new PelangganModel();
    private TransaksiModel transaksiModel = new TransaksiModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Pelanggan> cbPelanggan;
    private JComboBox<Transaksi> cbTransaksi;
    private JButton btnAdd, btnDelete, btnRefresh;
    private int selectedIdPengguna = -1;
    private int selectedIdTransaksi = -1;

    public PelangganTransaksiPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Pelanggan", "Nama Pelanggan", "ID Transaksi"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(2, 2));
        formPanel.add(new JLabel("Pelanggan:"));
        cbPelanggan = new JComboBox<>();
        formPanel.add(cbPelanggan);
        formPanel.add(new JLabel("Transaksi:"));
        cbTransaksi = new JComboBox<>();
        formPanel.add(cbTransaksi);

        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Add");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Events
        btnRefresh.addActionListener(e -> {
            refreshTable();
            refreshPelanggan();
            refreshTransaksi();
        });
        btnAdd.addActionListener(e -> addPT());
        btnDelete.addActionListener(e -> deletePT());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedIdPengguna = (int) tableModel.getValueAt(row, 0);
                selectedIdTransaksi = (int) tableModel.getValueAt(row, 2);
                
                for (int i = 0; i < cbPelanggan.getItemCount(); i++) {
                    if (cbPelanggan.getItemAt(i).getIdPengguna() == selectedIdPengguna) {
                        cbPelanggan.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < cbTransaksi.getItemCount(); i++) {
                    if (cbTransaksi.getItemAt(i).getIdTransaksi() == selectedIdTransaksi) {
                        cbTransaksi.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        refreshTable();
        refreshPelanggan();
        refreshTransaksi();
    }

    private void refreshPelanggan() {
        try {
            cbPelanggan.removeAllItems();
            List<Pelanggan> list = pelangganModel.getAll();
            for (Pelanggan pl : list) {
                cbPelanggan.addItem(pl);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Pelanggan: " + e.getMessage());
        }
    }

    private void refreshTransaksi() {
        try {
            cbTransaksi.removeAllItems();
            List<Transaksi> list = transaksiModel.getAll();
            for (Transaksi t : list) {
                cbTransaksi.addItem(t);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Transaksi: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<PelangganTransaksi> list = model.getAll();
            for (PelangganTransaksi pt : list) {
                tableModel.addRow(new Object[]{pt.getIdPengguna(), pt.getNamaPelanggan(), pt.getIdTransaksi()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addPT() {
        try {
            Pelanggan pl = (Pelanggan) cbPelanggan.getSelectedItem();
            Transaksi t = (Transaksi) cbTransaksi.getSelectedItem();
            if (pl == null || t == null) return;
            PelangganTransaksi pt = new PelangganTransaksi(pl.getIdPengguna(), t.getIdTransaksi());
            model.insert(pt);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void deletePT() {
        if (selectedIdPengguna == -1) return;
        try {
            model.delete(selectedIdPengguna, selectedIdTransaksi);
            refreshTable();
            selectedIdPengguna = -1;
            selectedIdTransaksi = -1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Delete: " + e.getMessage());
        }
    }
}
