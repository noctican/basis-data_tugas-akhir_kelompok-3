package ui;

import entities.DetailKeranjang;
import entities.Keranjang;
import entities.VarianProduk;
import models.DetailKeranjangModel;
import models.KeranjangModel;
import models.VarianProdukModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class DetailKeranjangPanel extends JPanel {
    private DetailKeranjangModel model = new DetailKeranjangModel();
    private KeranjangModel keranjangModel = new KeranjangModel();
    private VarianProdukModel varianModel = new VarianProdukModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtKuantitas, txtSubTotal;
    private JComboBox<Keranjang> cbKeranjang;
    private JComboBox<VarianProduk> cbVarian;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedIdKeranjang = -1;
    private int selectedIdProduk = -1;
    private String selectedSku = null;

    public DetailKeranjangPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Keranjang", "ID Produk", "SKU", "Kuantitas", "Sub Total"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("Keranjang:"));
        cbKeranjang = new JComboBox<>();
        formPanel.add(cbKeranjang);
        formPanel.add(new JLabel("Varian Produk:"));
        cbVarian = new JComboBox<>();
        formPanel.add(cbVarian);
        formPanel.add(new JLabel("Kuantitas:"));
        txtKuantitas = new JTextField();
        formPanel.add(txtKuantitas);
        formPanel.add(new JLabel("Sub Total:"));
        txtSubTotal = new JTextField();
        formPanel.add(txtSubTotal);

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
            refreshKeranjang();
            refreshVarian();
        });
        btnAdd.addActionListener(e -> addDetail());
        btnUpdate.addActionListener(e -> updateDetail());
        btnDelete.addActionListener(e -> deleteDetail());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedIdKeranjang = (int) tableModel.getValueAt(row, 0);
                selectedIdProduk = (int) tableModel.getValueAt(row, 1);
                selectedSku = (String) tableModel.getValueAt(row, 2);
                txtKuantitas.setText(tableModel.getValueAt(row, 3).toString());
                txtSubTotal.setText(tableModel.getValueAt(row, 4).toString());
                
                // Select combo boxes
                for (int i = 0; i < cbKeranjang.getItemCount(); i++) {
                    if (cbKeranjang.getItemAt(i).getIdKeranjang() == selectedIdKeranjang) {
                        cbKeranjang.setSelectedIndex(i);
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
        refreshKeranjang();
        refreshVarian();
    }

    private void refreshKeranjang() {
        try {
            cbKeranjang.removeAllItems();
            List<Keranjang> list = keranjangModel.getAll();
            for (Keranjang k : list) {
                cbKeranjang.addItem(k);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Keranjang: " + e.getMessage());
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
            List<DetailKeranjang> list = model.getAll();
            for (DetailKeranjang dk : list) {
                tableModel.addRow(new Object[]{dk.getIdKeranjang(), dk.getIdProduk(), dk.getSku(), dk.getKuantitas(), dk.getSubTotal()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addDetail() {
        try {
            Keranjang k = (Keranjang) cbKeranjang.getSelectedItem();
            VarianProduk vp = (VarianProduk) cbVarian.getSelectedItem();
            if (k == null || vp == null) return;
            DetailKeranjang dk = new DetailKeranjang(k.getIdKeranjang(), vp.getIdProduk(), vp.getSku(), Integer.parseInt(txtKuantitas.getText()), new BigDecimal(txtSubTotal.getText()));
            model.insert(dk);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateDetail() {
        if (selectedSku == null) return;
        try {
            DetailKeranjang dk = new DetailKeranjang(selectedIdKeranjang, selectedIdProduk, selectedSku, Integer.parseInt(txtKuantitas.getText()), new BigDecimal(txtSubTotal.getText()));
            model.update(dk);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteDetail() {
        if (selectedSku == null) return;
        try {
            model.delete(selectedIdKeranjang, selectedIdProduk, selectedSku);
            refreshTable();
            selectedSku = null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Delete: " + e.getMessage());
        }
    }
}
