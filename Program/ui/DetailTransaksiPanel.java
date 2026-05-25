package ui;

import entities.DetailTransaksi;
import entities.Transaksi;
import entities.VarianProduk;
import models.DetailTransaksiModel;
import models.TransaksiModel;
import models.VarianProdukModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class DetailTransaksiPanel extends JPanel {
    private DetailTransaksiModel model = new DetailTransaksiModel();
    private TransaksiModel transaksiModel = new TransaksiModel();
    private VarianProdukModel varianModel = new VarianProdukModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtHarga, txtKuantitas;
    private JComboBox<Transaksi> cbTransaksi;
    private JComboBox<VarianProduk> cbVarian;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedIdTransaksi = -1;
    private int selectedIdProduk = -1;
    private String selectedSku = null;

    public DetailTransaksiPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Transaksi", "ID Produk", "SKU", "Harga Beli", "Kuantitas"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("Transaksi:"));
        cbTransaksi = new JComboBox<>();
        formPanel.add(cbTransaksi);
        formPanel.add(new JLabel("Varian Produk:"));
        cbVarian = new JComboBox<>();
        formPanel.add(cbVarian);
        formPanel.add(new JLabel("Harga Pembelian:"));
        txtHarga = new JTextField();
        formPanel.add(txtHarga);
        formPanel.add(new JLabel("Kuantitas:"));
        txtKuantitas = new JTextField();
        formPanel.add(txtKuantitas);

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
            refreshVarian();
        });
        btnAdd.addActionListener(e -> addDetail());
        btnUpdate.addActionListener(e -> updateDetail());
        btnDelete.addActionListener(e -> deleteDetail());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedIdTransaksi = (int) tableModel.getValueAt(row, 0);
                selectedIdProduk = (int) tableModel.getValueAt(row, 1);
                selectedSku = (String) tableModel.getValueAt(row, 2);
                txtHarga.setText(tableModel.getValueAt(row, 3).toString());
                txtKuantitas.setText(tableModel.getValueAt(row, 4).toString());
                
                for (int i = 0; i < cbTransaksi.getItemCount(); i++) {
                    if (cbTransaksi.getItemAt(i).getIdTransaksi() == selectedIdTransaksi) {
                        cbTransaksi.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < cbVarian.getItemCount(); i++) {
                    VarianProduk vp = cbVarian.getItemAt(i);
                    if (vp.getIdProduk() == selectedIdProduk && vp.getSku().equals(selectedSku)) {
                        cbVarian.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        refreshTable();
        refreshTransaksi();
        refreshVarian();
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

    private void refreshVarian() {
        try {
            cbVarian.removeAllItems();
            List<VarianProduk> list = varianModel.getAll();
            for (VarianProduk vp : list) {
                cbVarian.addItem(vp);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Varian: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<DetailTransaksi> list = model.getAll();
            for (DetailTransaksi dt : list) {
                tableModel.addRow(new Object[]{dt.getIdTransaksi(), dt.getIdProduk(), dt.getSku(), dt.getHargaPembelian(), dt.getKuantitas()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addDetail() {
        try {
            Transaksi t = (Transaksi) cbTransaksi.getSelectedItem();
            VarianProduk vp = (VarianProduk) cbVarian.getSelectedItem();
            if (t == null || vp == null) return;
            DetailTransaksi dt = new DetailTransaksi(vp.getIdProduk(), vp.getSku(), t.getIdTransaksi(), new BigDecimal(txtHarga.getText()), Integer.parseInt(txtKuantitas.getText()));
            model.insert(dt);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateDetail() {
        if (selectedSku == null) return;
        try {
            DetailTransaksi dt = new DetailTransaksi(selectedIdProduk, selectedSku, selectedIdTransaksi, new BigDecimal(txtHarga.getText()), Integer.parseInt(txtKuantitas.getText()));
            model.update(dt);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteDetail() {
        if (selectedSku == null) return;
        try {
            model.delete(selectedIdTransaksi, selectedIdProduk, selectedSku);
            refreshTable();
            selectedSku = null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Delete: " + e.getMessage());
        }
    }
}
