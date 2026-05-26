package ui;

import entities.*;
import helpers.GUIHelper;
import models.UserModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HRPanel extends JPanel {
    private UserModel userModel;
    private JTable deptTable;
    private DefaultTableModel deptModel;

    public HRPanel() {
        userModel = new UserModel();
        setLayout(new BorderLayout());

        setupDeptTable();
        setupBottomPanel();

        refreshDepts();
    }

    private void setupDeptTable() {
        deptModel = new DefaultTableModel(new String[]{"ID", "Name", "Location", "Task Desc"}, 0);
        deptTable = new JTable(deptModel);
        add(new JScrollPane(deptTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton addDeptBtn = new JButton("Add Department");
        addDeptBtn.addActionListener(e -> showAddDeptDialog());
        
        JButton addEmpBtn = new JButton("Add Employee");
        addEmpBtn.addActionListener(e -> showAddEmpDialog());

        bottom.add(addDeptBtn);
        bottom.add(addEmpBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private void refreshDepts() {
        deptModel.setRowCount(0);
        List<Departemen> list = userModel.getAllDepartemen();
        for (Departemen d : list) deptModel.addRow(new Object[]{d.getIdDepartemen(), d.getNamaDepartemen(), d.getLokasi(), d.getDeskripsiTugas()});
    }

    private void showAddDeptDialog() {
        JTextField nameF = new JTextField();
        JTextField locF = new JTextField();
        JTextArea taskA = new JTextArea(3, 20);
        
        Object[] message = {"Name:", nameF, "Location:", locF, "Task:", new JScrollPane(taskA)};
        int option = JOptionPane.showConfirmDialog(this, message, "Add Department", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Departemen d = new Departemen();
            d.setNamaDepartemen(nameF.getText());
            d.setLokasi(locF.getText());
            d.setDeskripsiTugas(taskA.getText());
            if (userModel.addDepartemen(d)) {
                GUIHelper.showInfo(this, "Department added!");
                refreshDepts();
            }
        }
    }

    private void showAddEmpDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Employee", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField emailF = new JTextField();
        JTextField fnameF = new JTextField();
        JTextField lnameF = new JTextField();
        JTextField passF = new JTextField();
        JTextField phoneF = new JTextField();
        JTextField jabatanF = new JTextField();
        
        JComboBox<Departemen> deptC = new JComboBox<>();
        List<Departemen> depts = userModel.getAllDepartemen();
        for (Departemen d : depts) deptC.addItem(d);

        dialog.add(new JLabel("Email:")); dialog.add(emailF);
        dialog.add(new JLabel("First Name:")); dialog.add(fnameF);
        dialog.add(new JLabel("Last Name:")); dialog.add(lnameF);
        dialog.add(new JLabel("Password:")); dialog.add(passF);
        dialog.add(new JLabel("Phone:")); dialog.add(phoneF);
        dialog.add(new JLabel("Jabatan:")); dialog.add(jabatanF);
        dialog.add(new JLabel("Department:")); dialog.add(deptC);

        JButton saveBtn = new JButton("Save Employee");
        saveBtn.addActionListener(e -> {
            Pengguna p = new Pengguna();
            p.setEmail(emailF.getText());
            p.setNamaDepan(fnameF.getText());
            p.setNamaBelakang(lnameF.getText());
            p.setPassword(passF.getText());
            p.setNomorTelepon(phoneF.getText());
            
            Departemen d = (Departemen) deptC.getSelectedItem();
            if (userModel.addEmployee(p, d.getIdDepartemen(), jabatanF.getText())) {
                GUIHelper.showInfo(dialog, "Employee created (Pengguna + Karyawan Transactional)!");
                dialog.dispose();
            }
        });
        dialog.add(saveBtn);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
