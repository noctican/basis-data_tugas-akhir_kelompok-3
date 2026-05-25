package ui;

import entities.Karyawan;
import entities.Retur;
import entities.Transaksi;
import models.KaryawanModel;
import models.ReturModel;
import models.TransaksiModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ReturPanel extends JPanel {
    private ReturModel model = new ReturModel();
    private TransaksiModel transaksiModel = new TransaksiModel();
    private KaryawanModel karyawanModel = new KaryawanModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTanggal;
    private JComboBox<Transaksi> cbTransaksi;
    private JComboBox<Karyawan> cbValidator;
    private JComboBox<String> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public ReturPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Retur", "ID Transaksi", "Tanggal", "Status", "Validator ID"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("Transaksi:"));
        cbTransaksi = new JComboBox<>();
        formPanel.add(cbTransaksi);
        formPanel.add(new JLabel("Tanggal (YYYY-MM-DD):"));
        txtTanggal = new JTextField();
        formPanel.add(txtTanggal);
        formPanel.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"PENDING", "APPROVED", "REJECTED"});
        formPanel.add(cbStatus);
        formPanel.add(new JLabel("Validator (Karyawan):"));
        cbValidator = new JComboBox<>();
        formPanel.add(cbValidator);

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
            refreshTransaksi();
            refreshValidator();
        });
        btnAdd.addActionListener(e -> addRetur());
        btnUpdate.addActionListener(e -> updateRetur());
        btnDelete.addActionListener(e -> deleteRetur());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtTanggal.setText(tableModel.getValueAt(row, 2).toString());
                cbStatus.setSelectedItem(tableModel.getValueAt(row, 3));
                
                Object transId = tableModel.getValueAt(row, 1);
                if (transId != null) {
                    for (int i = 0; i < cbTransaksi.getItemCount(); i++) {
                        if (cbTransaksi.getItemAt(i).getIdTransaksi() == (int) transId) {
                            cbTransaksi.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                
                Object valId = tableModel.getValueAt(row, 4);
                if (valId != null) {
                    for (int i = 0; i < cbValidator.getItemCount(); i++) {
                        if (cbValidator.getItemAt(i).getIdPengguna() == (int) valId) {
                            cbValidator.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        refreshTable();
        refreshTransaksi();
        refreshValidator();
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

    private void refreshValidator() {
        try {
            cbValidator.removeAllItems();
            List<Karyawan> list = karyawanModel.getAll();
            for (Karyawan k : list) {
                cbValidator.addItem(k);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Karyawan: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Retur> list = model.getAll();
            for (Retur r : list) {
                tableModel.addRow(new Object[]{r.getIdRetur(), r.getIdTransaksi(), r.getTanggalPengembalian(), r.getStatus(), r.getIdValidator()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addRetur() {
        try {
            Transaksi t = (Transaksi) cbTransaksi.getSelectedItem();
            Karyawan k = (Karyawan) cbValidator.getSelectedItem();
            if (t == null) return;
            Retur r = new Retur();
            r.setIdTransaksi(t.getIdTransaksi());
            r.setTanggalPengembalian(Date.valueOf(txtTanggal.getText()));
            r.setStatus(cbStatus.getSelectedItem().toString());
            if (k != null) r.setIdValidator(k.getIdPengguna());
            model.insert(r);
            refreshTable();
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateRetur() {
        if (selectedId == -1) return;
        try {
            Transaksi t = (Transaksi) cbTransaksi.getSelectedItem();
            Karyawan k = (Karyawan) cbValidator.getSelectedItem();
            if (t == null) return;
            Retur r = new Retur(selectedId, t.getIdTransaksi(), Date.valueOf(txtTanggal.getText()), cbStatus.getSelectedItem().toString(), k != null ? k.getIdPengguna() : null);
            model.update(r);
            refreshTable();
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteRetur() {
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
