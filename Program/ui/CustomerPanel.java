package ui;

import entities.*;
import helpers.GUIHelper;
import models.UserModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private UserModel userModel;
    
    private JTable customerTable;
    private DefaultTableModel customerModel;
    
    private JTable memberTable;
    private DefaultTableModel memberModel;

    public CustomerPanel() {
        userModel = new UserModel();
        setLayout(new GridLayout(2, 1, 0, 15)); 
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setupCustomerSection();
        setupMemberSection();
        
        refreshCustomers();
        refreshMembers();
    }

    private void setupCustomerSection() {
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Management"));

        customerModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Membership"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        customerTable = new JTable(customerModel);
        customerTable.getTableHeader().setReorderingAllowed(false);
        customerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton addBtn = new JButton("Add Customer");
        JButton editBtn = new JButton("Edit Customer");
        JButton deleteBtn = new JButton("Delete Customer");

        addBtn.addActionListener(e -> showAddCustomerDialog());
        editBtn.addActionListener(e -> handleEditCustomer());
        deleteBtn.addActionListener(e -> handleDeleteCustomer());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        
        customerPanel.add(btnPanel, BorderLayout.SOUTH);
        add(customerPanel);
    }

    private void setupMemberSection() {
        JPanel memberPanel = new JPanel(new BorderLayout());
        memberPanel.setBorder(BorderFactory.createTitledBorder("Membership Management"));

        memberModel = new DefaultTableModel(new String[]{"Jenis", "Min Poin", "Voucher Price", "Voucher %"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        memberTable = new JTable(memberModel);
        memberTable.getTableHeader().setReorderingAllowed(false);
        memberPanel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton addBtn = new JButton("Add Member");
        JButton editBtn = new JButton("Edit Member");
        JButton deleteBtn = new JButton("Delete Member");

        addBtn.addActionListener(e -> handleAddMember());
        editBtn.addActionListener(e -> handleEditMember());
        deleteBtn.addActionListener(e -> handleDeleteMember());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        
        memberPanel.add(btnPanel, BorderLayout.SOUTH);
        add(memberPanel);
    }

    private void refreshCustomers() {
        customerModel.setRowCount(0);

        List<Object[]> list = userModel.getAllCustomers(); 
        if (list != null) {
            for (Object[] row : list) {
                Pengguna p = (Pengguna) row[0];      
                String jenisMember = (String) row[1]; 
                
                String fullName = p.getNamaDepan() + " " + p.getNamaBelakang();
                customerModel.addRow(new Object[]{
                    p.getIdPengguna(), 
                    fullName, 
                    p.getEmail(), 
                    p.getNomorTelepon(),
                    jenisMember 
                });
            }
        }
    }

    private void refreshMembers() {
        memberModel.setRowCount(0);
        List<Member> list = userModel.getAllMembers();
        for (Member m : list) {
            memberModel.addRow(new Object[]{
                m.getJenis(), 
                m.getPoin(), 
                m.getVoucherPrice(), 
                m.getVoucherPercentage()
            });
        }
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Customer", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField emailF = new JTextField();
        JTextField fnameF = new JTextField();
        JTextField lnameF = new JTextField();
        JTextField passF = new JTextField();
        JTextField phoneF = new JTextField();
        
        JComboBox<String> memberC = new JComboBox<>();
        List<Member> members = userModel.getAllMembers();
        for (Member m : members) memberC.addItem(m.getJenis());

        dialog.add(new JLabel("Email:")); dialog.add(emailF);
        dialog.add(new JLabel("First Name:")); dialog.add(fnameF);
        dialog.add(new JLabel("Last Name:")); dialog.add(lnameF);
        dialog.add(new JLabel("Password:")); dialog.add(passF);
        dialog.add(new JLabel("Phone:")); dialog.add(phoneF);
        dialog.add(new JLabel("Initial Membership:")); dialog.add(memberC);

        JButton saveBtn = new JButton("Save Customer");
        saveBtn.addActionListener(e -> {
            Pengguna p = new Pengguna();
            p.setEmail(emailF.getText());
            p.setNamaDepan(fnameF.getText());
            p.setNamaBelakang(lnameF.getText());
            p.setPassword(passF.getText());
            p.setNomorTelepon(phoneF.getText());
            
            String selectedMember = (String) memberC.getSelectedItem();
            if (userModel.addCustomer(p, selectedMember)) {
                GUIHelper.showInfo(dialog, "Customer created successfully!");
                dialog.dispose();
                refreshCustomers(); 
            }
        });
        dialog.add(saveBtn);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleEditCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih customer dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCustomer = (int) customerModel.getValueAt(row, 0);
        String currentFullName = (String) customerModel.getValueAt(row, 1);
        String currentEmail = (String) customerModel.getValueAt(row, 2);
        String currentPhone = (String) customerModel.getValueAt(row, 3);
        String currentMember = (String) customerModel.getValueAt(row, 4);

        String[] nameParts = currentFullName.split(" ", 2);
        String fname = nameParts[0];
        String lname = (nameParts.length > 1) ? nameParts[1] : "";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Customer", true);
        dialog.setSize(400, 450);
        dialog.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField emailF = new JTextField(currentEmail);
        JTextField fnameF = new JTextField(fname);
        JTextField lnameF = new JTextField(lname);
        JTextField phoneF = new JTextField(currentPhone);
        
        JComboBox<String> memberC = new JComboBox<>();
        List<Member> members = userModel.getAllMembers();
        for (Member m : members) {
            memberC.addItem(m.getJenis());
        }
        memberC.setSelectedItem(currentMember);

        dialog.add(new JLabel("Email:")); dialog.add(emailF);
        dialog.add(new JLabel("First Name:")); dialog.add(fnameF);
        dialog.add(new JLabel("Last Name:")); dialog.add(lnameF);
        dialog.add(new JLabel("Phone:")); dialog.add(phoneF);
        dialog.add(new JLabel("Membership Tier:")); dialog.add(memberC);

        JButton updateBtn = new JButton("Save Changes");
        updateBtn.addActionListener(e -> {
            Pengguna p = new Pengguna();
            p.setIdPengguna(idCustomer);
            p.setEmail(emailF.getText());
            p.setNamaDepan(fnameF.getText());
            p.setNamaBelakang(lnameF.getText());
            p.setNomorTelepon(phoneF.getText());
            p.setPassword(""); 

            String selectedMember = (String) memberC.getSelectedItem();

            if (userModel.updateCustomer(p, selectedMember)) {
                GUIHelper.showInfo(dialog, "Data customer berhasil diperbarui!");
                dialog.dispose();
                refreshCustomers(); 
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal memperbarui data customer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(updateBtn);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleDeleteCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih customer yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCustomer = (int) customerModel.getValueAt(row, 0);
        String name = (String) customerModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah kamu yakin ingin menghapus customer " + name + "?\nSemua data terkait akun ini akan ikut terhapus.", 
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userModel.deleteCustomer(idCustomer)) {
                GUIHelper.showInfo(this, "Customer berhasil dihapus!");
                refreshCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data dari database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAddMember() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Membership Tier", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField jenisF = new JTextField();
        JTextField poinF = new JTextField();
        JTextField priceF = new JTextField();
        JTextField percentF = new JTextField();

        dialog.add(new JLabel("Jenis Member (Nama Tier):")); dialog.add(jenisF);
        dialog.add(new JLabel("Minimal Poin:")); dialog.add(poinF);
        dialog.add(new JLabel("Voucher Price (Rp):")); dialog.add(priceF);
        dialog.add(new JLabel("Voucher Percentage (%):")); dialog.add(percentF);

        JButton saveBtn = new JButton("Save Tier");
        saveBtn.addActionListener(e -> {
            try {
                Member m = new Member();
                m.setJenis(jenisF.getText().trim());
                m.setPoin(Integer.parseInt(poinF.getText().trim()));
                m.setVoucherPrice(new java.math.BigDecimal(priceF.getText().trim()));
                m.setVoucherPercentage(new java.math.BigDecimal(percentF.getText().trim()));

                if (userModel.addMember(m)) {
                    GUIHelper.showInfo(dialog, "Membership tier baru berhasil dibuat!");
                    dialog.dispose();
                    refreshMembers(); 
                } else {
                    JOptionPane.showMessageDialog(dialog, "Gagal membuat tier baru.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Input nilai harus berupa angka valid!", "Input Salah", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(saveBtn);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleEditMember() {
        int row = memberTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih membership tier dari tabel bawah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentJenis = (String) memberModel.getValueAt(row, 0);
        int currentPoin = (int) memberModel.getValueAt(row, 1);
        java.math.BigDecimal currentPrice = (java.math.BigDecimal) memberModel.getValueAt(row, 2);
        java.math.BigDecimal currentPercent = (java.math.BigDecimal) memberModel.getValueAt(row, 3);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Membership Tier", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField jenisF = new JTextField(currentJenis);
        jenisF.setEditable(false); 
        JTextField poinF = new JTextField(String.valueOf(currentPoin));
        JTextField priceF = new JTextField(currentPrice.toString());
        JTextField percentF = new JTextField(currentPercent.toString());

        dialog.add(new JLabel("Jenis Member (Tidak dapat diubah):")); dialog.add(jenisF);
        dialog.add(new JLabel("Minimal Poin:")); dialog.add(poinF);
        dialog.add(new JLabel("Voucher Price (Rp):")); dialog.add(priceF);
        dialog.add(new JLabel("Voucher Percentage (%):")); dialog.add(percentF);

        JButton updateBtn = new JButton("Save Changes");
        updateBtn.addActionListener(e -> {
            try {
                Member m = new Member();
                m.setJenis(currentJenis);
                m.setPoin(Integer.parseInt(poinF.getText().trim()));
                m.setVoucherPrice(new java.math.BigDecimal(priceF.getText().trim()));
                m.setVoucherPercentage(new java.math.BigDecimal(percentF.getText().trim()));

                if (userModel.updateMember(m)) {
                    GUIHelper.showInfo(dialog, "Membership tier berhasil diperbarui!");
                    dialog.dispose();
                    refreshMembers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Gagal memperbarui data tier.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Input nilai angka tidak valid!", "Input Salah", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(updateBtn);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleDeleteMember() {
        int row = memberTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih tier member yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String jenis = (String) memberModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah kamu yakin ingin menghapus tier '" + jenis + "'?", 
                "Konfirmasi Hapus Tier", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userModel.deleteMember(jenis)) {
                GUIHelper.showInfo(this, "Membership tier berhasil dihapus!");
                refreshMembers();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus tier. Masih ada customer yang menggunakan tier ini!", "Database Constraint Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}