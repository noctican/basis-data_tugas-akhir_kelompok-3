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
        cartModel = new DefaultTableModel(new String[]{"Product ID", "SKU", "Qty", "Subtotal"}, 0);
        cartTable = new JTable(cartModel);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);
    }

    private void setupBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: 0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottom.add(totalLabel, BorderLayout.WEST);

        JButton checkoutBtn = new JButton("Checkout Now");
        checkoutBtn.addActionListener(e -> handleCheckout());
        bottom.add(checkoutBtn, BorderLayout.EAST);
        
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

    private void handleCheckout() {
        if (cartModel.getRowCount() == 0) { GUIHelper.showError(this, "Cart is empty!"); return; }

        String[] methods = {"Credit Card", "Bank Transfer", "E-Wallet", "COD"};
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
