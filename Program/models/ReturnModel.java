package models;

import config.DatabaseConfig;
import helpers.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnModel {
    public List<Object[]> getAllPendingReturns() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT r.id_retur, r.id_transaksi, dr.id_produk, dr.sku, dr.kuantitas, dr.alasan " +
                     "FROM retur r JOIN detail_retur dr ON r.id_retur = dr.id_retur " +
                     "WHERE r.status = 'PENDING'";
                     
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_retur"), 
                    rs.getInt("id_transaksi"), 
                    rs.getInt("id_produk"), 
                    rs.getString("sku"), 
                    rs.getInt("kuantitas"), 
                    rs.getString("alasan")
                });
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    public boolean approveReturn(int idRetur) {
        String sql = "UPDATE retur SET status = 'APPROVED' WHERE id_retur = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRetur);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReturn(int idRetur) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Hapus detail_retur (Anak tabel)
            String sqlDetail = "DELETE FROM detail_retur WHERE id_retur = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDetail)) {
                pstmt.setInt(1, idRetur);
                pstmt.executeUpdate();
            }

            // 2. Hapus data utama di tabel retur (Induk tabel)
            String sqlMain = "DELETE FROM retur WHERE id_retur = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlMain)) {
                pstmt.setInt(1, idRetur);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            DBHelper.rollback(conn);
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) {}
                try { conn.close(); } catch (SQLException ex) {}
            }
        }
    }
}