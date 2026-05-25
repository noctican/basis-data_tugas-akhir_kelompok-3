package ui;

import entities.Keranjang;
import entities.Pelanggan;
import models.KeranjangModel;
import models.PelangganModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class KeranjangPanel extends JPanel {
    private KeranjangModel model = new KeranjangModel();
    private PelangganModel pelangganModel = new PelangganModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Pelanggan> cbPelanggan;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public KeranjangPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Keranjang", "ID Pelanggan", "Nama Pelanggan"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(1, 2));
        formPanel.add(new JLabel("Pelanggan:"));
        cbPelanggan = new JComboBox<>();
        formPanel.add(cbPelanggan);

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
        btnRefresh.addActionListener(e -> {
            refreshTable();
            refreshPelanggan();
        });
        btnAdd.addActionListener(e -> addKeranjang());
        btnUpdate.addActionListener(e -> updateKeranjang());
        btnDelete.addActionListener(e -> deleteKeranjang());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                int pelId = (int) tableModel.getValueAt(row, 1);
                
                for (int i = 0; i < cbPelanggan.getItemCount(); i++) {
                    if (cbPelanggan.getItemAt(i).getIdPengguna() == pelId) {
                        cbPelanggan.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        refreshTable();
        refreshPelanggan();
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

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Keranjang> list = model.getAll();
            for (Keranjang k : list) {
                tableModel.addRow(new Object[]{k.getIdKeranjang(), k.getIdPengguna(), k.getNamaPelanggan()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addKeranjang() {
        try {
            Pelanggan pl = (Pelanggan) cbPelanggan.getSelectedItem();
            if (pl == null) return;
            Keranjang k = new Keranjang();
            k.setIdPengguna(pl.getIdPengguna());
            model.insert(k);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateKeranjang() {
        if (selectedId == -1) return;
        try {
            Pelanggan pl = (Pelanggan) cbPelanggan.getSelectedItem();
            if (pl == null) return;
            Keranjang k = new Keranjang(selectedId, pl.getIdPengguna());
            model.update(k);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteKeranjang() {
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
