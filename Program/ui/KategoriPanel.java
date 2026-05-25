package ui;

import entities.Kategori;
import models.KategoriModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class KategoriPanel extends JPanel {
    private KategoriModel model = new KategoriModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public KategoriPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama Kategori"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(2, 2));
        formPanel.add(new JLabel("Nama Kategori:"));
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
        btnRefresh.addActionListener(e -> refreshTable());
        btnAdd.addActionListener(e -> addKategori());
        btnUpdate.addActionListener(e -> updateKategori());
        btnDelete.addActionListener(e -> deleteKategori());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtNama.setText((String) tableModel.getValueAt(row, 1));
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Kategori> list = model.getAll();
            for (Kategori k : list) {
                tableModel.addRow(new Object[]{k.getIdKategori(), k.getNamaKategori()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addKategori() {
        try {
            Kategori k = new Kategori();
            k.setNamaKategori(txtNama.getText());
            model.insert(k);
            refreshTable();
            txtNama.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateKategori() {
        if (selectedId == -1) return;
        try {
            Kategori k = new Kategori(selectedId, txtNama.getText());
            model.update(k);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteKategori() {
        if (selectedId == -1) return;
        try {
            model.delete(selectedId);
            refreshTable();
            txtNama.setText("");
            selectedId = -1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
