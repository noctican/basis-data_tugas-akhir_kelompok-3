package ui;

import entities.*;
import helpers.GUIHelper;
import models.CatalogModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CatalogPanel extends JPanel {
    private CatalogModel catalogModel;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JComboBox<Kategori> kategoriCombo;
    private JComboBox<SubKategori> subKategoriCombo;
    private JTextField searchField;

    public CatalogPanel() {
        catalogModel = new CatalogModel();
        setLayout(new BorderLayout());

        setupTopPanel();
        setupTable();
        setupBottomPanel();

        refreshProducts();
    }

    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> refreshProducts());
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);
    }

    private void setupTable() {
        String[] columns = {"ID", "Name", "Base Price", "Category", "Sub-Category", "Demography"};
        productTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(productTableModel);
        add(new JScrollPane(productTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton addKategoriBtn = new JButton("Manage Categories");
        addKategoriBtn.addActionListener(e -> showCategoryManager());
        
        JButton addProdukBtn = new JButton("Add Product");
        addProdukBtn.addActionListener(e -> showAddProductDialog());

        JButton viewVarianBtn = new JButton("View Variants");
        viewVarianBtn.addActionListener(e -> showVarianManager());

        bottomPanel.add(addKategoriBtn);
        bottomPanel.add(addProdukBtn);
        bottomPanel.add(viewVarianBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshProducts() {
        productTableModel.setRowCount(0);
        List<Produk> list = catalogModel.searchProduk(searchField.getText());
        for (Produk p : list) {
            productTableModel.addRow(new Object[]{
                p.getIdProduk(), p.getNamaProduk(), p.getHargaBase(),
                p.getIdKategori(), p.getIdSubKategori(), p.getDemografi()
            });
        }
    }

    private void showCategoryManager() {
        // Simple dialog to add Kategori/SubKategori
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage Categories", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(0, 1, 10, 10));
        
        JTextField catField = new JTextField();
        JButton addCatBtn = new JButton("Add Main Category");
        addCatBtn.addActionListener(e -> {
            if (catalogModel.addKategori(catField.getText())) {
                GUIHelper.showInfo(dialog, "Category added!");
                catField.setText("");
            }
        });

        dialog.add(new JLabel("New Main Category Name:"));
        dialog.add(catField);
        dialog.add(addCatBtn);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddProductDialog() {
        // Modal for Produk
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Product", true);
        dialog.setSize(500, 500);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField();
        JTextArea descA = new JTextArea(3, 20);
        JTextField priceF = new JTextField("0");
        String[] demos = {"pria", "wanita", "anak", "equipment"};
        JComboBox<String> demoC = new JComboBox<>(demos);
        JTextField actF = new JTextField();

        kategoriCombo = new JComboBox<>();
        List<Kategori> cats = catalogModel.getAllKategori();
        for (Kategori c : cats) kategoriCombo.addItem(c);

        subKategoriCombo = new JComboBox<>();
        kategoriCombo.addActionListener(e -> {
            subKategoriCombo.removeAllItems();
            Kategori selected = (Kategori) kategoriCombo.getSelectedItem();
            if (selected != null) {
                List<SubKategori> subs = catalogModel.getSubKategoriByKategori(selected.getIdKategori());
                for (SubKategori s : subs) subKategoriCombo.addItem(s);
            }
        });

        // Add components to GBC...
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; dialog.add(nameF, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; dialog.add(new JScrollPane(descA), gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Base Price:"), gbc);
        gbc.gridx = 1; dialog.add(priceF, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Demography:"), gbc);
        gbc.gridx = 1; dialog.add(demoC, gbc);

        gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Activity:"), gbc);
        gbc.gridx = 1; dialog.add(actF, gbc);

        gbc.gridx = 0; gbc.gridy = 5; dialog.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; dialog.add(kategoriCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 6; dialog.add(new JLabel("Sub-Category:"), gbc);
        gbc.gridx = 1; dialog.add(subKategoriCombo, gbc);

        JButton saveBtn = new JButton("Save Product");
        saveBtn.addActionListener(e -> {
            try {
                Produk p = new Produk();
                p.setNamaProduk(nameF.getText());
                p.setDeskripsiProduk(descA.getText());
                p.setHargaBase(new BigDecimal(priceF.getText()));
                p.setDemografi((String) demoC.getSelectedItem());
                p.setAktivitas(actF.getText());
                p.setIdKategori(((Kategori) kategoriCombo.getSelectedItem()).getIdKategori());
                p.setIdSubKategori(((SubKategori) subKategoriCombo.getSelectedItem()).getIdSubKategori());
                
                if (catalogModel.addProduk(p)) {
                    GUIHelper.showInfo(dialog, "Product saved!");
                    dialog.dispose();
                    refreshProducts();
                }
            } catch (Exception ex) { GUIHelper.showError(dialog, "Error: " + ex.getMessage()); }
        });
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; dialog.add(saveBtn, gbc);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showVarianManager() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            GUIHelper.showError(this, "Select a product first!");
            return;
        }
        int idProduk = (int) productTableModel.getValueAt(row, 0);
        String productName = (String) productTableModel.getValueAt(row, 1);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Variants: " + productName, true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        DefaultTableModel varModel = new DefaultTableModel(new String[]{"SKU", "Stock", "Color", "Price", "Size"}, 0);
        JTable varTable = new JTable(varModel);
        dialog.add(new JScrollPane(varTable), BorderLayout.CENTER);

        List<VarianProduk> vars = catalogModel.getVariansByProduk(idProduk);
        for (VarianProduk v : vars) varModel.addRow(new Object[]{v.getSku(), v.getStock(), v.getWarna(), v.getHargaVarian(), v.getUkuran()});

        JButton addVarBtn = new JButton("Add Variant");
        addVarBtn.addActionListener(e -> {
            // Nested simple input dialog or similar
            JTextField skuF = new JTextField();
            JTextField stockF = new JTextField("0");
            JTextField colorF = new JTextField();
            JTextField priceF = new JTextField("0");
            JTextField sizeF = new JTextField();
            
            Object[] message = {"SKU:", skuF, "Stock:", stockF, "Color:", colorF, "Price:", priceF, "Size:", sizeF};
            int option = JOptionPane.showConfirmDialog(dialog, message, "New Variant", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                VarianProduk v = new VarianProduk();
                v.setIdProduk(idProduk);
                v.setSku(skuF.getText());
                v.setStock(Integer.parseInt(stockF.getText()));
                v.setWarna(colorF.getText());
                v.setHargaVarian(new BigDecimal(priceF.getText()));
                v.setUkuran(sizeF.getText());
                if (catalogModel.addVarian(v)) {
                    varModel.addRow(new Object[]{v.getSku(), v.getStock(), v.getWarna(), v.getHargaVarian(), v.getUkuran()});
                }
            }
        });
        dialog.add(addVarBtn, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
