package ui;

import entities.*;
import helpers.GUIHelper;
import models.CatalogModel;
import models.TransactionModel;
import session.UserSession;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ShopPanel extends JPanel {
    private CatalogModel catalogModel;
    private TransactionModel transactionModel;
    private JTable productTable;
    private DefaultTableModel productModel;
    private JTextField searchField;

    public ShopPanel() {
        catalogModel = new CatalogModel();
        transactionModel = new TransactionModel();
        setLayout(new BorderLayout());

        setupTopPanel();
        setupTable();
        setupBottomPanel();
        
        refreshProducts();
    }

    private void setupTopPanel() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.add(new JLabel("Search Products:"));
        searchField = new JTextField(20);
        top.add(searchField);
        JButton btn = new JButton("Search");
        btn.addActionListener(e -> refreshProducts());
        top.add(btn);
        add(top, BorderLayout.NORTH);
    }

    private void setupTable() {
        productModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(productModel);
        add(new JScrollPane(productTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JButton addBtn = new JButton("Add Selected to Cart");
        addBtn.addActionListener(e -> handleAddToCart());
        add(addBtn, BorderLayout.SOUTH);
    }

    private void refreshProducts() {
        productModel.setRowCount(0);
        List<Produk> list = catalogModel.searchProduk(searchField.getText());
        for (Produk p : list) productModel.addRow(new Object[]{p.getIdProduk(), p.getNamaProduk(), p.getHargaBase(), p.getDeskripsiProduk()});
    }

    private void handleAddToCart() {
        int row = productTable.getSelectedRow();
        if (row == -1) { GUIHelper.showError(this, "Select a product!"); return; }

        int idProduk = (int) productModel.getValueAt(row, 0);
        List<VarianProduk> vars = catalogModel.getVariansByProduk(idProduk);
        if (vars.isEmpty()) { GUIHelper.showError(this, "No variants available!"); return; }

        VarianProduk selected = (VarianProduk) JOptionPane.showInputDialog(this, "Select Variant:", "Cart", JOptionPane.QUESTION_MESSAGE, null, vars.toArray(), vars.get(0));
        if (selected != null) {
            String qtyS = JOptionPane.showInputDialog(this, "Quantity:", "1");
            if (qtyS != null) {
                try {
                    int qty = Integer.parseInt(qtyS);
                    if (qty > selected.getStock()) { GUIHelper.showError(this, "Not enough stock!"); return; }
                    
                    Keranjang k = transactionModel.getOrCreateCart(UserSession.getCurrentUser().getIdPengguna());
                    DetailKeranjang d = new DetailKeranjang();
                    d.setIdKeranjang(k.getIdKeranjang());
                    d.setIdProduk(idProduk);
                    d.setSku(selected.getSku());
                    d.setKuantitas(qty);
                    d.setSubTotal(selected.getHargaVarian().multiply(new BigDecimal(qty)));
                    
                    if (transactionModel.addToCart(d)) GUIHelper.showInfo(this, "Added to cart!");
                } catch (Exception ex) { GUIHelper.showError(this, "Invalid quantity."); }
            }
        }
    }
}


