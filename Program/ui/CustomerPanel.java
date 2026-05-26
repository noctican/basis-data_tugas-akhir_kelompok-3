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
    private JTable memberTable;
    private DefaultTableModel memberModel;

    public CustomerPanel() {
        userModel = new UserModel();
        setLayout(new BorderLayout());

        setupMemberTable();
        setupBottomPanel();
        
        refreshMembers();
    }

    private void setupMemberTable() {
        memberModel = new DefaultTableModel(new String[]{"Jenis", "Min Poin", "Voucher Price", "Voucher %"}, 0);
        memberTable = new JTable(memberModel);
        add(new JScrollPane(memberTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton addCustomerBtn = new JButton("Add New Customer");
        addCustomerBtn.addActionListener(e -> showAddCustomerDialog());

        bottom.add(addCustomerBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private void refreshMembers() {
        memberModel.setRowCount(0);
        List<Member> list = userModel.getAllMembers();
        for (Member m : list) memberModel.addRow(new Object[]{m.getJenis(), m.getPoin(), m.getVoucherPrice(), m.getVoucherPercentage()});
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
        
        JComboBox<Member> memberC = new JComboBox<>();
        List<Member> members = userModel.getAllMembers();
        for (Member m : members) memberC.addItem(m);

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
            
            Member m = (Member) memberC.getSelectedItem();
            if (userModel.addCustomer(p, m.getJenis())) {
                GUIHelper.showInfo(dialog, "Customer created (Pengguna + Pelanggan Transactional)!");
                dialog.dispose();
            }
        });
        dialog.add(saveBtn);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
