package ui;

import entities.Pelanggan;
import entities.Pengguna;
import helpers.GUIHelper;
import models.TransactionModel;
import models.UserModel;
import session.UserSession;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPelanggan extends JPanel {
    private TransactionModel transactionModel;
    private UserModel userModel;
    
    private JTextField txtNamaDepan;
    private JTextField txtNamaBelakang;
    private JTextField txtEmail; 
    private JPasswordField txtPassword;
    private JTextField txtJenisMember;     
    private JTextField txtPoin;    
    private JTextField txtTanggalGabung;   
    
    private JButton btnAction; 
    private JButton btnBatal;  
    private JButton btnHapusAkun; // Tombol baru untuk hapus akun
    private boolean isEditMode = false; 

    private JTable topProductsTable;
    private DefaultTableModel tableModel;

    public DashboardPelanggan() {
        transactionModel = new TransactionModel();
        userModel = new UserModel();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        setupIdentityPanel();
        add(Box.createVerticalStrut(20)); 
        setupTopProductsPanel();

        loadUserData();
        loadTopProducts();
        setFieldsEditable(false); 
    }

    private void setupIdentityPanel() {
        JPanel identityPanel = new JPanel(new GridBagLayout());
        identityPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Identity & Membership Information", 
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

        // 5. Jenis Member (Read-Only)
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0;
        identityPanel.add(new JLabel("Membership Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtJenisMember = new JTextField(20);
        txtJenisMember.setEditable(false);
        txtJenisMember.setBackground(new Color(240, 240, 240)); 
        identityPanel.add(txtJenisMember, gbc);

        // 6. Poin (Read-Only)
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.0;
        identityPanel.add(new JLabel("Points:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPoin = new JTextField(20);
        txtPoin.setEditable(false);
        txtPoin.setBackground(new Color(240, 240, 240));
        identityPanel.add(txtPoin, gbc);

        // 7. Tanggal Bergabung (Read-Only)
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.0;
        identityPanel.add(new JLabel("Join Date:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtTanggalGabung = new JTextField(20);
        txtTanggalGabung.setEditable(false);
        txtTanggalGabung.setBackground(new Color(240, 240, 240));
        identityPanel.add(txtTanggalGabung, gbc);

        // Panel Khusus Tombol (Diposisikan di gridy = 7 agar TIDAK MENUMPUK)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        
        btnHapusAkun = new JButton("Delete Account");
        btnHapusAkun.setBackground(new Color(220, 53, 69)); // Warna merah danger
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

        gbc.gridx = 1; gbc.gridy = 7; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        identityPanel.add(buttonPanel, gbc);

        add(identityPanel);
    }

    private void setupTopProductsPanel() {
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "5 Top Purchased Items", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        tableModel = new DefaultTableModel(new String[]{"Product Name", "Category", "Total Quantity", "Unit Price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        topProductsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(topProductsTable);
        scrollPane.setPreferredSize(new Dimension(500, 150)); 
        
        productsPanel.add(scrollPane, BorderLayout.CENTER);
        add(productsPanel);
    }

    private void loadUserData() {
        Pengguna currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            Pelanggan pelanggan = userModel.getPelangganById(currentUser.getIdPengguna());

            txtNamaDepan.setText(currentUser.getNamaDepan() != null ? currentUser.getNamaDepan() : "");
            txtNamaBelakang.setText(currentUser.getNamaBelakang() != null ? currentUser.getNamaBelakang() : "");
            txtEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
            txtPassword.setText(currentUser.getPassword() != null ? currentUser.getPassword() : "");

            // Mengisi data spesifik pelanggan dan memunculkan angka poin
            txtJenisMember.setText(pelanggan != null && pelanggan.getJenisMember() != null ? pelanggan.getJenisMember() : "Reguler");
            txtPoin.setText(pelanggan != null ? String.valueOf(pelanggan.getPoin()) : "0");
            txtTanggalGabung.setText(pelanggan != null && pelanggan.getTanggalBergabung() != null ? pelanggan.getTanggalBergabung().toString() : "-");
        }
    }

    private void loadTopProducts() {
        tableModel.setRowCount(0);
        Pengguna currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            List<Object[]> items = transactionModel.getTop5PurchasedItems(currentUser.getIdPengguna());
            for (Object[] row : items) {
                tableModel.addRow(row);
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
            btnHapusAkun.setVisible(false); // Sembunyikan hapus akun saat sedang mengedit
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

                // Silakan aktifkan panggillan database asli Anda jika method-nya sudah siap:
                // boolean success = userModel.updateProfile(currentUser);
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

        // Tampilkan dialog konfirmasi kepada user
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete your account permanently?\nThis action cannot be undone!", 
                "Confirm Delete Account", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Panggil query delete dari model database Anda
            // Tip: Di dalam userModel.deleteUser, pastikan data anak di tabel `pelanggan` ikut terhapus (Cascade)
            // boolean success = userModel.deleteUser(currentUser.getIdPengguna());
            boolean success = true; // Simulasi berhasil

            if (success) {
                GUIHelper.showInfo(this, "Your account has been successfully deleted.");
                UserSession.logout(); // Bersihkan data session login
                
                // Menutup window/frame saat ini dan kembali ke form login
                Window ancestor = SwingUtilities.getWindowAncestor(this);
                if (ancestor != null) {
                    ancestor.dispose();
                }
                
                // Buka kembali halaman Login (Sesuaikan nama class Frame Login milikmu)
                // new LoginFrame().setVisible(true);
                System.out.println("DEBUG: Your Account has been deleted, redirected to login screen.");
            } else {
                GUIHelper.showError(this, "Failed to delete account from database.");
            }
        }
    }
}