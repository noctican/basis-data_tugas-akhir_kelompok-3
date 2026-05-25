package ui;

import entities.Pengiriman;
import entities.Transaksi;
import models.PengirimanModel;
import models.TransaksiModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class PengirimanPanel extends JPanel {
    private PengirimanModel model = new PengirimanModel();
    private TransaksiModel transaksiModel = new TransaksiModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNoResi, txtEkspedisi, txtJalan, txtPenerima, txtKota, txtNoTelp, txtKodePos, txtProvinsi, txtBiaya, txtTanggal;
    private JComboBox<Transaksi> cbTransaksi;
    private JComboBox<String> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private String selectedNoResi = null;
    private int selectedIdTransaksi = -1;

    public PengirimanPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"No Resi", "ID Transaksi", "Ekspedisi", "Status", "Tanggal"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(6, 4));
        formPanel.add(new JLabel("No Resi:"));
        txtNoResi = new JTextField();
        formPanel.add(txtNoResi);
        formPanel.add(new JLabel("Transaksi:"));
        cbTransaksi = new JComboBox<>();
        formPanel.add(cbTransaksi);
        
        formPanel.add(new JLabel("Ekspedisi:"));
        txtEkspedisi = new JTextField();
        formPanel.add(txtEkspedisi);
        formPanel.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"PROSES", "DIKIRIM", "SELESAI"});
        formPanel.add(cbStatus);
        
        formPanel.add(new JLabel("Penerima:"));
        txtPenerima = new JTextField();
        formPanel.add(txtPenerima);
        formPanel.add(new JLabel("No Telp:"));
        txtNoTelp = new JTextField();
        formPanel.add(txtNoTelp);
        
        formPanel.add(new JLabel("Jalan:"));
        txtJalan = new JTextField();
        formPanel.add(txtJalan);
        formPanel.add(new JLabel("Kota:"));
        txtKota = new JTextField();
        formPanel.add(txtKota);
        
        formPanel.add(new JLabel("Provinsi:"));
        txtProvinsi = new JTextField();
        formPanel.add(txtProvinsi);
        formPanel.add(new JLabel("Kode Pos:"));
        txtKodePos = new JTextField();
        formPanel.add(txtKodePos);
        
        formPanel.add(new JLabel("Biaya:"));
        txtBiaya = new JTextField();
        formPanel.add(txtBiaya);
        formPanel.add(new JLabel("Tgl (YYYY-MM-DD HH:MM:SS):"));
        txtTanggal = new JTextField();
        formPanel.add(txtTanggal);

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
        });
        btnAdd.addActionListener(e -> addPengiriman());
        btnUpdate.addActionListener(e -> updatePengiriman());
        btnDelete.addActionListener(e -> deletePengiriman());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedNoResi = (String) tableModel.getValueAt(row, 0);
                selectedIdTransaksi = (int) tableModel.getValueAt(row, 1);
                
                // Usually we'd fetch the full object to populate the other fields
                // but for brevity we'll just show the selected IDs in form
                txtNoResi.setText(selectedNoResi);
                cbStatus.setSelectedItem(tableModel.getValueAt(row, 3));
                txtTanggal.setText(tableModel.getValueAt(row, 4).toString());
                
                for (int i = 0; i < cbTransaksi.getItemCount(); i++) {
                    if (cbTransaksi.getItemAt(i).getIdTransaksi() == selectedIdTransaksi) {
                        cbTransaksi.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        refreshTable();
        refreshTransaksi();
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
            List<Pengiriman> list = model.getAll();
            for (Pengiriman p : list) {
                tableModel.addRow(new Object[]{p.getNoResi(), p.getIdTransaksi(), p.getNamaEkspedisi(), p.getStatusPengiriman(), p.getTanggalPengiriman()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addPengiriman() {
        try {
            Transaksi t = (Transaksi) cbTransaksi.getSelectedItem();
            if (t == null) return;
            Pengiriman p = new Pengiriman(
                txtNoResi.getText(), t.getIdTransaksi(), txtEkspedisi.getText(), txtJalan.getText(), txtPenerima.getText(),
                txtKota.getText(), txtNoTelp.getText(), Integer.parseInt(txtKodePos.getText()), txtProvinsi.getText(),
                cbStatus.getSelectedItem().toString(), new BigDecimal(txtBiaya.getText()), Timestamp.valueOf(txtTanggal.getText())
            );
            model.insert(p);
            refreshTable();
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updatePengiriman() {
        if (selectedNoResi == null) return;
        try {
            Transaksi t = (Transaksi) cbTransaksi.getSelectedItem();
            if (t == null) return;
            Pengiriman p = new Pengiriman(
                selectedNoResi, t.getIdTransaksi(), txtEkspedisi.getText(), txtJalan.getText(), txtPenerima.getText(),
                txtKota.getText(), txtNoTelp.getText(), Integer.parseInt(txtKodePos.getText()), txtProvinsi.getText(),
                cbStatus.getSelectedItem().toString(), new BigDecimal(txtBiaya.getText()), Timestamp.valueOf(txtTanggal.getText())
            );
            model.update(p);
            refreshTable();
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deletePengiriman() {
        if (selectedNoResi == null) return;
        try {
            model.delete(selectedIdTransaksi, selectedNoResi);
            refreshTable();
            selectedNoResi = null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Delete: " + e.getMessage());
        }
    }
}
