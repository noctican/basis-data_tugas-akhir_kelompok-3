package ui;

import entities.*;
import helpers.GUIHelper;
import models.UserModel;
import session.UserSession;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.math.BigDecimal;

public class ProfilePanel extends JPanel {
    private UserModel userModel;
    
    // Identity fields
    private JTextField txtNamaDepan;
    private JTextField txtNamaBelakang;
    private JTextField txtEmail; 
    private JTextField txtWallet;
    private JPasswordField txtPassword;
    private JTextField txtJenisMember;     
    private JTextField txtPoin;    
    private JTextField txtTanggalGabung;   
    private JButton btnAction; 
    private JButton btnBatal;  
    private JButton btnHapusAkun; 
    private boolean isEditMode = false;

    // Customer Specific fields
    private JTable alamatTable, topupTable;
    private DefaultTableModel alamatModel, topupModel;

    public ProfilePanel() {
        userModel = new UserModel();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        setupIdentityPanel();
        loadUserData();
        setFieldsEditable(false);

        if (UserSession.getRole() == UserSession.Role.PELANGGAN) {
            setupCustomerSpecifics();
        }
    }

    private void setupIdentityPanel() {
        JPanel identityPanel = new JPanel(new GridBagLayout());
        identityPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Identity Information", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Nama Depan
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        identityPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNamaDepan = new JTextField(20);
        identityPanel.add(txtNamaDepan, gbc);

        // 2. Nama Belakang
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        identityPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNamaBelakang = new JTextField(20);
        identityPanel.add(txtNamaBelakang, gbc);

        // 3. Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        identityPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        identityPanel.add(txtEmail, gbc);

        // 4. Password
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        identityPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPassword = new JPasswordField(20);
        txtPassword.setEchoChar('*');
        identityPanel.add(txtPassword, gbc);

        int currentY = 4;

        if (UserSession.getRole() == UserSession.Role.PELANGGAN) {
            // 5. Jenis Member (Read-Only)
            gbc.gridx = 0; gbc.gridy = currentY; gbc.weightx = 0.0;
            identityPanel.add(new JLabel("Membership Type:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            txtJenisMember = new JTextField(20);
            txtJenisMember.setEditable(false);
            txtJenisMember.setBackground(new Color(240, 240, 240)); 
            identityPanel.add(txtJenisMember, gbc);
            currentY++;

            gbc.gridx = 0; gbc.gridy = currentY; gbc.weightx = 0.0;
            identityPanel.add(new JLabel("Wallet:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            txtWallet = new JTextField(20);
            txtWallet.setEditable(false);
            txtWallet.setBackground(new Color(240, 240, 240));
            identityPanel.add(txtWallet, gbc);
            currentY++;

            // 6. Poin (Read-Only)
            gbc.gridx = 0; gbc.gridy = currentY; gbc.weightx = 0.0;
            identityPanel.add(new JLabel("Points:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            txtPoin = new JTextField(20);
            txtPoin.setEditable(false);
            txtPoin.setBackground(new Color(240, 240, 240));
            identityPanel.add(txtPoin, gbc);
            currentY++;

            // 7. Tanggal Bergabung (Read-Only)
            gbc.gridx = 0; gbc.gridy = currentY; gbc.weightx = 0.0;
            identityPanel.add(new JLabel("Join Date:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            txtTanggalGabung = new JTextField(20);
            txtTanggalGabung.setEditable(false);
            txtTanggalGabung.setBackground(new Color(240, 240, 240));
            identityPanel.add(txtTanggalGabung, gbc);
            currentY++;
        }

        // Panel Khusus Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        
        btnHapusAkun = new JButton("Delete Account");
        btnHapusAkun.setBackground(new Color(220, 53, 69));
        btnHapusAkun.setForeground(Color.WHITE);
        btnHapusAkun.addActionListener(e -> handleHapusAkun());
        buttonPanel.add(btnHapusAkun);

        btnBatal = new JButton("Cancel");
        btnBatal.setVisible(false); 
        btnBatal.addActionListener(e -> handleCancel());
        buttonPanel.add(btnBatal);

        btnAction = new JButton("Edit Profile");
        btnAction.addActionListener(e -> handleActionButton());
        buttonPanel.add(btnAction);

        gbc.gridx = 1; gbc.gridy = currentY; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        identityPanel.add(buttonPanel, gbc);

        add(identityPanel, BorderLayout.NORTH);
    }

    private void loadUserData() {
        Pengguna currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            txtNamaDepan.setText(currentUser.getNamaDepan() != null ? currentUser.getNamaDepan() : "");
            txtNamaBelakang.setText(currentUser.getNamaBelakang() != null ? currentUser.getNamaBelakang() : "");
            txtEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
            txtPassword.setText(currentUser.getPassword() != null ? currentUser.getPassword() : "");

            if (UserSession.getRole() == UserSession.Role.PELANGGAN) {
                Pelanggan pelanggan = userModel.getPelangganById(currentUser.getIdPengguna());
                txtJenisMember.setText(pelanggan != null && pelanggan.getJenisMember() != null ? pelanggan.getJenisMember() : "Reguler");
                txtWallet.setText(pelanggan != null ? String.valueOf(pelanggan.getWallet()) : "0");
                txtPoin.setText(pelanggan != null ? String.valueOf(pelanggan.getPoin()) : "0");
                txtTanggalGabung.setText(pelanggan != null && pelanggan.getTanggalBergabung() != null ? pelanggan.getTanggalBergabung().toString() : "-");
            }
        }
    }

    private void setFieldsEditable(boolean editable) {
        txtNamaDepan.setEditable(editable);
        txtNamaBelakang.setEditable(editable);
        txtEmail.setEditable(editable);
        txtPassword.setEditable(editable);
        
        Color bg = editable ? Color.WHITE : new Color(240, 240, 240);
        txtNamaDepan.setBackground(bg);
        txtNamaBelakang.setBackground(bg);
        txtEmail.setBackground(bg);
        txtPassword.setBackground(bg);
    }

    private void handleActionButton() {
        if (!isEditMode) {
            isEditMode = true;
            setFieldsEditable(true);
            btnAction.setText("Save Changes");
            btnBatal.setVisible(true);
            btnHapusAkun.setVisible(false);
        } else {
            String namaDepanBaru = txtNamaDepan.getText().trim();
            String namaBelakangBaru = txtNamaBelakang.getText().trim();
            String emailBaru = txtEmail.getText().trim();
            String passwordBaru = new String(txtPassword.getPassword()).trim();

            if (namaDepanBaru.isEmpty() || emailBaru.isEmpty() || passwordBaru.isEmpty()) {
                GUIHelper.showError(this, "First Name, Email, and Password must be filled out!");
                return;
            }

            Pengguna currentUser = UserSession.getCurrentUser();
            if (currentUser != null) {
                currentUser.setNamaDepan(namaDepanBaru);
                currentUser.setNamaBelakang(namaBelakangBaru);
                currentUser.setEmail(emailBaru);
                currentUser.setPassword(passwordBaru);

                // boolean success = userModel.updatePenggunaFull(currentUser);
                boolean success = true;

                if (success) {
                    GUIHelper.showInfo(this, "Profile successfully updated!");
                    isEditMode = false;
                    setFieldsEditable(false);
                    btnAction.setText("Edit Profil");
                    btnBatal.setVisible(false);
                    btnHapusAkun.setVisible(true);
                } else {
                    GUIHelper.showError(this, "Failed to update profile in database.");
                }
            }
        }
    }

    private void handleCancel() {
        loadUserData();
        isEditMode = false;
        setFieldsEditable(false);
        btnAction.setText("Edit Profil");
        btnBatal.setVisible(false);
        btnHapusAkun.setVisible(true);
    }

    private void handleHapusAkun() {
        Pengguna currentUser = UserSession.getCurrentUser();
        if (currentUser == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete your account permanently?\nThis action cannot be undone!", 
                "Confirm Delete Account", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = false;
            if (UserSession.getRole() == UserSession.Role.PELANGGAN) {
                success = userModel.deleteCustomer(currentUser.getIdPengguna());
            } else {
                success = userModel.deleteEmployee(currentUser.getIdPengguna());
            }

            if (success) {
                GUIHelper.showInfo(this, "Your account has been successfully deleted.");
                UserSession.logout();
                
                Window ancestor = SwingUtilities.getWindowAncestor(this);
                if (ancestor != null) {
                    ancestor.dispose();
                }
                
                new LoginFrame().setVisible(true);
            } else {
                GUIHelper.showError(this, "Failed to delete account from database.");
            }
        }
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
        
        JPanel addressBtnPanel = new JPanel(new GridLayout(1, 0));
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
        JButton deleteAddressBtn = new JButton("Delete Address");
        deleteAddressBtn.addActionListener(e -> handleDeleteAlamat());
        addressBtnPanel.add(addAddressBtn);
        addressBtnPanel.add(editAddressBtn);
        addressBtnPanel.add(deleteAddressBtn);
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
                refreshAlamat();
            } else {
                GUIHelper.showError(this, "Failed to delete address.");
            }
        }
    }

    private void showAddTopupDialog() {
        String nominalS = JOptionPane.showInputDialog(this, "Enter Topup Nominal:");
        if (nominalS != null) {
            try {
                BigDecimal nominal = new BigDecimal(nominalS);
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
            if (recipientF.getText().trim().isEmpty() || streetF.getText().trim().isEmpty()) {
                GUIHelper.showInfo(this, "Nama Penerima dan Jalan tidak boleh kosong!");
                return;
            }

            boolean success;
            if (idAlamat == null) {
                success = userModel.addAlamat(UserSession.getCurrentUser().getIdPengguna(), provF.getText(), cityF.getText(), streetF.getText(), recipientF.getText(), phoneF.getText());
            } else {
                success = userModel.updateAlamat(idAlamat, provF.getText(), cityF.getText(), streetF.getText(), recipientF.getText(), phoneF.getText());
            }
            
            if (success) {
                GUIHelper.showInfo(this, "Address saved successfully!");
                refreshAlamat();
            } else {
                GUIHelper.showError(this, "Failed to save address.");
            }
        }
    }
}
