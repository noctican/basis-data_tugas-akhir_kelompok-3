package ui;

import entities.Produk;
import entities.SubKategori;
import models.ProdukModel;
import models.SubKategoriModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProdukPanel extends JPanel {
    private ProdukModel model = new ProdukModel();
    private SubKategoriModel subKategoriModel = new SubKategoriModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtDeskripsi, txtHarga, txtAktivitas;
    private JComboBox<SubKategori> cbSubKategori;
    private JComboBox<String> cbDemografi;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public ProdukPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Sub Kategori", "Nama Produk", "Harga Base", "Demografi", "Aktivitas"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(7, 2));
        formPanel.add(new JLabel("Sub Kategori:"));
        cbSubKategori = new JComboBox<>();
        formPanel.add(cbSubKategori);
        formPanel.add(new JLabel("Nama Produk:"));
        txtNama = new JTextField();
        formPanel.add(txtNama);
        formPanel.add(new JLabel("Deskripsi:"));
        txtDeskripsi = new JTextField();
        formPanel.add(txtDeskripsi);
        formPanel.add(new JLabel("Harga Base:"));
        txtHarga = new JTextField();
        formPanel.add(txtHarga);
        formPanel.add(new JLabel("Demografi:"));
        cbDemografi = new JComboBox<>(new String[]{"pria", "wanita", "anak", "equipment"});
        formPanel.add(cbDemografi);
        formPanel.add(new JLabel("Aktivitas:"));
        txtAktivitas = new JTextField();
        formPanel.add(txtAktivitas);

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
            refreshSubKategori();
        });
        btnAdd.addActionListener(e -> addProduk());
        btnUpdate.addActionListener(e -> updateProduk());
        btnDelete.addActionListener(e -> deleteProduk());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtNama.setText((String) tableModel.getValueAt(row, 2));
                txtHarga.setText(tableModel.getValueAt(row, 3).toString());
                cbDemografi.setSelectedItem(tableModel.getValueAt(row, 4));
                txtAktivitas.setText((String) tableModel.getValueAt(row, 5));
                
                // Select sub kategori
                String subName = (String) tableModel.getValueAt(row, 1);
                for (int i = 0; i < cbSubKategori.getItemCount(); i++) {
                    if (cbSubKategori.getItemAt(i).getNamaSubKategori().equals(subName)) {
                        cbSubKategori.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        refreshTable();
        refreshSubKategori();
    }

    private void refreshSubKategori() {
        try {
            cbSubKategori.removeAllItems();
            List<SubKategori> list = subKategoriModel.getAll();
            for (SubKategori sk : list) {
                cbSubKategori.addItem(sk);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error SubKategori: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Produk> list = model.getAll();
            for (Produk p : list) {
                tableModel.addRow(new Object[]{p.getIdProduk(), p.getNamaSubKategori(), p.getNamaProduk(), p.getHargaBase(), p.getDemografi(), p.getAktivitas()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addProduk() {
        try {
            SubKategori sk = (SubKategori) cbSubKategori.getSelectedItem();
            if (sk == null) return;
            Produk p = new Produk();
            p.setIdKategori(sk.getIdKategori());
            p.setIdSubKategori(sk.getIdSubKategori());
            p.setNamaProduk(txtNama.getText());
            p.setDeskripsiProduk(txtDeskripsi.getText());
            p.setHargaBase(new BigDecimal(txtHarga.getText()));
            p.setDemografi(cbDemografi.getSelectedItem().toString());
            p.setAktivitas(txtAktivitas.getText());
            model.insert(p);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateProduk() {
        if (selectedId == -1) return;
        try {
            SubKategori sk = (SubKategori) cbSubKategori.getSelectedItem();
            if (sk == null) return;
            Produk p = new Produk(selectedId, sk.getIdKategori(), sk.getIdSubKategori(), txtNama.getText(), txtDeskripsi.getText(), new BigDecimal(txtHarga.getText()), cbDemografi.getSelectedItem().toString(), txtAktivitas.getText());
            model.update(p);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteProduk() {
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
