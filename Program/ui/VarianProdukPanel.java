package ui;

import entities.Produk;
import entities.VarianProduk;
import models.ProdukModel;
import models.VarianProdukModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class VarianProdukPanel extends JPanel {
    private VarianProdukModel model = new VarianProdukModel();
    private ProdukModel produkModel = new ProdukModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSku, txtStock, txtWarna, txtHarga, txtUkuran;
    private JComboBox<Produk> cbProduk;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedIdProduk = -1;
    private String selectedSku = null;

    public VarianProdukPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Produk", "Produk", "SKU", "Stock", "Warna", "Harga Varian", "Ukuran"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        formPanel.add(new JLabel("Produk:"));
        cbProduk = new JComboBox<>();
        formPanel.add(cbProduk);
        formPanel.add(new JLabel("SKU:"));
        txtSku = new JTextField();
        formPanel.add(txtSku);
        formPanel.add(new JLabel("Stock:"));
        txtStock = new JTextField();
        formPanel.add(txtStock);
        formPanel.add(new JLabel("Warna:"));
        txtWarna = new JTextField();
        formPanel.add(txtWarna);
        formPanel.add(new JLabel("Harga Varian:"));
        txtHarga = new JTextField();
        formPanel.add(txtHarga);
        formPanel.add(new JLabel("Ukuran:"));
        txtUkuran = new JTextField();
        formPanel.add(txtUkuran);

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
            refreshProduk();
        });
        btnAdd.addActionListener(e -> addVarian());
        btnUpdate.addActionListener(e -> updateVarian());
        btnDelete.addActionListener(e -> deleteVarian());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedIdProduk = (int) tableModel.getValueAt(row, 0);
                selectedSku = (String) tableModel.getValueAt(row, 2);
                txtSku.setText(selectedSku);
                txtStock.setText(tableModel.getValueAt(row, 3).toString());
                txtWarna.setText((String) tableModel.getValueAt(row, 4));
                txtHarga.setText(tableModel.getValueAt(row, 5).toString());
                txtUkuran.setText((String) tableModel.getValueAt(row, 6));
                
                // Select produk
                String prodName = (String) tableModel.getValueAt(row, 1);
                for (int i = 0; i < cbProduk.getItemCount(); i++) {
                    if (cbProduk.getItemAt(i).getNamaProduk().equals(prodName)) {
                        cbProduk.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        refreshTable();
        refreshProduk();
    }

    private void refreshProduk() {
        try {
            cbProduk.removeAllItems();
            List<Produk> list = produkModel.getAll();
            for (Produk p : list) {
                cbProduk.addItem(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Produk: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<VarianProduk> list = model.getAll();
            for (VarianProduk vp : list) {
                tableModel.addRow(new Object[]{vp.getIdProduk(), vp.getNamaProduk(), vp.getSku(), vp.getStock(), vp.getWarna(), vp.getHargaVarian(), vp.getUkuran()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addVarian() {
        try {
            Produk p = (Produk) cbProduk.getSelectedItem();
            if (p == null) return;
            VarianProduk vp = new VarianProduk();
            vp.setIdProduk(p.getIdProduk());
            vp.setSku(txtSku.getText());
            vp.setStock(Integer.parseInt(txtStock.getText()));
            vp.setWarna(txtWarna.getText());
            vp.setHargaVarian(new BigDecimal(txtHarga.getText()));
            vp.setUkuran(txtUkuran.getText());
            model.insert(vp);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateVarian() {
        if (selectedSku == null) return;
        try {
            Produk p = (Produk) cbProduk.getSelectedItem();
            if (p == null) return;
            VarianProduk vp = new VarianProduk(p.getIdProduk(), selectedSku, Integer.parseInt(txtStock.getText()), txtWarna.getText(), new BigDecimal(txtHarga.getText()), txtUkuran.getText());
            model.update(vp);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteVarian() {
        if (selectedSku == null) return;
        try {
            model.delete(selectedIdProduk, selectedSku);
            refreshTable();
            selectedSku = null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Delete: " + e.getMessage());
        }
    }
}
