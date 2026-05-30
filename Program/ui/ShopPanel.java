package ui;

import entities.*;
import helpers.GUIHelper;
import helpers.NumberHelper;
import models.CatalogModel;
import models.TransactionModel;
import models.ProductAnalysisModel;
import session.UserSession;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ShopPanel extends JPanel {
    private CatalogModel catalogModel;
    private TransactionModel transactionModel;
    private ProductAnalysisModel analysisModel;
    private JTable productTable;
    private DefaultTableModel productModel;
    private JTextField searchField;
    private JLabel recommendationLabel;

    public ShopPanel() {
        catalogModel = new CatalogModel();
        transactionModel = new TransactionModel();
        analysisModel = new ProductAnalysisModel();
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
        productTable.getTableHeader().setReorderingAllowed(false);

        NumberHelper.setRupiah(productTable, 2);

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateRecommendations();
            }
        });

        add(new JScrollPane(productTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JPanel containerBottom = new JPanel();
        containerBottom.setLayout(new BoxLayout(containerBottom, BoxLayout.Y_AXIS));

        JPanel recPanel = new JPanel(new BorderLayout());
        recPanel.setBorder(BorderFactory.createTitledBorder("Frequently Bought Together"));
        
        recommendationLabel = new JLabel("<html><i style='color:gray;'>Select a product to see recommendation bundles...</i></html>");
        recommendationLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        recPanel.add(recommendationLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton addBtn = new JButton("Add Selected to Cart");
        addBtn.addActionListener(e -> handleAddToCart());
        buttonPanel.add(addBtn);

        containerBottom.add(recPanel);
        containerBottom.add(buttonPanel);

        add(containerBottom, BorderLayout.SOUTH);
    }

    private void updateRecommendations() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            recommendationLabel.setText("<html><i style='color:gray;'>Select a product to see recommendation bundles...</i></html>");
            return;
        }

        try {
            int idProduk = (int) productModel.getValueAt(row, 0);
            
            List<Object[]> recs = analysisModel.getTop3ProductsBoughtTogether(idProduk);

            if (recs.isEmpty()) {
                recommendationLabel.setText("<html><span style='color:orange; font-weight:bold;'>💡 Tip:</span> <i>Be the first to pair this product with another item!</i></html>");
            } else {
                StringBuilder html = new StringBuilder("<html><span style='color:navy; font-weight:bold;'>Customers who bought this also bought:</span><ul style='margin-top: 3px;'>");
                for (Object[] rec : recs) {
                    String namaProdukPendamping = (String) rec[0];
                    html.append("<li style='font-weight:bold; color:#333;'>").append(namaProdukPendamping).append("</li>");
                }
                html.append("</ul></html>");
                recommendationLabel.setText(html.toString());
            }
        } catch (Exception ex) {
            recommendationLabel.setText("<html><i style='color:red;'>Failed to load recommendations.</i></html>");
        }
    }

    private void refreshProducts() {
        productModel.setRowCount(0);
        List<Produk> list = catalogModel.searchProduk(searchField.getText());
        for (Produk p : list) {
            productModel.addRow(new Object[]{
                p.getIdProduk(), 
                p.getNamaProduk(), 
                p.getHargaBase(),
                p.getDeskripsiProduk()
            });
        }

        if (recommendationLabel != null) {
            recommendationLabel.setText("<html><i style='color:gray;'>Select a product to see recommendation bundles...</i></html>");
        }
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