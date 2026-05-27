package ui;

import entities.*;
import helpers.GUIHelper;
import models.UserModel;
import session.UserSession;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProfilePanel extends JPanel {
    private UserModel userModel;
    private JTable alamatTable;
    private DefaultTableModel alamatModel;

    public ProfilePanel() {
        userModel = new UserModel();
        setLayout(new BorderLayout());

        setupProfileHeader();
        setupAlamatTable();
        setupBottomPanel();

        refreshAlamat();
    }

    private void setupProfileHeader() {
        Pengguna u = UserSession.getCurrentUser();
        JPanel header = GUIHelper.createContentHeader("Welcome, " + u.getFullName());
        add(header, BorderLayout.NORTH);
    }

    private void setupAlamatTable() {
        alamatModel = new DefaultTableModel(new String[]{"ID", "Penerima", "Jalan", "Kota", "Provinsi", "No Telp"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        alamatTable = new JTable(alamatModel);
        add(new JScrollPane(alamatTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JButton addBtn = new JButton("Add New Address");
        addBtn.addActionListener(e -> showAddAlamatDialog());
        add(addBtn, BorderLayout.SOUTH);
    }

    private void refreshAlamat() {
        alamatModel.setRowCount(0);
        List<AlamatPelanggan> list = userModel.getAlamatByCustomer(UserSession.getCurrentUser().getIdPengguna());
        for (AlamatPelanggan a : list) {
            alamatModel.addRow(new Object[]{a.getIdAlamat(), a.getNamaPenerima(), a.getJalan(), a.getKota(), a.getProvinsi(), a.getNoTelp()});
        }
    }

    private void showAddAlamatDialog() {
        JTextField recipientF = new JTextField();
        JTextField streetF = new JTextField();
        JTextField cityF = new JTextField();
        JTextField provF = new JTextField();
        JTextField phoneF = new JTextField();

        Object[] message = {"Recipient Name:", recipientF, "Street:", streetF, "City:", cityF, "Province:", provF, "Phone:", phoneF};
        int option = JOptionPane.showConfirmDialog(this, message, "Add Address", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            GUIHelper.showInfo(this, "Feature to save address coming soon (Model update required)");
        }
    }
}
