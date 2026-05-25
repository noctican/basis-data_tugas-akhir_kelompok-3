package ui;

import entities.Kategori;
import entities.SubKategori;
import models.KategoriModel;
import models.SubKategoriModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SubKategoriPanel extends JPanel {
    private SubKategoriModel model = new SubKategoriModel();
    private KategoriModel kategoriModel = new KategoriModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama;
    private JComboBox<Kategori> cbKategori;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedIdKategori = -1;
    private int selectedIdSubKategori = -1;

    public SubKategoriPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Kategori", "Kategori", "ID Sub", "Nama Sub Kategori"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("Kategori:"));
        cbKategori = new JComboBox<>();
        formPanel.add(cbKategori);
        formPanel.add(new JLabel("Nama Sub Kategori:"));
        txtNama = new JTextField();
        formPanel.add(txtNama);

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
            refreshKategori();
        });
        btnAdd.addActionListener(e -> addSubKategori());
        btnUpdate.addActionListener(e -> updateSubKategori());
        btnDelete.addActionListener(e -> deleteSubKategori());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedIdKategori = (int) tableModel.getValueAt(row, 0);
                selectedIdSubKategori = (int) tableModel.getValueAt(row, 2);
                txtNama.setText((String) tableModel.getValueAt(row, 3));
                
                // Select in combo box
                String katName = (String) tableModel.getValueAt(row, 1);
                for (int i = 0; i < cbKategori.getItemCount(); i++) {
                    if (cbKategori.getItemAt(i).getNamaKategori().equals(katName)) {
                        cbKategori.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        refreshTable();
        refreshKategori();
    }

    private void refreshKategori() {
        try {
            cbKategori.removeAllItems();
            List<Kategori> list = kategoriModel.getAll();
            for (Kategori k : list) {
                cbKategori.addItem(k);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Kategori: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<SubKategori> list = model.getAll();
            for (SubKategori sk : list) {
                tableModel.addRow(new Object[]{sk.getIdKategori(), sk.getNamaKategori(), sk.getIdSubKategori(), sk.getNamaSubKategori()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addSubKategori() {
        try {
            Kategori k = (Kategori) cbKategori.getSelectedItem();
            if (k == null) return;
            SubKategori sk = new SubKategori();
            sk.setIdKategori(k.getIdKategori());
            sk.setNamaSubKategori(txtNama.getText());
            model.insert(sk);
            refreshTable();
            txtNama.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateSubKategori() {
        if (selectedIdSubKategori == -1) return;
        try {
            Kategori k = (Kategori) cbKategori.getSelectedItem();
            if (k == null) return;
            SubKategori sk = new SubKategori(k.getIdKategori(), selectedIdSubKategori, txtNama.getText());
            model.update(sk);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteSubKategori() {
        if (selectedIdSubKategori == -1) return;
        try {
            model.delete(selectedIdKategori, selectedIdSubKategori);
            refreshTable();
            txtNama.setText("");
            selectedIdKategori = -1;
            selectedIdSubKategori = -1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Delete: " + e.getMessage());
        }
    }
}
