package ui;

import entities.*;
import helpers.GUIHelper;
import models.UserModel;
import session.UserSession;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ProfilePanel extends JPanel {
    private UserModel userModel;
    private JTable alamatTable;
    private DefaultTableModel alamatModel;

    public ProfilePanel() {
        userModel = new UserModel();
        setLayout(new BorderLayout());

        setupProfileHeader();
        setupAlamatTable();
        setupBottomPanel();

        refreshAlamat();
    }

    private void setupProfileHeader() {
        Pengguna u = UserSession.getCurrentUser();
        JPanel header = GUIHelper.createContentHeader("Welcome, " + u.getFullName());
        add(header, BorderLayout.NORTH);
    }

    private void setupAlamatTable() {
        alamatModel = new DefaultTableModel(new String[]{"ID", "Penerima", "Jalan", "Kota", "Provinsi", "No Telp"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        alamatTable = new JTable(alamatModel);
        add(new JScrollPane(alamatTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton addBtn = new JButton("Add New Address");
        addBtn.addActionListener(e -> showAddAlamatDialog());
        
        JButton editAlamatBtn = new JButton("Edit Selected Address");
        editAlamatBtn.addActionListener(e -> handleEditAlamat());
        
        JButton deleteAlamatBtn = new JButton("Delete Selected Address");
        deleteAlamatBtn.addActionListener(e -> handleDeleteAlamat());
        
        bottomPanel.add(addBtn);
        bottomPanel.add(editAlamatBtn); 
        bottomPanel.add(deleteAlamatBtn); 

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleEditAlamat() {
        int selectedRow = alamatTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIHelper.showInfo(this, "Silakan pilih salah satu alamat di tabel terlebih dahulu!");
            return;
        }

        int idAlamat = (int) alamatModel.getValueAt(selectedRow, 0);
        String oldPenerima = (String) alamatModel.getValueAt(selectedRow, 1);
        String oldJalan = (String) alamatModel.getValueAt(selectedRow, 2);
        String oldKota = (String) alamatModel.getValueAt(selectedRow, 3);
        String oldProvinsi = (String) alamatModel.getValueAt(selectedRow, 4);
        String oldTelp = (String) alamatModel.getValueAt(selectedRow, 5);

        JTextField recipientF = new JTextField(oldPenerima);
        JTextField streetF = new JTextField(oldJalan);
        JTextField cityF = new JTextField(oldKota);
        JTextField provF = new JTextField(oldProvinsi);
        JTextField phoneF = new JTextField(oldTelp);

        Object[] message = {
            "Recipient Name:", recipientF, 
            "Street:", streetF, 
            "City:", cityF, 
            "Province:", provF, 
            "Phone:", phoneF
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Edit Address", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            boolean success = userModel.updateAlamat(idAlamat, provF.getText(), cityF.getText(), streetF.getText(), recipientF.getText(), phoneF.getText());
            if (success) {
                JOptionPane.showMessageDialog(this, "Alamat berhasil diperbarui!");
                refreshAlamat(); // Reload tabel otomatis
            } else {
                GUIHelper.showInfo(this, "Gagal memperbarui alamat.");
            }
        }
    }

    private void handleDeleteAlamat() {
        int selectedRow = alamatTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIHelper.showInfo(this, "Silakan pilih salah satu alamat di tabel yang ingin dihapus!");
            return;
        }

        int idAlamat = (int) alamatModel.getValueAt(selectedRow, 0);
        String namaPenerima = (String) alamatModel.getValueAt(selectedRow, 1);

        int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus alamat atas nama " + namaPenerima + "?", 
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            boolean success = userModel.deleteAlamat(idAlamat);
            if (success) {
                JOptionPane.showMessageDialog(this, "Alamat berhasil dihapus!");
                refreshAlamat(); // Reload tabel otomatis
            } else {
                GUIHelper.showInfo(this, "Gagal menghapus alamat.");
            }
        }
    }

    private void refreshAlamat() {
        alamatModel.setRowCount(0);
        List<AlamatPelanggan> list = userModel.getAlamatByCustomer(UserSession.getCurrentUser().getIdPengguna());
        for (AlamatPelanggan a : list) {
            alamatModel.addRow(new Object[]{a.getIdAlamat(), a.getNamaPenerima(), a.getJalan(), a.getKota(), a.getProvinsi(), a.getNoTelp()});
        }
    }

private void showAddAlamatDialog() {
        JTextField recipientF = new JTextField();
        JTextField streetF = new JTextField();
        JTextField cityF = new JTextField();
        JTextField provF = new JTextField();
        JTextField phoneF = new JTextField();

        Object[] message = {
            "Recipient Name:", recipientF, 
            "Street:", streetF, 
            "City:", cityF, 
            "Province:", provF, 
            "Phone:", phoneF
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add Address", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            if (recipientF.getText().trim().isEmpty() || streetF.getText().trim().isEmpty()) {
                GUIHelper.showInfo(this, "Nama Penerima dan Jalan tidak boleh kosong!");
                return;
            }

            int idPenggunaLogin = UserSession.getCurrentUser().getIdPengguna();
            
            boolean success = userModel.addAlamat(
                idPenggunaLogin, 
                provF.getText(), 
                cityF.getText(), 
                streetF.getText(), 
                recipientF.getText(), 
                phoneF.getText()
            );
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Alamat berhasil ditambahkan!");
                refreshAlamat(); 
            } else {
                GUIHelper.showInfo(this, "Gagal menambahkan alamat. Silakan cek log konsol.");
            }
        }
    }

}
