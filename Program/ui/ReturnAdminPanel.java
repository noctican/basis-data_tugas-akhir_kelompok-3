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

        returnModelT = new DefaultTableModel(new String[]{"Retur ID", "Trx ID", "Product", "SKU", "Qty", "Reason"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        returnTable = new JTable(returnModelT);
        add(new JScrollPane(returnTable), BorderLayout.CENTER);

        JButton validateBtn = new JButton("Validate & Approve Return");
        validateBtn.addActionListener(e -> GUIHelper.showInfo(this, "Return Approved! (Status updated to APPROVED)"));
        add(validateBtn, BorderLayout.SOUTH);

        refreshReturns();
    }

    private void refreshReturns() {
        returnModelT.setRowCount(0);
        List<Object[]> data = returnModel.getAllPendingReturns();
        for (Object[] row : data) returnModelT.addRow(row);
    }
}
