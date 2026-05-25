package ui;

import entities.Departemen;
import entities.Karyawan;
import entities.Pengguna;
import models.DepartemenModel;
import models.KaryawanModel;
import models.PenggunaModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class KaryawanPanel extends JPanel {
    private KaryawanModel model = new KaryawanModel();
    private PenggunaModel penggunaModel = new PenggunaModel();
    private DepartemenModel departemenModel = new DepartemenModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtJabatan;
    private JComboBox<Pengguna> cbPengguna;
    private JComboBox<Departemen> cbDepartemen;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public KaryawanPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Pengguna", "Nama", "Departemen", "Jabatan"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("Pengguna:"));
        cbPengguna = new JComboBox<>();
        formPanel.add(cbPengguna);
        formPanel.add(new JLabel("Departemen:"));
        cbDepartemen = new JComboBox<>();
        formPanel.add(cbDepartemen);
        formPanel.add(new JLabel("Jabatan:"));
        txtJabatan = new JTextField();
        formPanel.add(txtJabatan);

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
            refreshPengguna();
            refreshDepartemen();
        });
        btnAdd.addActionListener(e -> addKaryawan());
        btnUpdate.addActionListener(e -> updateKaryawan());
        btnDelete.addActionListener(e -> deleteKaryawan());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtJabatan.setText((String) tableModel.getValueAt(row, 3));
                
                // Select combo boxes (matching by string/id)
                String depName = (String) tableModel.getValueAt(row, 2);
                for (int i = 0; i < cbDepartemen.getItemCount(); i++) {
                    if (cbDepartemen.getItemAt(i).getNamaDepartemen().equals(depName)) {
                        cbDepartemen.setSelectedIndex(i);
                        break;
                    }
                }
                
                for (int i = 0; i < cbPengguna.getItemCount(); i++) {
                    if (cbPengguna.getItemAt(i).getIdPengguna() == selectedId) {
                        cbPengguna.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        refreshTable();
        refreshPengguna();
        refreshDepartemen();
    }

    private void refreshPengguna() {
        try {
            cbPengguna.removeAllItems();
            List<Pengguna> list = penggunaModel.getAll();
            for (Pengguna p : list) {
                cbPengguna.addItem(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Pengguna: " + e.getMessage());
        }
    }

    private void refreshDepartemen() {
        try {
            cbDepartemen.removeAllItems();
            List<Departemen> list = departemenModel.getAll();
            for (Departemen d : list) {
                cbDepartemen.addItem(d);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Departemen: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Karyawan> list = model.getAll();
            for (Karyawan k : list) {
                tableModel.addRow(new Object[]{k.getIdPengguna(), k.getNamaLengkap(), k.getNamaDepartemen(), k.getJabatan()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addKaryawan() {
        try {
            Pengguna p = (Pengguna) cbPengguna.getSelectedItem();
            Departemen d = (Departemen) cbDepartemen.getSelectedItem();
            if (p == null || d == null) return;
            Karyawan k = new Karyawan(p.getIdPengguna(), d.getIdDepartemen(), txtJabatan.getText());
            model.insert(k);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateKaryawan() {
        if (selectedId == -1) return;
        try {
            Departemen d = (Departemen) cbDepartemen.getSelectedItem();
            if (d == null) return;
            Karyawan k = new Karyawan(selectedId, d.getIdDepartemen(), txtJabatan.getText());
            model.update(k);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteKaryawan() {
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
