package ui;

import entities.*;
import helpers.GUIHelper;
import models.UserModel;
import session.UserSession;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProfilePanel extends JPanel {
    private UserModel userModel;
    private JTable alamatTable, topupTable;
    private DefaultTableModel alamatModel, topupModel;
    private JLabel nameL, emailL, phoneL, walletL;

    public ProfilePanel() {
        userModel = new UserModel();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setupProfileInfo();
        
        if (UserSession.getRole() == UserSession.Role.PELANGGAN) {
            setupCustomerSpecifics();
        }
    }

    private void setupProfileInfo() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("My Profile"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        Pengguna u = UserSession.getCurrentUser();
        nameL = new JLabel("Name: " + u.getFullName());
        emailL = new JLabel("Email: " + u.getEmail());
        phoneL = new JLabel("Phone: " + (u.getNomorTelepon() != null ? u.getNomorTelepon() : "-"));
        
        gbc.gridx = 0; gbc.gridy = 0; infoPanel.add(nameL, gbc);
        gbc.gridx = 0; gbc.gridy = 1; infoPanel.add(emailL, gbc);
        gbc.gridx = 0; gbc.gridy = 2; infoPanel.add(phoneL, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editBtn = new JButton("Edit Profile");
        JButton passBtn = new JButton("Edit Password");
        
        editBtn.addActionListener(e -> showEditProfileDialog());
        passBtn.addActionListener(e -> showChangePasswordDialog());
        
        btnPanel.add(editBtn);
        btnPanel.add(passBtn);
        gbc.gridx = 0; gbc.gridy = 3; infoPanel.add(btnPanel, gbc);

        add(infoPanel, BorderLayout.NORTH);
    }

    private void setupCustomerSpecifics() {
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        
        // Addresses
        JPanel aPanel = new JPanel(new BorderLayout());
        aPanel.setBorder(BorderFactory.createTitledBorder("My Addresses"));
        alamatModel = new DefaultTableModel(new String[]{"ID", "Recipient", "Street", "City", "Province", "Phone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        alamatTable = new JTable(alamatModel);
        aPanel.add(new JScrollPane(alamatTable), BorderLayout.CENTER);
        
        JPanel addressBtnPanel = new JPanel(new BorderLayout());
        JButton addAddressBtn = new JButton("Add Address");
        addAddressBtn.addActionListener(e -> showAddressDialog(null, "", "", "", "", ""));
        JButton editAddressBtn = new JButton("Edit Address");
        editAddressBtn.addActionListener(e -> {
            int row = alamatTable.getSelectedRow();
            if (row == -1) {
                GUIHelper.showError(this, "Select an address to edit!");
                return;
            }
            int id = (int) alamatModel.getValueAt(row, 0);
            String rec = (String) alamatModel.getValueAt(row, 1);
            String str = (String) alamatModel.getValueAt(row, 2);
            String cit = (String) alamatModel.getValueAt(row, 3);
            String pro = (String) alamatModel.getValueAt(row, 4);
            String phn = (String) alamatModel.getValueAt(row, 5);
            showAddressDialog(id, rec, str, cit, pro, phn);
        });
        addressBtnPanel.add(addAddressBtn, BorderLayout.WEST);
        addressBtnPanel.add(editAddressBtn, BorderLayout.EAST);
        aPanel.add(addressBtnPanel, BorderLayout.SOUTH);

        // Topups
        JPanel tPanel = new JPanel(new BorderLayout());
        tPanel.setBorder(BorderFactory.createTitledBorder("Top-up History"));
        topupModel = new DefaultTableModel(new String[]{"Date", "Nominal", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        topupTable = new JTable(topupModel);
        tPanel.add(new JScrollPane(topupTable), BorderLayout.CENTER);
        
        JButton addTopupBtn = new JButton("Topup");
        addTopupBtn.addActionListener(e -> showAddTopupDialog());
        tPanel.add(addTopupBtn, BorderLayout.SOUTH);

        centerPanel.add(aPanel);
        centerPanel.add(tPanel);
        add(centerPanel, BorderLayout.CENTER);
        
        refreshAlamat();
        refreshTopup();
    }

    private void refreshAlamat() {
        alamatModel.setRowCount(0);
        List<AlamatPelanggan> list = userModel.getAlamatByCustomer(UserSession.getCurrentUser().getIdPengguna());
        for (AlamatPelanggan a : list) alamatModel.addRow(new Object[]{a.getIdAlamat(), a.getNamaPenerima(), a.getJalan(), a.getKota(), a.getProvinsi(), a.getNoTelp()});
    }

    private void refreshTopup() {
        topupModel.setRowCount(0);
        List<RiwayatTopup> list = userModel.getTopupHistory(UserSession.getCurrentUser().getIdPengguna());
        for (RiwayatTopup r : list) topupModel.addRow(new Object[]{r.getTanggalTopup(), r.getNominal(), r.getStatus()});
    }

    private void showEditProfileDialog() {
        Pengguna u = UserSession.getCurrentUser();
        JTextField fnameF = new JTextField(u.getNamaDepan());
        JTextField lnameF = new JTextField(u.getNamaBelakang());
        JTextField phoneF = new JTextField(u.getNomorTelepon());
        
        Object[] message = {"First Name:", fnameF, "Last Name:", lnameF, "Phone:", phoneF};
        int option = JOptionPane.showConfirmDialog(this, message, "Update Profile", JOptionPane.OK_CANCEL_OPTION);
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
            u.setNamaDepan(fnameF.getText());
            u.setNamaBelakang(lnameF.getText());
            u.setNomorTelepon(phoneF.getText());
            if (userModel.updateProfile(u)) {
                GUIHelper.showInfo(this, "Profile updated!");
                nameL.setText("Name: " + u.getFullName());
                phoneL.setText("Phone: " + u.getNomorTelepon());
            }
        }
    }

    private void showChangePasswordDialog() {
        JPasswordField oldP = new JPasswordField();
        JPasswordField newP = new JPasswordField();
        JPasswordField confP = new JPasswordField();
        
        Object[] message = {"Old Password:", oldP, "New Password:", newP, "Confirm New Password:", confP};
        int option = JOptionPane.showConfirmDialog(this, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String op = new String(oldP.getPassword());
            String np = new String(newP.getPassword());
            String cp = new String(confP.getPassword());
            
            if (!np.equals(cp)) { GUIHelper.showError(this, "Confirm password does not match!"); return; }
            
            if (userModel.updatePassword(UserSession.getCurrentUser().getIdPengguna(), op, np)) {
                GUIHelper.showInfo(this, "Password updated!");
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
                GUIHelper.showError(this, "Failed to update password. Check your old password.");
            }
        }
    }

    private void showAddTopupDialog() {
        String nominalS = JOptionPane.showInputDialog(this, "Enter Topup Nominal:");
        if (nominalS != null) {
            try {
                java.math.BigDecimal nominal = new java.math.BigDecimal(nominalS);
                if (userModel.addTopup(UserSession.getCurrentUser().getIdPengguna(), nominal)) {
                    GUIHelper.showInfo(this, "Topup successful!");
                    refreshTopup();
                }
            } catch (Exception ex) { GUIHelper.showError(this, "Invalid nominal."); }
        }
    }

    private void showAddressDialog(Integer idAlamat, String rec, String str, String cit, String pro, String phn) {
        JTextField recipientF = new JTextField(rec);
        JTextField streetF = new JTextField(str);
        JTextField cityF = new JTextField(cit);
        JTextField provF = new JTextField(pro);
        JTextField phoneF = new JTextField(phn);

        Object[] message = {"Recipient Name:", recipientF, "Street:", streetF, "City:", cityF, "Province:", provF, "Phone:", phoneF};
        int option = JOptionPane.showConfirmDialog(this, message, (idAlamat == null ? "Add" : "Edit") + " Address", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            boolean success;
            if (idAlamat == null) {
                success = userModel.addAlamat(UserSession.getCurrentUser().getIdPengguna(), provF.getText(), cityF.getText(), streetF.getText(), recipientF.getText(), phoneF.getText());
            } else {
                success = userModel.updateAlamat(idAlamat, provF.getText(), cityF.getText(), streetF.getText(), recipientF.getText(), phoneF.getText());
            }
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
                GUIHelper.showInfo(this, "Address saved successfully!");
                refreshAlamat();
            } else {
                GUIHelper.showError(this, "Failed to save address.");
            }
        }
    }
}
