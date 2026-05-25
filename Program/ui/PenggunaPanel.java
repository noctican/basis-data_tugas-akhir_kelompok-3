package ui;

import entities.Pengguna;
import models.PenggunaModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PenggunaPanel extends JPanel {
    private PenggunaModel model = new PenggunaModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtEmail, txtNamaDepan, txtNamaBelakang, txtNoTelp, txtPassword;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public PenggunaPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Email", "Nama Depan", "Nama Belakang", "No Telp", "Password"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Nama Depan:"));
        txtNamaDepan = new JTextField();
        formPanel.add(txtNamaDepan);
        formPanel.add(new JLabel("Nama Belakang:"));
        txtNamaBelakang = new JTextField();
        formPanel.add(txtNamaBelakang);
        formPanel.add(new JLabel("No Telp:"));
        txtNoTelp = new JTextField();
        formPanel.add(txtNoTelp);
        formPanel.add(new JLabel("Password:"));
        txtPassword = new JTextField();
        formPanel.add(txtPassword);

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
        btnAdd.addActionListener(e -> addPengguna());
        btnUpdate.addActionListener(e -> updatePengguna());
        btnDelete.addActionListener(e -> deletePengguna());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtEmail.setText((String) tableModel.getValueAt(row, 1));
                txtNamaDepan.setText((String) tableModel.getValueAt(row, 2));
                txtNamaBelakang.setText((String) tableModel.getValueAt(row, 3));
                txtNoTelp.setText((String) tableModel.getValueAt(row, 4));
                txtPassword.setText((String) tableModel.getValueAt(row, 5));
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Pengguna> list = model.getAll();
            for (Pengguna p : list) {
                tableModel.addRow(new Object[]{p.getIdPengguna(), p.getEmail(), p.getNamaDepan(), p.getNamaBelakang(), p.getNomorTelepon(), p.getPassword()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addPengguna() {
        try {
            Pengguna p = new Pengguna();
            p.setEmail(txtEmail.getText());
            p.setNamaDepan(txtNamaDepan.getText());
            p.setNamaBelakang(txtNamaBelakang.getText());
            p.setNomorTelepon(txtNoTelp.getText());
            p.setPassword(txtPassword.getText());
            model.insert(p);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updatePengguna() {
        if (selectedId == -1) return;
        try {
            Pengguna p = new Pengguna(selectedId, txtEmail.getText(), txtNamaDepan.getText(), txtNamaBelakang.getText(), txtNoTelp.getText(), txtPassword.getText());
            model.update(p);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deletePengguna() {
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
