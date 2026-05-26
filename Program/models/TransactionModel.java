package models;

import config.DatabaseConfig;
import entities.*;
import helpers.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class TransactionModel {
    
    public Keranjang getOrCreateCart(int idPengguna) {
        String select = "SELECT * FROM keranjang WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(select)) {
            pstmt.setInt(1, idPengguna);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Keranjang k = new Keranjang();
                    k.setIdKeranjang(rs.getInt("id_keranjang"));
                    k.setIdPengguna(rs.getInt("id_pengguna"));
                    return k;
                }
            }
            
            String insert = "INSERT INTO keranjang (id_pengguna) VALUES (?)";
            try (PreparedStatement istmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                istmt.setInt(1, idPengguna);
                istmt.executeUpdate();
                try (ResultSet rs = istmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Keranjang k = new Keranjang();
                        k.setIdKeranjang(rs.getInt(1));
                        k.setIdPengguna(idPengguna);
                        return k;
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<DetailKeranjang> getCartDetails(int idKeranjang) {
        List<DetailKeranjang> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_keranjang WHERE id_keranjang = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idKeranjang);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetailKeranjang d = new DetailKeranjang();
                    d.setIdKeranjang(rs.getInt("id_keranjang"));
                    d.setIdProduk(rs.getInt("id_produk"));
                    d.setSku(rs.getString("sku"));
                    d.setKuantitas(rs.getInt("kuantitas"));
                    d.setSubTotal(rs.getBigDecimal("sub_total"));
                    list.add(d);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addToCart(DetailKeranjang d) {
        // MERGE like logic
        String check = "SELECT kuantitas, sub_total FROM detail_keranjang WHERE id_keranjang = ? AND id_produk = ? AND sku = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            try (PreparedStatement cstmt = conn.prepareStatement(check)) {
                cstmt.setInt(1, d.getIdKeranjang());
                cstmt.setInt(2, d.getIdProduk());
                cstmt.setString(3, d.getSku());
                try (ResultSet rs = cstmt.executeQuery()) {
                    if (rs.next()) {
                        String update = "UPDATE detail_keranjang SET kuantitas = kuantitas + ?, sub_total = sub_total + ? WHERE id_keranjang = ? AND id_produk = ? AND sku = ?";
                        try (PreparedStatement ustmt = conn.prepareStatement(update)) {
                            ustmt.setInt(1, d.getKuantitas());
                            ustmt.setBigDecimal(2, d.getSubTotal());
                            ustmt.setInt(3, d.getIdKeranjang());
                            ustmt.setInt(4, d.getIdProduk());
                            ustmt.setString(5, d.getSku());
                            return ustmt.executeUpdate() > 0;
                        }
                    } else {
                        String insert = "INSERT INTO detail_keranjang (id_keranjang, id_produk, sku, kuantitas, sub_total) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement istmt = conn.prepareStatement(insert)) {
                            istmt.setInt(1, d.getIdKeranjang());
                            istmt.setInt(2, d.getIdProduk());
                            istmt.setString(3, d.getSku());
                            istmt.setInt(4, d.getKuantitas());
                            istmt.setBigDecimal(5, d.getSubTotal());
                            return istmt.executeUpdate() > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean checkout(int idPengguna, int idKeranjang, String metodeBayar) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            List<DetailKeranjang> items = getCartDetails(idKeranjang);
            if (items.isEmpty()) return false;

            BigDecimal total = BigDecimal.ZERO;
            for (DetailKeranjang item : items) total = total.add(item.getSubTotal());

            // 1. Transaksi
            int idTransaksi = -1;
            String sqlT = "INSERT INTO transaksi (total_pembelian, metode_pembayaran) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlT, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setBigDecimal(1, total);
                pstmt.setString(2, metodeBayar);
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) idTransaksi = rs.getInt(1);
                }
            }

            // 2. Pelanggan_transaksi
            String sqlPT = "INSERT INTO pelanggan_transaksi (id_pengguna, id_transaksi) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPT)) {
                pstmt.setInt(1, idPengguna);
                pstmt.setInt(2, idTransaksi);
                pstmt.executeUpdate();
            }

            // 3. Detail_transaksi & Update stock
            String sqlDT = "INSERT INTO detail_transaksi (id_transaksi, id_produk, sku, harga_pembelian, kuantitas) VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE varian_produk SET stock = stock - ? WHERE id_produk = ? AND sku = ?";
            try (PreparedStatement pstmtDT = conn.prepareStatement(sqlDT);
                 PreparedStatement pstmtS = conn.prepareStatement(sqlStock)) {
                for (DetailKeranjang item : items) {
                    pstmtDT.setInt(1, idTransaksi);
                    pstmtDT.setInt(2, item.getIdProduk());
                    pstmtDT.setString(3, item.getSku());
                    pstmtDT.setBigDecimal(4, item.getSubTotal().divide(new BigDecimal(item.getKuantitas()), 4, BigDecimal.ROUND_HALF_UP));
                    pstmtDT.setInt(5, item.getKuantitas());
                    pstmtDT.executeUpdate();

                    pstmtS.setInt(1, item.getKuantitas());
                    pstmtS.setInt(2, item.getIdProduk());
                    pstmtS.setString(3, item.getSku());
                    pstmtS.executeUpdate();
                }
            }

            // 4. Clear Cart
            String sqlClear = "DELETE FROM detail_keranjang WHERE id_keranjang = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlClear)) {
                pstmt.setInt(1, idKeranjang);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            DBHelper.rollback(conn);
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }
}
