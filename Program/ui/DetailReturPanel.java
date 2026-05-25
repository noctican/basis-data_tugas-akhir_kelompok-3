package ui;

import entities.DetailRetur;
import entities.DetailTransaksi;
import entities.Retur;
import models.DetailReturModel;
import models.DetailTransaksiModel;
import models.ReturModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DetailReturPanel extends JPanel {
    private DetailReturModel model = new DetailReturModel();
    private ReturModel returModel = new ReturModel();
    private DetailTransaksiModel dtModel = new DetailTransaksiModel();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtKuantitas, txtAlasan;
    private JComboBox<Retur> cbRetur;
    private JComboBox<DetailTransaksi> cbDetailTransaksi;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedIdTransaksi = -1;
    private int selectedIdRetur = -1;
    private int selectedIdProduk = -1;
    private String selectedSku = null;

    public DetailReturPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID Transaksi", "ID Retur", "ID Produk", "SKU", "Kuantitas", "Alasan"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("Retur:"));
        cbRetur = new JComboBox<>();
        formPanel.add(cbRetur);
        formPanel.add(new JLabel("Item Transaksi:"));
        cbDetailTransaksi = new JComboBox<>();
        formPanel.add(cbDetailTransaksi);
        formPanel.add(new JLabel("Kuantitas:"));
        txtKuantitas = new JTextField();
        formPanel.add(txtKuantitas);
        formPanel.add(new JLabel("Alasan:"));
        txtAlasan = new JTextField();
        formPanel.add(txtAlasan);

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

        // Custom renderer for cbDetailTransaksi to show useful info
        cbDetailTransaksi.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DetailTransaksi) {
                    DetailTransaksi dt = (DetailTransaksi) value;
                    setText("TX#" + dt.getIdTransaksi() + " Prod#" + dt.getIdProduk() + " " + dt.getSku());
                }
                return this;
            }
        });

        // Events
        btnRefresh.addActionListener(e -> {
            refreshTable();
            refreshRetur();
            refreshDT();
        });
        btnAdd.addActionListener(e -> addDetail());
        btnUpdate.addActionListener(e -> updateDetail());
        btnDelete.addActionListener(e -> deleteDetail());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedIdTransaksi = (int) tableModel.getValueAt(row, 0);
                selectedIdRetur = (int) tableModel.getValueAt(row, 1);
                selectedIdProduk = (int) tableModel.getValueAt(row, 2);
                selectedSku = (String) tableModel.getValueAt(row, 3);
                txtKuantitas.setText(tableModel.getValueAt(row, 4).toString());
                txtAlasan.setText((String) tableModel.getValueAt(row, 5));
                
                for (int i = 0; i < cbRetur.getItemCount(); i++) {
                    if (cbRetur.getItemAt(i).getIdRetur() == selectedIdRetur) {
                        cbRetur.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < cbDetailTransaksi.getItemCount(); i++) {
                    DetailTransaksi dt = cbDetailTransaksi.getItemAt(i);
                    if (dt.getIdTransaksi() == selectedIdTransaksi && dt.getIdProduk() == selectedIdProduk && dt.getSku().equals(selectedSku)) {
                        cbDetailTransaksi.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        refreshTable();
        refreshRetur();
        refreshDT();
    }

    private void refreshRetur() {
        try {
            cbRetur.removeAllItems();
            List<Retur> list = returModel.getAll();
            for (Retur r : list) {
                cbRetur.addItem(r);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Retur: " + e.getMessage());
        }
    }

    private void refreshDT() {
        try {
            cbDetailTransaksi.removeAllItems();
            List<DetailTransaksi> list = dtModel.getAll();
            for (DetailTransaksi dt : list) {
                cbDetailTransaksi.addItem(dt);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error DetailTransaksi: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<DetailRetur> list = model.getAll();
            for (DetailRetur dr : list) {
                tableModel.addRow(new Object[]{dr.getIdTransaksi(), dr.getIdRetur(), dr.getIdProduk(), dr.getSku(), dr.getKuantitas(), dr.getAlasan()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Table: " + e.getMessage());
        }
    }

    private void addDetail() {
        try {
            Retur r = (Retur) cbRetur.getSelectedItem();
            DetailTransaksi dt = (DetailTransaksi) cbDetailTransaksi.getSelectedItem();
            if (r == null || dt == null) return;
            DetailRetur dr = new DetailRetur(dt.getIdTransaksi(), r.getIdRetur(), dt.getIdProduk(), dt.getSku(), Integer.parseInt(txtKuantitas.getText()), txtAlasan.getText());
            model.insert(dr);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Add: " + e.getMessage());
        }
    }

    private void updateDetail() {
        if (selectedSku == null) return;
        try {
            DetailRetur dr = new DetailRetur(selectedIdTransaksi, selectedIdRetur, selectedIdProduk, selectedSku, Integer.parseInt(txtKuantitas.getText()), txtAlasan.getText());
            model.update(dr);
            refreshTable();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }

    private void deleteDetail() {
        if (selectedSku == null) return;
        try {
            model.delete(selectedIdTransaksi, selectedIdRetur, selectedIdProduk, selectedSku);
            refreshTable();
            selectedSku = null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Delete: " + e.getMessage());
        }
    }
}
