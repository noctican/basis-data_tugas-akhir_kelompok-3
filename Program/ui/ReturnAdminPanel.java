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

    public ReturnAdminPanel() {
        returnModel = new ReturnModel();
        setLayout(new BorderLayout());
        add(GUIHelper.createContentHeader("Returns Validation"), BorderLayout.NORTH);

        returnModelT = new DefaultTableModel(new String[]{"Retur ID", "Trx ID", "Product ID", "SKU", "Qty", "Reason"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        returnTable = new JTable(returnModelT);
        add(new JScrollPane(returnTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton validateBtn = new JButton("Validate & Approve");
        JButton deleteBtn = new JButton("Tolak / Hapus Return");
        JButton refreshBtn = new JButton("Refresh");

        validateBtn.addActionListener(e -> handleValidateReturn());
        deleteBtn.addActionListener(e -> handleDeleteReturn());
        refreshBtn.addActionListener(e -> refreshReturns());

        buttonPanel.add(validateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshReturns();
    }
    
    private void refreshReturns() {
        returnModelT.setRowCount(0);
        List<Object[]> data = returnModel.getAllPendingReturns();
        for (Object[] row : data) returnModelT.addRow(row);
    }

    private void handleValidateReturn() {
        int selectedRow = returnTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris data pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mengambil Retur ID dari kolom index ke-0 dan ubah ke Integer
        int returId = Integer.parseInt(returnTable.getValueAt(selectedRow, 0).toString());

        boolean isUpdated = returnModel.approveReturn(returId); 
        if (isUpdated) {
            JOptionPane.showMessageDialog(this, "Return ID: " + returId + " Berhasil Disetujui! (Status updated to APPROVED)", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshReturns();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memproses persetujuan return.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteReturn() {
        int selectedRow = returnTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data pada tabel yang ingin dihapus/ditolak!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int returId = Integer.parseInt(returnTable.getValueAt(selectedRow, 0).toString());
        
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menolak/menghapus Return ID: " + returId + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean isDeleted = returnModel.deleteReturn(returId);
            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "Data return berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshReturns();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data return.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}