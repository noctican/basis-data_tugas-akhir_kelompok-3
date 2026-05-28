package ui;

import helpers.GUIHelper;
import models.ReturnModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReturnAdminPanel extends JPanel {
    private ReturnModel returnModel;
    private JTable returnTable;
    private DefaultTableModel returnModelT;
    
    // Memindahkan komponen filter & aksi ke variabel kelas
    private JComboBox<String> statusCombo;
    private JButton validateBtn;
    private JButton rejectBtn;

    public ReturnAdminPanel() {
        returnModel = new ReturnModel();
        setLayout(new BorderLayout());

        // 1. Membuat Panel Atas (Header + Dropdown Filter)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(GUIHelper.createContentHeader("Returns Validation"), BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter Status: "));
        
        // Item dropdown disesuaikan dengan nilai konkrit string pada DDL/DML
        statusCombo = new JComboBox<>(new String[]{"PENDING", "APPROVED", "REJECTED"});
        // Otomatis refresh tabel setiap kali pilihan dropdown berubah
        statusCombo.addActionListener(e -> refreshReturns()); 
        filterPanel.add(statusCombo);
        
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 2. Membuat Tabel Konten (Center)
        returnModelT = new DefaultTableModel(new String[]{"Retur ID", "Trx ID", "Product ID", "SKU", "Qty", "Reason"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        returnTable = new JTable(returnModelT);
        add(new JScrollPane(returnTable), BorderLayout.CENTER);

        // 3. Membuat Panel Tombol Aksi (South)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        validateBtn = new JButton("Validate & Approve");
        rejectBtn = new JButton("Reject");
        JButton refreshBtn = new JButton("Refresh");

        validateBtn.addActionListener(e -> handleValidateReturn());
        rejectBtn.addActionListener(e -> handleRejectReturn());
        refreshBtn.addActionListener(e -> refreshReturns());

        buttonPanel.add(validateBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(refreshBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load data pertama kali
        refreshReturns();
    }
    
    private void refreshReturns() {
        returnModelT.setRowCount(0);
        
        // Mengambil status yang sedang dipilih dari dropdown ("PENDING", "disetujui", atau "ditolak")
        String selectedStatus = (String) statusCombo.getSelectedItem();
        
        // Memanggil fungsi baru di model yang menerima parameter status
        List<Object[]> data = returnModel.getReturnsByStatus(selectedStatus);
        for (Object[] row : data) returnModelT.addRow(row);

        // FITUR KEAMANAN UI: Tombol Approve & Reject hanya aktif jika data berstatus 'PENDING'
        boolean isPending = "PENDING".equals(selectedStatus);
        validateBtn.setEnabled(isPending);
        rejectBtn.setEnabled(isPending);
    }

    private void handleValidateReturn() {
        int selectedRow = returnTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row in the table first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int returId = Integer.parseInt(returnTable.getValueAt(selectedRow, 0).toString());
        boolean isUpdated = returnModel.approveReturn(returId); 
        if (isUpdated) {
            JOptionPane.showMessageDialog(this, "Return ID: " + returId + " Successfully approved! (Status updated to 'APPROVED')", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshReturns();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to process return approval.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRejectReturn() {
        int selectedRow = returnTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row in the table first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int returId = Integer.parseInt(returnTable.getValueAt(selectedRow, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to reject Return ID: " + returId + "?", "Confirm Reject", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean isRejected = returnModel.rejectReturn(returId);
            if (isRejected) {
                JOptionPane.showMessageDialog(this, "Status Return ID: " + returId + " Successfully updated to 'REJECTED'!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshReturns();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reject return data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}