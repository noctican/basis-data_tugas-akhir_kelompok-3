package ui;

import entities.AlamatPelanggan;
import entities.Pelanggan;
import models.AlamatPelangganModel;
import models.PelangganModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AlamatPelangganPanel extends JPanel {
    private AlamatPelangganModel model = new AlamatPelangganModel();
    private PelangganModel pelangganModel = new PelangganModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtProvinsi, txtKota, txtJalan, txtPenerima, txtNoTelp;
    private JComboBox<Pelanggan> cbPelanggan;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedIdPengguna = -1;
    private int selectedIdAlamat = -1;

    public AlamatPelangganPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Pengguna", "Pelanggan", "ID Alamat", "Provinsi", "Kota", "Jalan", "Penerima", "No Telp"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        formPanel.add(new JLabel("Pelanggan:"));
        cbPelanggan = new JComboBox<>();
        formPanel.add(cbPelanggan);
        formPanel.add(new JLabel("Provinsi:"));
        txtProvinsi = new JTextField();
        formPanel.add(txtProvinsi);
        formPanel.add(new JLabel("Kota:"));
        txtKota = new JTextField();
        formPanel.add(txtKota);
        formPanel.add(new JLabel("Jalan:"));
        txtJalan = new JTextField();
        formPanel.add(txtJalan);
        formPanel.add(new JLabel("Penerima:"));
        txtPenerima = new JTextField();
        formPanel.add(txtPenerima);
        formPanel.add(new JLabel("No Telp:"));
        txtNoTelp = new JTextField();
        formPanel.add(txtNoTelp);

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
            refreshPelanggan();
        });
        btnAdd.addActionListener(e -> addAlamat());
        btnUpdate.addActionListener(e -> updateAlamat());
        btnDelete.addActionListener(e -> deleteAlamat());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedIdPengguna = (int) tableModel.getValueAt(row, 0);
                selectedIdAlamat = (int) tableModel.getValueAt(row, 2);
                txtProvinsi.setText((String) tableModel.getValueAt(row, 3));
                txtKota.setText((String) tableModel.getValueAt(row, 4));
                txtJalan.setText((String) tableModel.getValueAt(row, 5));
                txtPenerima.setText((String) tableModel.getValueAt(row, 6));
                txtNoTelp.setText((String) tableModel.getValueAt(row, 7));
                
                // Select pelanggan
                for (int i = 0; i < cbPelanggan.getItemCount(); i++) {
                    if (cbPelanggan.getItemAt(i).getIdPengguna() == selectedIdPengguna) {
                        cbPelanggan.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        refreshTable();
        refreshPelanggan();
    }

    private void refreshPelanggan() {
        try {
            cbPelanggan.removeAllItems();
            List<Pelanggan> list = pelangganModel.getAll();
            for (Pelanggan pl : list) {
                cbPelanggan.addItem(pl);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Pelanggan: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<AlamatPelanggan> list = model.getAll();
            for (AlamatPelanggan ap : list) {
                tableModel.addRow(new Object[]{ap.getIdPengguna(), ap.getNamaPelanggan(), ap.getIdAlamat(), ap.getProvinsi(), ap.getKota(), ap.getJalan(), ap.getNamaPenerima(), ap.getNoTelp()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addAlamat() {
        try {
            Pelanggan pl = (Pelanggan) cbPelanggan.getSelectedItem();
            if (pl == null) return;
            AlamatPelanggan ap = new AlamatPelanggan();
            ap.setIdPengguna(pl.getIdPengguna());
            ap.setProvinsi(txtProvinsi.getText());
            ap.setKota(txtKota.getText());
            ap.setJalan(txtJalan.getText());
            ap.setNamaPenerima(txtPenerima.getText());
            ap.setNoTelp(txtNoTelp.getText());
            model.insert(ap);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateAlamat() {
        if (selectedIdAlamat == -1) return;
        try {
            Pelanggan pl = (Pelanggan) cbPelanggan.getSelectedItem();
            if (pl == null) return;
            AlamatPelanggan ap = new AlamatPelanggan(pl.getIdPengguna(), selectedIdAlamat, txtProvinsi.getText(), txtKota.getText(), txtJalan.getText(), txtPenerima.getText(), txtNoTelp.getText());
            model.update(ap);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteAlamat() {
        if (selectedIdAlamat == -1) return;
        try {
            model.delete(selectedIdPengguna, selectedIdAlamat);
            refreshTable();
            selectedIdAlamat = -1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Delete: " + e.getMessage());
        }
    }
}
