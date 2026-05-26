package models;

import config.DatabaseConfig;
import entities.*;
import helpers.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnModel {
    public boolean createReturn(int idTransaksi, int idProduk, String sku, int qty, String alasan) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Retur
            int idRetur = -1;
            String sqlR = "INSERT INTO retur (id_transaksi, status) VALUES (?, 'PENDING')";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlR, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, idTransaksi);
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) idRetur = rs.getInt(1);
                }
            }

            // 2. Insert Detail_retur
            String sqlDR = "INSERT INTO detail_retur (id_transaksi, id_retur, id_produk, sku, kuantitas, alasan) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDR)) {
                pstmt.setInt(1, idTransaksi);
                pstmt.setInt(2, idRetur);
                pstmt.setInt(3, idProduk);
                pstmt.setString(4, sku);
                pstmt.setInt(5, qty);
                pstmt.setString(6, alasan);
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

    public List<Object[]> getAllPendingReturns() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT r.id_retur, r.id_transaksi, dr.id_produk, dr.sku, dr.kuantitas, dr.alasan " +
                     "FROM retur r JOIN detail_retur dr ON r.id_retur = dr.id_retur " +
                     "WHERE r.status = 'PENDING'";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("id_retur"), rs.getInt("id_transaksi"), rs.getInt("id_produk"), rs.getString("sku"), rs.getInt("kuantitas"), rs.getString("alasan")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
