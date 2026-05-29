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

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton editBtn = new JButton("Edit Qty");
        editBtn.addActionListener(e -> handleEditQuantity());
        actionPanel.add(editBtn);

        JButton deleteBtn = new JButton("Delete Item");
        deleteBtn.addActionListener(e -> handleDeleteItem());
        actionPanel.add(deleteBtn);

        JButton checkoutBtn = new JButton("Checkout Now");
        checkoutBtn.addActionListener(e -> handleCheckout());
        actionPanel.add(checkoutBtn);
        
        bottom.add(actionPanel, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    public void refreshCart() {
        // Otomatis bersihkan transaksi expired (>10 menit) di DB sebelum merender ulang halaman belanja
        transactionModel.cekExpiredPembayaran();

        cartModel.setRowCount(0);
        currentCart = transactionModel.getOrCreateCart(UserSession.getCurrentUser().getIdPengguna());
        List<DetailKeranjang> items = transactionModel.getCartDetails(currentCart.getIdKeranjang());
        BigDecimal total = BigDecimal.ZERO;
        for (DetailKeranjang d : items) {
            cartModel.addRow(new Object[]{d.getIdProduk(), d.getSku(), d.getKuantitas(), d.getSubTotal()});
            total = total.add(d.getSubTotal());
        }
        totalLabel.setText("Total: Rp " + total.toString());
    }

    private void handleCheckout() {
        if (cartModel.getRowCount() == 0) { 
            GUIHelper.showError(this, "Cart is empty!"); 
            return; 
        }

        // 1. Pilih metode pembayaran (Sesuai parameter sp_Checkout_Reservasi_HapusKeranjang)
        String[] methods = {"Wallet"};
        String selectedMethod = (String) JOptionPane.showInputDialog(
                this, "Select Payment Method:", "Checkout Confirmation", 
                JOptionPane.QUESTION_MESSAGE, null, methods, methods[0]
        );
        
        if (selectedMethod == null) return; // User cancel dialog pemilihan metode

        int currentUserId = UserSession.getCurrentUser().getIdPengguna();
        StringBuilder errorBuffer = new StringBuilder();

        // 2. Eksekusi Stored Procedure Tahap 1: Membuat Transaksi PENDING & Mengosongkan Keranjang
        int newTransactionId = transactionModel.checkoutReservasi(currentUserId, selectedMethod, errorBuffer);

        if (newTransactionId > 0) {
            // Berhasil reservasi stok dan membuat ID transaksi baru
            JOptionPane.showMessageDialog(this, 
                    "Checkout Sukses!\nID Transaksi Anda: #" + newTransactionId + 
                    "\nStatus: PENDING\n\nCatatan: Selesaikan pembayaran dalam waktu 10 menit atau pesanan otomatis dibatalkan.", 
                    "Informasi Transaksi", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh tabel keranjang (sekarang harusnya sudah kosong karena dihapus oleh SP)
            refreshCart();

            // 3. Eksekusi Stored Procedure Tahap 2: Menawarkan pembayaran instan menggunakan Wallet
            int payConfirm = JOptionPane.showConfirmDialog(this, 
                    "Apakah Anda ingin langsung membayar Transaksi #" + newTransactionId + " menggunakan Saldo Wallet?", 
                    "Bayar Sekarang", JOptionPane.YES_NO_OPTION);

            if (payConfirm == JOptionPane.YES_OPTION) {
                // Menjalankan sp_BayarTransaksi
                String hasilPembayaran = transactionModel.bayarTransaksi(currentUserId, newTransactionId);
                
                if (hasilPembayaran.startsWith("SUKSES")) {
                    JOptionPane.showMessageDialog(this, hasilPembayaran, "Pembayaran Berhasil", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Menampilkan pesan error spesifik dari kembalian RAISERROR / OUTPUT SP (Misal: Saldo kurang, dsb)
                    JOptionPane.showMessageDialog(this, hasilPembayaran, "Pembayaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // Gagal karena validasi internal SP (misal: stok mendadak tidak cukup)
            GUIHelper.showError(this, "Checkout Gagal: " + errorBuffer.toString());
        }
    }

    private void handleEditQuantity() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIHelper.showError(this, "Please select an item to edit!");
            return;
        }

        int idProduk = (int) cartModel.getValueAt(selectedRow, 0);
        String sku = (String) cartModel.getValueAt(selectedRow, 1);
        int currentQty = (int) cartModel.getValueAt(selectedRow, 2);

        String input = JOptionPane.showInputDialog(this, "Enter new quantity:", currentQty);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int newQty = Integer.parseInt(input);
                if (newQty <= 0) {
                    GUIHelper.showError(this, "Quantity must be greater than 0!");
                    return;
                }
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
                "Delete Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sku = (String) cartModel.getValueAt(selectedRow, 1);
            if (transactionModel.deleteCartItem(currentCart.getIdKeranjang(), idProduk, sku)) {
                GUIHelper.showInfo(this, "Item successfully deleted.");
                refreshCart();
            } else {
                GUIHelper.showError(this, "Failed to delete item.");
            }
        }
    }
}