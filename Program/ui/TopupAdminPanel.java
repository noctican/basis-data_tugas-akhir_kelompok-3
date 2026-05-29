package ui;

import entities.RiwayatTopup;
import helpers.GUIHelper;
import models.UserModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class TopupAdminPanel extends JPanel {
    private UserModel userModel;
    private JTable topupTable;
    private DefaultTableModel topupModel;
    private JComboBox<String> statusFilter;

    public TopupAdminPanel() {
        userModel = new UserModel();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setupTopPanel();
        setupTable();
        setupBottomPanel();
        
        refreshTopups();
    }

    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(GUIHelper.createContentHeader("Top-up Requests Management"), BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Filter by Status:"));
        String[] statuses = {"PENDING", "ALL", "SUCCESS", "FAILED"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.addActionListener(e -> refreshTopups());
        filterPanel.add(statusFilter);
        
        topPanel.add(filterPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void setupTable() {
        topupModel = new DefaultTableModel(new String[]{"ID Pengguna", "ID Topup", "Date", "Nominal", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        topupTable = new JTable(topupModel);
        add(new JScrollPane(topupTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTopups());

        JButton approveBtn = new JButton("Approve");
        approveBtn.setBackground(new Color(40, 167, 69)); // Success green
        approveBtn.setForeground(Color.WHITE);
        approveBtn.addActionListener(e -> handleProcessTopup(true));

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setBackground(new Color(220, 53, 69)); // Danger red
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.addActionListener(e -> handleProcessTopup(false));

        bottom.add(refreshBtn);
        bottom.add(approveBtn);
        bottom.add(rejectBtn);
        
        add(bottom, BorderLayout.SOUTH);
    }

    private void refreshTopups() {
        topupModel.setRowCount(0);
        String selectedStatus = (String) statusFilter.getSelectedItem();
        List<RiwayatTopup> list = userModel.getAllTopupsFiltered(selectedStatus);
        for (RiwayatTopup r : list) {
            topupModel.addRow(new Object[]{r.getIdPengguna(), r.getIdTopup(), r.getTanggalTopup(), r.getNominal(), r.getStatus()});
        }
    }

    private void handleProcessTopup(boolean isApprove) {
        int row = topupTable.getSelectedRow();
        if (row == -1) {
            GUIHelper.showError(this, "Please select a top-up request to process.");
            return;
        }

        String status = (String) topupModel.getValueAt(row, 4);
        if (!status.equals("PENDING")) {
            GUIHelper.showError(this, "Only pending top-up requests can be processed.");
            return;
        }

        int idTopup = (int) topupModel.getValueAt(row, 1);
        BigDecimal nominal = (BigDecimal) topupModel.getValueAt(row, 3);
        
        String actionStr = isApprove ? "approve" : "reject";

        if (GUIHelper.confirm(this, "Are you sure you want to " + actionStr + " this top-up of " + nominal + "?")) {
            boolean success = userModel.processTopup(idTopup, isApprove);
            if (success) refreshTopups();
        }
    }
}
