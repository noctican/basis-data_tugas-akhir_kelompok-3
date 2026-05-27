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
        addProdukBtn.addActionListener(e -> showProductDialog(null));

        JButton editProdukBtn = new JButton("Edit Product");
        editProdukBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row == -1) {
                GUIHelper.showError(this, "Silakan pilih produk yang ingin diedit terlebih dahulu!");
                return;
            }
            int idProduk = (int) productTableModel.getValueAt(row, 0);
            Produk produkTerpilih = catalogModel.getProdukById(idProduk);
            if (produkTerpilih != null) {
                showProductDialog(produkTerpilih); 
            }
        });

        JButton deleteProdukBtn = new JButton("Delete Product");
        deleteProdukBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row == -1) {
                GUIHelper.showError(this, "Silakan pilih produk terlebih dahulu yang ingin dihapus!");
                return;
            }
            int idProduk = (int) productTableModel.getValueAt(row, 0);
            String namaProduk = (String) productTableModel.getValueAt(row, 1);

            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Apakah Anda yakin ingin menghapus produk '" + namaProduk + "' beserta seluruh variannya?", 
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (catalogModel.deleteProduk(idProduk)) {
                    GUIHelper.showInfo(this, "Produk berhasil dihapus!");
                    refreshProducts();
                } else {
                    GUIHelper.showError(this, "Gagal menghapus produk.");
                }
            }
        });

        JButton viewVarianBtn = new JButton("View Variants");
        viewVarianBtn.addActionListener(e -> showVarianManager());

        bottomPanel.add(addKategoriBtn);
        bottomPanel.add(addProdukBtn);
        bottomPanel.add(editProdukBtn);
        bottomPanel.add(deleteProdukBtn);
        bottomPanel.add(viewVarianBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshProducts() {
        productTableModel.setRowCount(0);
        List<Produk> list = catalogModel.searchProduk(searchField.getText());
        for (Produk p : list) {
            String namaKategori = "Unknown";
            String namaSubKategori = "Unknown";

            Kategori kat = catalogModel.getKategoriById(p.getIdKategori());
            if (kat != null) {
                namaKategori = kat.getNamaKategori();
            }
            
            SubKategori sub = catalogModel.getSubKategoriById(p.getIdKategori(), p.getIdSubKategori());
            if (sub != null) {
                namaSubKategori = sub.getNamaSubKategori();
            }

            productTableModel.addRow(new Object[]{
                p.getIdProduk(), 
                p.getNamaProduk(), 
                p.getHargaBase(),
                namaKategori,    
                namaSubKategori, 
                p.getDemografi()
            });
        }
    }

private void showCategoryManager() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage Categories & Sub-Categories", true);
        dialog.setSize(550, 400);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Main Category"));
        
        JComboBox<Kategori> katCombo = new JComboBox<>();
        katCombo.setPreferredSize(new Dimension(150, 25));
        
        JButton btnAddKat = new JButton("Add");
        JButton btnEditKat = new JButton("Edit");
        JButton btnDelKat = new JButton("Delete");

        topPanel.add(new JLabel("Select Category:"));
        topPanel.add(katCombo);
        topPanel.add(btnAddKat);
        topPanel.add(btnEditKat);
        topPanel.add(btnDelKat);

        dialog.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Sub-Categories"));

        String[] subCols = {"ID Sub", "Sub-Category Name"};
        DefaultTableModel subModel = new DefaultTableModel(subCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable subTable = new JTable(subModel);
        
        subTable.removeColumn(subTable.getColumnModel().getColumn(0));
        centerPanel.add(new JScrollPane(subTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAddSub = new JButton("Add Sub-Category");
        JButton btnEditSub = new JButton("Edit Sub-Category");
        JButton btnDelSub = new JButton("Delete Sub-Category");

        bottomPanel.add(btnAddSub);
        bottomPanel.add(btnEditSub);
        bottomPanel.add(btnDelSub);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.add(centerPanel, BorderLayout.CENTER);

        Runnable refreshSubKategori = () -> {
            subModel.setRowCount(0);
            Kategori selectedKat = (Kategori) katCombo.getSelectedItem();
            if (selectedKat != null) {
                List<SubKategori> subs = catalogModel.getSubKategoriByKategori(selectedKat.getIdKategori());
                for (SubKategori s : subs) {
                    subModel.addRow(new Object[]{s.getIdSubKategori(), s.getNamaSubKategori()});
                }
            }
        };

        Runnable refreshKategori = () -> {
            katCombo.removeAllItems();
            List<Kategori> kats = catalogModel.getAllKategori();
            for (Kategori k : kats) {
                katCombo.addItem(k);
            }
            if (katCombo.getItemCount() > 0) {
                katCombo.setSelectedIndex(0);
            }
        };

        katCombo.addActionListener(e -> {
            if (katCombo.getSelectedItem() != null) refreshSubKategori.run();
        });

        btnAddKat.addActionListener(e -> {
            String nama = JOptionPane.showInputDialog(dialog, "Masukkan Nama Kategori Baru:");
            if (nama != null && !nama.trim().isEmpty()) {
                if (catalogModel.addKategori(nama.trim())) {
                    refreshKategori.run();
                } else {
                    GUIHelper.showError(dialog, "Gagal menambahkan kategori.");
                }
            }
        });

        btnEditKat.addActionListener(e -> {
            Kategori selected = (Kategori) katCombo.getSelectedItem();
            if (selected == null) return;
            
            String nama = (String) JOptionPane.showInputDialog(dialog, "Edit Nama Kategori:", "Edit Kategori", 
                                JOptionPane.PLAIN_MESSAGE, null, null, selected.getNamaKategori());
            
            if (nama != null && !nama.trim().isEmpty()) {
                selected.setNamaKategori(nama.trim());
                if (catalogModel.updateKategori(selected)) {
                    refreshKategori.run();
                } else {
                    GUIHelper.showError(dialog, "Gagal mengupdate kategori.");
                }
            }
        });

        btnDelKat.addActionListener(e -> {
            Kategori selected = (Kategori) katCombo.getSelectedItem();
            if (selected == null) return;
            
            int confirm = JOptionPane.showConfirmDialog(dialog, 
                    "Menghapus Kategori '" + selected.getNamaKategori() + "' juga akan menghapus produk dan sub-kategori di dalamnya. Yakin?", 
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (catalogModel.deleteKategori(selected.getIdKategori())) {
                    refreshKategori.run();
                } else {
                    GUIHelper.showError(dialog, "Gagal menghapus kategori.");
                }
            }
        });

        btnAddSub.addActionListener(e -> {
            Kategori selectedKat = (Kategori) katCombo.getSelectedItem();
            if (selectedKat == null) {
                GUIHelper.showError(dialog, "Pilih Kategori Utama terlebih dahulu!");
                return;
            }
            String nama = JOptionPane.showInputDialog(dialog, "Masukkan Nama Sub-Kategori Baru:");
            if (nama != null && !nama.trim().isEmpty()) {
                SubKategori sub = new SubKategori();
                sub.setIdKategori(selectedKat.getIdKategori());
                sub.setNamaSubKategori(nama.trim());
                
                if (catalogModel.addSubKategori(sub)) {
                    refreshSubKategori.run();
                } else {
                    GUIHelper.showError(dialog, "Gagal menambahkan sub-kategori.");
                }
            }
        });

        btnEditSub.addActionListener(e -> {
            int row = subTable.getSelectedRow();
            if (row == -1) {
                GUIHelper.showError(dialog, "Pilih sub-kategori di tabel yang ingin diedit!");
                return;
            }
            int idSub = (int) subModel.getValueAt(row, 0); // Ambil ID asli yang tersembunyi
            String currentName = (String) subModel.getValueAt(row, 1);
            Kategori selectedKat = (Kategori) katCombo.getSelectedItem();

            String nama = (String) JOptionPane.showInputDialog(dialog, "Edit Nama Sub-Kategori:", "Edit Sub-Kategori", 
                                JOptionPane.PLAIN_MESSAGE, null, null, currentName);
            
            if (nama != null && !nama.trim().isEmpty()) {
                SubKategori sub = new SubKategori();
                sub.setIdKategori(selectedKat.getIdKategori());
                sub.setIdSubKategori(idSub);
                sub.setNamaSubKategori(nama.trim());
                
                if (catalogModel.updateSubKategori(sub)) {
                    refreshSubKategori.run();
                } else {
                    GUIHelper.showError(dialog, "Gagal mengupdate sub-kategori.");
                }
            }
        });

        btnDelSub.addActionListener(e -> {
            int row = subTable.getSelectedRow();
            if (row == -1) {
                GUIHelper.showError(dialog, "Pilih sub-kategori di tabel yang ingin dihapus!");
                return;
            }
            int idSub = (int) subModel.getValueAt(row, 0);
            String currentName = (String) subModel.getValueAt(row, 1);
            Kategori selectedKat = (Kategori) katCombo.getSelectedItem();

            int confirm = JOptionPane.showConfirmDialog(dialog, 
                    "Yakin ingin menghapus sub-kategori '" + currentName + "'?", 
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Perhatikan: PK dari SubKategori di DDL Anda adalah (id_kategori, id_sub_kategori)
                if (catalogModel.deleteSubKategori(selectedKat.getIdKategori(), idSub)) {
                    refreshSubKategori.run();
                } else {
                    GUIHelper.showError(dialog, "Gagal menghapus sub-kategori.");
                }
            }
        });

        refreshKategori.run();

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showProductDialog(Produk produk) {
        boolean isEditMode = (produk != null);
        String title = isEditMode ? "Edit Product" : "Add New Product";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(500, 500);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(isEditMode ? produk.getNamaProduk() : "");
        JTextArea descA = new JTextArea(3, 20);
        descA.setText(isEditMode ? produk.getDeskripsiProduk() : "");
        JTextField priceF = new JTextField(isEditMode ? produk.getHargaBase().toString() : "0");
        String[] demos = {"pria", "wanita", "anak", "equipment"};
        JComboBox<String> demoC = new JComboBox<>(demos);
        if (isEditMode) demoC.setSelectedItem(produk.getDemografi());
        JTextField actF = new JTextField(isEditMode ? produk.getAktivitas() : "");

        kategoriCombo = new JComboBox<>();
        List<Kategori> cats = catalogModel.getAllKategori();
        for (Kategori c : cats) {
            kategoriCombo.addItem(c);
            if (isEditMode && c.getIdKategori() == produk.getIdKategori()) {
                kategoriCombo.setSelectedItem(c);
            }
        }

        subKategoriCombo = new JComboBox<>();
        kategoriCombo.addActionListener(e -> {
            subKategoriCombo.removeAllItems();
            Kategori selected = (Kategori) kategoriCombo.getSelectedItem();
            if (selected != null) {
                List<SubKategori> subs = catalogModel.getSubKategoriByKategori(selected.getIdKategori());
                for (SubKategori s : subs) {
                    subKategoriCombo.addItem(s);
                    if (isEditMode && s.getIdSubKategori() == produk.getIdSubKategori()) {
                        subKategoriCombo.setSelectedItem(s);
                    }
                }
            }
        });
        
        kategoriCombo.getActionListeners()[0].actionPerformed(null);

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

        JButton saveBtn = new JButton(isEditMode ? "Update Product" : "Save Product");
        saveBtn.addActionListener(e -> {
            try {
                Produk p = isEditMode ? produk : new Produk();
                p.setNamaProduk(nameF.getText());
                p.setDeskripsiProduk(descA.getText());
                p.setHargaBase(new BigDecimal(priceF.getText()));
                p.setDemografi((String) demoC.getSelectedItem());
                p.setAktivitas(actF.getText());
                p.setIdKategori(((Kategori) kategoriCombo.getSelectedItem()).getIdKategori());
                p.setIdSubKategori(((SubKategori) subKategoriCombo.getSelectedItem()).getIdSubKategori());
                
                boolean success = isEditMode ? catalogModel.updateProduk(p) : catalogModel.addProduk(p);
                
                if (success) {
                    GUIHelper.showInfo(dialog, isEditMode ? "Product updated!" : "Product saved!");
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
            GUIHelper.showError(this, "Silakan pilih produk terlebih dahulu untuk melihat varian!");
            return;
        }
        int idProduk = (int) productTableModel.getValueAt(row, 0);
        String productName = (String) productTableModel.getValueAt(row, 1);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Variants: " + productName, true);
        dialog.setSize(650, 450);
        dialog.setLayout(new BorderLayout());

        String[] varColumns = {"SKU", "Stock", "Color", "Price", "Size"};
        DefaultTableModel varModel = new DefaultTableModel(varColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable varTable = new JTable(varModel);
        
        dialog.add(new JScrollPane(varTable), BorderLayout.CENTER);

        Runnable refreshVarians = () -> {
            varModel.setRowCount(0);
            List<VarianProduk> vars = catalogModel.getVariansByProduk(idProduk);
            for (VarianProduk v : vars) {
                varModel.addRow(new Object[]{v.getSku(), v.getStock(), v.getWarna(), v.getHargaVarian(), v.getUkuran()});
            }
        };
        refreshVarians.run();

        JPanel varActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton addVarBtn = new JButton("Add Variant");
        JButton editVarBtn = new JButton("Edit Variant");
        JButton deleteVarBtn = new JButton("Delete Variant");

        addVarBtn.addActionListener(e -> {
            JTextField skuF = new JTextField();
            JTextField stockF = new JTextField("0");
            JTextField colorF = new JTextField();
            JTextField priceF = new JTextField("0");
            JTextField sizeF = new JTextField();
            
            Object[] message = {"SKU:", skuF, "Stock:", stockF, "Color:", colorF, "Price:", priceF, "Size:", sizeF};
            int option = JOptionPane.showConfirmDialog(dialog, message, "New Variant", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    VarianProduk v = new VarianProduk();
                    v.setIdProduk(idProduk);
                    v.setSku(skuF.getText());
                    v.setStock(Integer.parseInt(stockF.getText()));
                    v.setWarna(colorF.getText());
                    v.setHargaVarian(new BigDecimal(priceF.getText()));
                    v.setUkuran(sizeF.getText());
                    
                    if (catalogModel.addVarian(v)) {
                        refreshVarians.run();
                    }
                } catch (Exception ex) { GUIHelper.showError(dialog, "Input tidak valid: " + ex.getMessage()); }
            }
        });

        editVarBtn.addActionListener(e -> {
            int varRow = varTable.getSelectedRow();
            if (varRow == -1) {
                GUIHelper.showError(dialog, "Silakan pilih baris varian terlebih dahulu yang ingin diedit!");
                return;
            }
            
            String currentSku = (String) varModel.getValueAt(varRow, 0);
            int currentStock = (int) varModel.getValueAt(varRow, 1);
            String currentColor = (String) varModel.getValueAt(varRow, 2);
            BigDecimal currentPrice = (BigDecimal) varModel.getValueAt(varRow, 3);
            String currentSize = (String) varModel.getValueAt(varRow, 4);

            JTextField skuF = new JTextField(currentSku);
            skuF.setEditable(false); 
            JTextField stockF = new JTextField(String.valueOf(currentStock));
            JTextField colorF = new JTextField(currentColor);
            JTextField priceF = new JTextField(currentPrice.toString());
            JTextField sizeF = new JTextField(currentSize == null ? "" : currentSize);

            Object[] message = {"SKU (Read-Only):", skuF, "Stock:", stockF, "Color:", colorF, "Price:", priceF, "Size:", sizeF};
            int option = JOptionPane.showConfirmDialog(dialog, message, "Edit Variant", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    VarianProduk v = new VarianProduk();
                    v.setIdProduk(idProduk);
                    v.setSku(skuF.getText());
                    v.setStock(Integer.parseInt(stockF.getText()));
                    v.setWarna(colorF.getText());
                    v.setHargaVarian(new BigDecimal(priceF.getText()));
                    v.setUkuran(sizeF.getText());

                    if (catalogModel.updateVarian(v)) { 
                        GUIHelper.showInfo(dialog, "Varian berhasil diupdate!");
                        refreshVarians.run();
                    }
                } catch (Exception ex) { GUIHelper.showError(dialog, "Input tidak valid: " + ex.getMessage()); }
            }
        });

        deleteVarBtn.addActionListener(e -> {
            int varRow = varTable.getSelectedRow();
            if (varRow == -1) {
                GUIHelper.showError(dialog, "Silakan pilih baris varian terlebih dahulu yang ingin dihapus!");
                return;
            }
            String sku = (String) varModel.getValueAt(varRow, 0); 

            int confirm = JOptionPane.showConfirmDialog(dialog, 
                    "Apakah Anda yakin ingin menghapus varian dengan SKU: " + sku + "?", 
                    "Konfirmasi Hapus Varian", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (catalogModel.deleteVarian(idProduk, sku)) { 
                    GUIHelper.showInfo(dialog, "Varian berhasil dihapus!");
                    refreshVarians.run();
                } else {
                    GUIHelper.showError(dialog, "Gagal menghapus varian.");
                }
            }
        });

        varActionPanel.add(addVarBtn);
        varActionPanel.add(editVarBtn);
        varActionPanel.add(deleteVarBtn);
        dialog.add(varActionPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}