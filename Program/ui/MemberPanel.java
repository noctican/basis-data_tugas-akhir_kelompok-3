package ui;

import entities.Member;
import models.MemberModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class MemberPanel extends JPanel {
    private MemberModel model = new MemberModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtJenis, txtPoin, txtPrice, txtPercentage;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private String selectedJenis = null;

    public MemberPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Jenis", "Poin", "Voucher Price", "Voucher %"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("Jenis:"));
        txtJenis = new JTextField();
        formPanel.add(txtJenis);
        formPanel.add(new JLabel("Poin:"));
        txtPoin = new JTextField();
        formPanel.add(txtPoin);
        formPanel.add(new JLabel("Voucher Price:"));
        txtPrice = new JTextField();
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Voucher %:"));
        txtPercentage = new JTextField();
        formPanel.add(txtPercentage);

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
        btnAdd.addActionListener(e -> addMember());
        btnUpdate.addActionListener(e -> updateMember());
        btnDelete.addActionListener(e -> deleteMember());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedJenis = (String) tableModel.getValueAt(row, 0);
                txtJenis.setText(selectedJenis);
                txtPoin.setText(tableModel.getValueAt(row, 1).toString());
                txtPrice.setText(tableModel.getValueAt(row, 2).toString());
                txtPercentage.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Member> list = model.getAll();
            for (Member m : list) {
                tableModel.addRow(new Object[]{m.getJenis(), m.getPoin(), m.getVoucherPrice(), m.getVoucherPercentage()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addMember() {
        try {
            Member m = new Member(txtJenis.getText(), Integer.parseInt(txtPoin.getText()), new BigDecimal(txtPrice.getText()), new BigDecimal(txtPercentage.getText()));
            model.insert(m);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateMember() {
        if (selectedJenis == null) return;
        try {
            Member m = new Member(selectedJenis, Integer.parseInt(txtPoin.getText()), new BigDecimal(txtPrice.getText()), new BigDecimal(txtPercentage.getText()));
            model.update(m);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteMember() {
        if (selectedJenis == null) return;
        try {
            model.delete(selectedJenis);
            refreshTable();
            selectedJenis = null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Delete: " + e.getMessage());
        }
    }
}
