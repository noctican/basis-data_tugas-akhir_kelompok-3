package ui;

import entities.*;
import helpers.GUIHelper;
import models.TransactionModel;
import session.UserSession;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CartPanel extends JPanel {
    private TransactionModel transactionModel;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel totalLabel;
    private Keranjang currentCart;

    public CartPanel() {
        transactionModel = new TransactionModel();
        setLayout(new BorderLayout());

        setupTable();
        setupBottomPanel();
        
        refreshCart();
    }

    private void setupTable() {
        cartModel = new DefaultTableModel(new String[]{"Product ID", "SKU", "Qty", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartModel);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: 0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottom.add(totalLabel, BorderLayout.WEST);

        // Panel khusus untuk menampung tombol aksi di sebelah kanan
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Tombol Edit Kuantitas
        JButton editBtn = new JButton("Edit Qty");
        editBtn.addActionListener(e -> handleEditQuantity());
        actionPanel.add(editBtn);

        // Tombol Hapus Item
        JButton deleteBtn = new JButton("Delete Item");
        deleteBtn.addActionListener(e -> handleDeleteItem());
        actionPanel.add(deleteBtn);

        // Tombol Checkout
        JButton checkoutBtn = new JButton("Checkout Now");
        checkoutBtn.addActionListener(e -> handleCheckout());
        actionPanel.add(checkoutBtn);
        
        bottom.add(actionPanel, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    public void refreshCart() {
        cartModel.setRowCount(0);
        currentCart = transactionModel.getOrCreateCart(UserSession.getCurrentUser().getIdPengguna());
        List<DetailKeranjang> items = transactionModel.getCartDetails(currentCart.getIdKeranjang());
        BigDecimal total = BigDecimal.ZERO;
        for (DetailKeranjang d : items) {
            cartModel.addRow(new Object[]{d.getIdProduk(), d.getSku(), d.getKuantitas(), d.getSubTotal()});
            total = total.add(d.getSubTotal());
        }
        totalLabel.setText("Total: " + total.toString());
    }

    private void handleEditQuantity() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIHelper.showError(this, "Please select an item to edit!");
            return;
        }

        // Mengambil data ID Produk dan Kuantitas saat ini dari baris yang dipilih
        int idProduk = (int) cartModel.getValueAt(selectedRow, 0);
        String sku = (String) cartModel.getValueAt(selectedRow, 1);
        int currentQty = (int) cartModel.getValueAt(selectedRow, 2);

        // Menampilkan dialog input untuk kuantitas baru
        String input = JOptionPane.showInputDialog(this, "Enter new quantity:", currentQty);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int newQty = Integer.parseInt(input);
                if (newQty <= 0) {
                    GUIHelper.showError(this, "Quantity must be greater than 0!");
                    return;
                }

                // Panggil method update di TransactionModel Anda (sesuaikan namanya jika berbeda)
                if (transactionModel.updateCartQuantity(currentCart.getIdKeranjang(), idProduk, sku, newQty)) {
                    GUIHelper.showInfo(this, "Quantity updated successfully!");
                    refreshCart();
                } else {
                    GUIHelper.showError(this, "Failed to update quantity.");
                }
            } catch (NumberFormatException ex) {
                GUIHelper.showError(this, "Input must be a number!");
            }
        }
    }

    private void handleDeleteItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIHelper.showError(this, "Please select an item to delete!");
            return;
        }

        int idProduk = (int) cartModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this item from the cart?", 
                "Delete Confirmation", 
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sku = (String) cartModel.getValueAt(selectedRow, 1);
            // Panggil method delete di TransactionModel Anda (sesuaikan namanya jika berbeda)
            if (transactionModel.deleteCartItem(currentCart.getIdKeranjang(), idProduk, sku)) {
                GUIHelper.showInfo(this, "Item successfully deleted.");
                refreshCart();
            } else {
                GUIHelper.showError(this, "Failed to delete item.");
            }
        }
    }

    private void handleCheckout() {
        if (cartModel.getRowCount() == 0) { GUIHelper.showError(this, "Cart is empty!"); return; }

        String[] methods = {"Wallet"};
        String selectedMethod = (String) JOptionPane.showInputDialog(this, "Select Payment Method:", "Checkout", JOptionPane.QUESTION_MESSAGE, null, methods, methods[0]);
        
        if (selectedMethod != null) {
            if (transactionModel.checkout(UserSession.getCurrentUser().getIdPengguna(), currentCart.getIdKeranjang(), selectedMethod)) {
                GUIHelper.showInfo(this, "Checkout successful! Your order is being processed.");
                refreshCart();
            } else {
                GUIHelper.showError(this, "Checkout failed.");
            }
        }
    }
}