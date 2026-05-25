package ui;

import entities.Departemen;
import entities.Karyawan;
import models.DepartemenModel;
import models.KaryawanModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DepartemenPanel extends JPanel {
    private DepartemenModel model = new DepartemenModel();
    private KaryawanModel karyawanModel = new KaryawanModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtLokasi, txtDeskripsi;
    private JComboBox<Object> cbManajer; // Use Object to handle "None"
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public DepartemenPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama Departemen", "Manajer ID", "Lokasi", "Deskripsi"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("Nama Departemen:"));
        txtNama = new JTextField();
        formPanel.add(txtNama);
        formPanel.add(new JLabel("Manajer:"));
        cbManajer = new JComboBox<>();
        formPanel.add(cbManajer);
        formPanel.add(new JLabel("Lokasi:"));
        txtLokasi = new JTextField();
        formPanel.add(txtLokasi);
        formPanel.add(new JLabel("Deskripsi:"));
        txtDeskripsi = new JTextField();
        formPanel.add(txtDeskripsi);

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
            refreshManajer();
        });
        btnAdd.addActionListener(e -> addDepartemen());
        btnUpdate.addActionListener(e -> updateDepartemen());
        btnDelete.addActionListener(e -> deleteDepartemen());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtNama.setText((String) tableModel.getValueAt(row, 1));
                txtLokasi.setText((String) tableModel.getValueAt(row, 3));
                txtDeskripsi.setText((String) tableModel.getValueAt(row, 4));
                
                Object manId = tableModel.getValueAt(row, 2);
                if (manId == null) {
                    cbManajer.setSelectedIndex(0);
                } else {
                    for (int i = 1; i < cbManajer.getItemCount(); i++) {
                        Karyawan k = (Karyawan) cbManajer.getItemAt(i);
                        if (k.getIdPengguna() == (int) manId) {
                            cbManajer.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        refreshTable();
        refreshManajer();
    }

    private void refreshManajer() {
        try {
            cbManajer.removeAllItems();
            cbManajer.addItem("None");
            List<Karyawan> list = karyawanModel.getAll();
            for (Karyawan k : list) {
                cbManajer.addItem(k);
            }
        } catch (SQLException e) {
            // Might fail if karyawan table is empty or has issues
            cbManajer.addItem("None");
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Departemen> list = model.getAll();
            for (Departemen d : list) {
                tableModel.addRow(new Object[]{d.getIdDepartemen(), d.getNamaDepartemen(), d.getIdManajer(), d.getLokasi(), d.getDeskripsiTugas()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addDepartemen() {
        try {
            Departemen d = new Departemen();
            d.setNamaDepartemen(txtNama.getText());
            d.setLokasi(txtLokasi.getText());
            d.setDeskripsiTugas(txtDeskripsi.getText());
            
            Object selected = cbManajer.getSelectedItem();
            if (selected instanceof Karyawan) {
                d.setIdManajer(((Karyawan) selected).getIdPengguna());
            } else {
                d.setIdManajer(null);
            }
            
            model.insert(d);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateDepartemen() {
        if (selectedId == -1) return;
        try {
            Departemen d = new Departemen(selectedId, null, txtNama.getText(), txtLokasi.getText(), txtDeskripsi.getText());
            Object selected = cbManajer.getSelectedItem();
            if (selected instanceof Karyawan) {
                d.setIdManajer(((Karyawan) selected).getIdPengguna());
            }
            model.update(d);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteDepartemen() {
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
