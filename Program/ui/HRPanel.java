package ui;

import entities.*;
import helpers.GUIHelper;
import models.UserModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HRPanel extends JPanel {
    private UserModel userModel;
    private int loggedInUserId;
    private Karyawan currentKaryawan;
    private Departemen currentDept;
    
    private JPanel topPanel;
    private JTable memberTable;
    private DefaultTableModel memberModel;
    
    private JTable deptTable;
    private DefaultTableModel deptModel;

    public HRPanel(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
        this.userModel = new UserModel();
        setLayout(new BorderLayout(10, 10));

        loadCurrentUserInfo();

        setupTopPanel();
        setupCenterPanel();
        setupBottomPanel();

        refreshDepartmentMembers();
        refreshOtherDepts();
    }

    private void loadCurrentUserInfo() {
        try {
            currentKaryawan = userModel.getKaryawanById(loggedInUserId);
            if (currentKaryawan != null) {
                currentDept = userModel.getDepartemenById(currentKaryawan.getIdDepartemen());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTopPanel() {
        topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Your Current Department Info", TitledBorder.LEFT, TitledBorder.TOP));

        if (currentDept != null) {
            String managerName = "None / No Manager Assigned";
            Integer managerId = currentDept.getIdManajer();
            
            if (managerId != null && managerId == loggedInUserId) {
                managerName = "You";
            } else if (managerId != null && managerId > 0) {
                Pengguna manager = userModel.getPenggunaById(managerId);
                if (manager != null) {
                    managerName = manager.getNamaDepan() + " " + (manager.getNamaBelakang() != null ? manager.getNamaBelakang() : "");
                }
            }

            topPanel.add(new JLabel("  Department Name : " + currentDept.getNamaDepartemen()));
            topPanel.add(new JLabel("  Location               : " + (currentDept.getLokasi() != null ? currentDept.getLokasi() : "-")));
            topPanel.add(new JLabel("  Manager               : " + managerName));
        } else {
            topPanel.add(new JLabel("  No Department assigned to your account."));
        }

        add(topPanel, BorderLayout.NORTH);
    }

    private void setupCenterPanel() {
        JPanel mainCenter = new JPanel(new GridLayout(2, 1, 10, 10));

        JPanel memberPanel = new JPanel(new BorderLayout());
        memberPanel.setBorder(BorderFactory.createTitledBorder("Your Department Members"));

        memberModel = new DefaultTableModel(new String[]{"ID Pengguna", "Name", "Email", "Phone", "Jabatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        memberTable = new JTable(memberModel);
        memberPanel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

        JPanel memberActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton addEmpBtn = new JButton("Add Member");
        JButton editEmpBtn = new JButton("Edit Member");
        JButton deleteEmpBtn = new JButton("Delete Member");

        addEmpBtn.addActionListener(e -> showEmpDialog(null, null));
        
        editEmpBtn.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row == -1) {
                GUIHelper.showError(this, "Silakan pilih anggota tim yang ingin diedit!");
                return;
            }
            int idPengguna = (int) memberModel.getValueAt(row, 0);
            
            Pengguna p = userModel.getPenggunaById(idPengguna);
            Karyawan k = userModel.getKaryawanById(idPengguna);
            
            if (p != null && k != null) {
                showEmpDialog(p, k);
            }
        });

        deleteEmpBtn.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row == -1) {
                GUIHelper.showError(this, "Silakan pilih anggota tim yang ingin dihapus!");
                return;
            }
            int idPengguna = (int) memberModel.getValueAt(row, 0);

            if (idPengguna == loggedInUserId) {
                GUIHelper.showError(this, "Anda tidak bisa menghapus akun Anda sendiri dari panel ini!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Apakah Anda yakin ingin menghapus karyawan ini?\n(Aksi ini menghapus data Karyawan beserta akun Pengguna)", 
                    "Confirm Delete Member", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (userModel.deleteEmployee(idPengguna)) {
                    GUIHelper.showInfo(this, "Karyawan berhasil dihapus!");
                    refreshDepartmentMembers();
                } else {
                    GUIHelper.showError(this, "Gagal menghapus karyawan. Pastikan data tidak terikat relasi transaksional lain.");
                }
            }
        });

        memberActionPanel.add(addEmpBtn);
        memberActionPanel.add(editEmpBtn);
        memberActionPanel.add(deleteEmpBtn);
        memberPanel.add(memberActionPanel, BorderLayout.SOUTH);

        mainCenter.add(memberPanel);

        JPanel otherDeptPanel = new JPanel(new BorderLayout());
        otherDeptPanel.setBorder(BorderFactory.createTitledBorder("Other Departments"));

        deptModel = new DefaultTableModel(new String[]{"ID", "Name", "Location", "Task Desc", "Manager ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        deptTable = new JTable(deptModel);
        deptTable.removeColumn(deptTable.getColumnModel().getColumn(4));
        
        otherDeptPanel.add(new JScrollPane(deptTable), BorderLayout.CENTER);
        mainCenter.add(otherDeptPanel);

        add(mainCenter, BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton addDeptBtn = new JButton("Add Department");
        addDeptBtn.addActionListener(e -> showDeptDialog(null));
        
        JButton editDeptBtn = new JButton("Edit Department");
        editDeptBtn.addActionListener(e -> {
            int row = deptTable.getSelectedRow();
            if (row == -1) {
                GUIHelper.showError(this, "Silakan pilih departemen dari tabel 'Other Departments' yang ingin diedit!");
                return;
            }
            
            int idDept = (int) deptModel.getValueAt(row, 0);
            String name = (String) deptModel.getValueAt(row, 1);
            String loc = (String) deptModel.getValueAt(row, 2);
            String task = (String) deptModel.getValueAt(row, 3);
            Object mgrObj = deptModel.getValueAt(row, 4);
            
            Integer idMgr = null;
            if (mgrObj instanceof Number) idMgr = ((Number) mgrObj).intValue();

            Departemen d = new Departemen();
            d.setIdDepartemen(idDept);
            d.setNamaDepartemen(name);
            d.setLokasi(loc);
            d.setDeskripsiTugas(task);
            if (idMgr != null) d.setIdManajer(idMgr);

            showDeptDialog(d);
        });

        bottom.add(addDeptBtn);
        bottom.add(editDeptBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private void refreshDepartmentMembers() {
        memberModel.setRowCount(0);
        if (currentDept == null) return;

        List<Karyawan> members = userModel.getKaryawanByDepartemen(currentDept.getIdDepartemen());
        if (members != null) {
            for (Karyawan k : members) {
                Pengguna p = userModel.getPenggunaById(k.getIdPengguna());
                if (p != null) {
                    String fullName = p.getNamaDepan() + " " + (p.getNamaBelakang() != null ? p.getNamaBelakang() : "");
                    if (k.getIdPengguna() == loggedInUserId) {
                        fullName += " (You)";
                    }
                    
                    memberModel.addRow(new Object[]{
                        k.getIdPengguna(),
                        fullName,
                        p.getEmail(),
                        p.getNomorTelepon() != null ? p.getNomorTelepon() : "-",
                        k.getJabatan()
                    });
                }
            }
        }
    }

    private void refreshOtherDepts() {
        deptModel.setRowCount(0);
        List<Departemen> list = userModel.getAllDepartemen();
        if (list != null) {
            for (Departemen d : list) {
                if (currentDept == null || d.getIdDepartemen() != currentDept.getIdDepartemen()) {
                    deptModel.addRow(new Object[]{
                        d.getIdDepartemen(), 
                        d.getNamaDepartemen(), 
                        d.getLokasi(), 
                        d.getDeskripsiTugas(),
                        d.getIdManajer()
                    });
                }
            }
        }
    }

    private void showEmpDialog(Pengguna pToEdit, Karyawan kToEdit) {
        boolean isEditMode = (pToEdit != null && kToEdit != null);
        String title = isEditMode ? "Edit Employee Member" : "Add New Employee Member";

        JTextField emailF = new JTextField(isEditMode ? pToEdit.getEmail() : "");
        JTextField fnameF = new JTextField(isEditMode ? pToEdit.getNamaDepan() : "");
        JTextField lnameF = new JTextField(isEditMode ? pToEdit.getNamaBelakang() : "");
        JTextField passF = new JTextField(isEditMode ? pToEdit.getPassword() : "");
        JTextField phoneF = new JTextField(isEditMode ? pToEdit.getNomorTelepon() : "");
        JTextField jabatanF = new JTextField(isEditMode ? kToEdit.getJabatan() : "");
        
        JComboBox<Departemen> deptC = new JComboBox<>();
        if (currentDept != null) {
            deptC.addItem(currentDept);
            deptC.setEnabled(false);
        } else {
            List<Departemen> depts = userModel.getAllDepartemen();
            for (Departemen d : depts) deptC.addItem(d);
        }

        Object[] message = {
            "Email:", emailF,
            "First Name:", fnameF,
            "Last Name:", lnameF,
            "Password:", passF,
            "Phone:", phoneF,
            "Jabatan:", jabatanF,
            "Department:", deptC
        };

        int option = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Pengguna p = isEditMode ? pToEdit : new Pengguna();
                p.setEmail(emailF.getText());
                p.setNamaDepan(fnameF.getText());
                p.setNamaBelakang(lnameF.getText());
                p.setPassword(passF.getText());
                p.setNomorTelepon(phoneF.getText());

                Departemen selectedDept = (Departemen) deptC.getSelectedItem();
                int targetDeptId = selectedDept != null ? selectedDept.getIdDepartemen() : 0;

                boolean success;
                if (isEditMode) {
                    kToEdit.setJabatan(jabatanF.getText());
                    success = userModel.updateEmployee(p, kToEdit);
                } else {
                    success = userModel.addEmployee(p, targetDeptId, jabatanF.getText());
                }

                if (success) {
                    GUIHelper.showInfo(this, isEditMode ? "Employee updated successfully!" : "Employee added successfully!");
                    refreshDepartmentMembers();
                } else {
                    GUIHelper.showError(this, "Gagal memproses data karyawan.");
                }
            } catch (Exception ex) {
                GUIHelper.showError(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showDeptDialog(Departemen deptToEdit) {
        boolean isEditMode = (deptToEdit != null);
        String title = isEditMode ? "Edit Department" : "Add Department";

        JTextField nameF = new JTextField(isEditMode ? deptToEdit.getNamaDepartemen() : "");
        JTextField locF = new JTextField(isEditMode ? deptToEdit.getLokasi() : "");
        JTextArea taskA = new JTextArea(3, 20);
        taskA.setText(isEditMode ? deptToEdit.getDeskripsiTugas() : "");
        
        String currentMgrStr = "";
        if (isEditMode && deptToEdit.getIdManajer() != null && deptToEdit.getIdManajer() > 0) {
            currentMgrStr = String.valueOf(deptToEdit.getIdManajer());
        }
        JTextField mgrF = new JTextField(currentMgrStr);

        Object[] message = {
            "Name:", nameF, 
            "Location:", locF, 
            "Task:", new JScrollPane(taskA),
            "Manager ID (Optional):", mgrF
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Departemen d = isEditMode ? deptToEdit : new Departemen();
                d.setNamaDepartemen(nameF.getText());
                d.setLokasi(locF.getText());
                d.setDeskripsiTugas(taskA.getText());
                
                if (!mgrF.getText().trim().isEmpty()) {
                    d.setIdManajer(Integer.parseInt(mgrF.getText().trim()));
                } else {
                    d.setIdManajer(0);
                }

                boolean success = isEditMode ? userModel.updateDepartemen(d) : userModel.addDepartemen(d);

                if (success) {
                    GUIHelper.showInfo(this, isEditMode ? "Department updated!" : "Department added!");
                    refreshOtherDepts();
                    loadCurrentUserInfo();
                    setupTopPanel(); 
                    revalidate(); repaint();
                } else {
                    GUIHelper.showError(this, "Gagal menyimpan data.");
                }
            } catch (NumberFormatException ex) {
                GUIHelper.showError(this, "ID Manager harus berupa angka!");
            } catch (Exception ex) {
                GUIHelper.showError(this, "Error: " + ex.getMessage());
            }
        }
    }
}