package ui;

import entities.Member;
import entities.Pelanggan;
import entities.Pengguna;
import models.MemberModel;
import models.PelangganModel;
import models.PenggunaModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class PelangganPanel extends JPanel {
    private PelangganModel model = new PelangganModel();
    private PenggunaModel penggunaModel = new PenggunaModel();
    private MemberModel memberModel = new MemberModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtPoin, txtTanggal;
    private JComboBox<Pengguna> cbPengguna;
    private JComboBox<Member> cbMember;
    private JCheckBox chkVoucherPercent, chkVoucherPrice;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public PelangganPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Pengguna", "Nama", "Member", "Tgl Bergabung", "Poin", "Voucher % Used", "Voucher Price Used"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        formPanel.add(new JLabel("Pengguna:"));
        cbPengguna = new JComboBox<>();
        formPanel.add(cbPengguna);
        formPanel.add(new JLabel("Member:"));
        cbMember = new JComboBox<>();
        formPanel.add(cbMember);
        formPanel.add(new JLabel("Tgl Bergabung (YYYY-MM-DD):"));
        txtTanggal = new JTextField();
        formPanel.add(txtTanggal);
        formPanel.add(new JLabel("Poin:"));
        txtPoin = new JTextField();
        formPanel.add(txtPoin);
        formPanel.add(new JLabel("Voucher % Used:"));
        chkVoucherPercent = new JCheckBox();
        formPanel.add(chkVoucherPercent);
        formPanel.add(new JLabel("Voucher Price Used:"));
        chkVoucherPrice = new JCheckBox();
        formPanel.add(chkVoucherPrice);

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
            refreshMember();
        });
        btnAdd.addActionListener(e -> addPelanggan());
        btnUpdate.addActionListener(e -> updatePelanggan());
        btnDelete.addActionListener(e -> deletePelanggan());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtTanggal.setText(tableModel.getValueAt(row, 3).toString());
                txtPoin.setText(tableModel.getValueAt(row, 4).toString());
                chkVoucherPercent.setSelected((boolean) tableModel.getValueAt(row, 5));
                chkVoucherPrice.setSelected((boolean) tableModel.getValueAt(row, 6));
                
                // Select combo boxes
                String memJenis = (String) tableModel.getValueAt(row, 2);
                for (int i = 0; i < cbMember.getItemCount(); i++) {
                    if (cbMember.getItemAt(i).getJenis().equals(memJenis)) {
                        cbMember.setSelectedIndex(i);
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
        refreshMember();
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

    private void refreshMember() {
        try {
            cbMember.removeAllItems();
            List<Member> list = memberModel.getAll();
            for (Member m : list) {
                cbMember.addItem(m);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Member: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Pelanggan> list = model.getAll();
            for (Pelanggan pl : list) {
                tableModel.addRow(new Object[]{pl.getIdPengguna(), pl.getNamaLengkap(), pl.getJenisMember(), pl.getTanggalBergabung(), pl.getPoin(), pl.isUsedVoucherPercentage(), pl.isUsedVoucherPrice()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addPelanggan() {
        try {
            Pengguna p = (Pengguna) cbPengguna.getSelectedItem();
            Member m = (Member) cbMember.getSelectedItem();
            if (p == null || m == null) return;
            Pelanggan pl = new Pelanggan(p.getIdPengguna(), m.getJenis(), Date.valueOf(txtTanggal.getText()), Integer.parseInt(txtPoin.getText()), chkVoucherPercent.isSelected(), chkVoucherPrice.isSelected());
            model.insert(pl);
            refreshTable();
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updatePelanggan() {
        if (selectedId == -1) return;
        try {
            Member m = (Member) cbMember.getSelectedItem();
            if (m == null) return;
            Pelanggan pl = new Pelanggan(selectedId, m.getJenis(), Date.valueOf(txtTanggal.getText()), Integer.parseInt(txtPoin.getText()), chkVoucherPercent.isSelected(), chkVoucherPrice.isSelected());
            model.update(pl);
            refreshTable();
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deletePelanggan() {
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
