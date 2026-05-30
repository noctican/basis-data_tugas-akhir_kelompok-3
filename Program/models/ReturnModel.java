package models;

import config.DatabaseConfig;
import helpers.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnModel {
    public List<Object[]> getReturnsByStatus(String status) {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT r.id_retur, r.id_transaksi, dr.id_produk, dr.sku, dr.kuantitas, dr.alasan " +
                    "FROM retur r " +
                    "JOIN detail_retur dr ON r.id_retur = dr.id_retur " +
                    "WHERE r.status = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            
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



    public List<Object[]> getReturnsByCustomer(int idPengguna) {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT r.id_retur, r.id_transaksi, r.tanggal_pengembalian, r.status, " +
                       "dr.id_produk, dr.sku, dr.kuantitas, dr.alasan " +
                       "FROM retur r " +
                       "JOIN detail_retur dr ON r.id_retur = dr.id_retur " +
                       "JOIN pelanggan_transaksi pt ON r.id_transaksi = pt.id_transaksi " +
                       "WHERE pt.id_pengguna = ? " +
                       "ORDER BY r.id_retur DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idPengguna);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_retur"),
                    rs.getInt("id_transaksi"),
                    rs.getDate("tanggal_pengembalian"),
                    rs.getString("status"),
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

    public String requestReturn(int idPengguna, int idTransaksi, int idProduk, String sku, int qty, String alasan) {
        String sql = "{CALL sp_AjukanRetur(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idPengguna);
            cstmt.setInt(2, idTransaksi);
            cstmt.setInt(3, idProduk);
            cstmt.setString(4, sku);
            cstmt.setInt(5, qty);
            cstmt.setString(6, alasan);
            cstmt.registerOutParameter(7, java.sql.Types.NVARCHAR);
            
            cstmt.execute();
            return cstmt.getString(7);
            
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    public boolean approveReturn(int idRetur, int idValidator) {
        String sql = "{CALL sp_ProsesRetur(?, ?, ?, ?)}";
        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idRetur);
            cstmt.setBoolean(2, true);
            cstmt.setInt(3, idValidator);
            cstmt.registerOutParameter(4, java.sql.Types.NVARCHAR);
            
            cstmt.execute();
            String msg = cstmt.getString(4);
            return msg != null && msg.startsWith("SUCCESS");
            
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

            String sqlDetail = "DELETE FROM detail_retur WHERE id_retur = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDetail)) {
                pstmt.setInt(1, idRetur);
                pstmt.executeUpdate();
            }

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
    
    public boolean rejectReturn(int idRetur, int idValidator) {
        String sql = "{CALL sp_ProsesRetur(?, ?, ?, ?)}";
        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idRetur);
            cstmt.setBoolean(2, false);
            cstmt.setInt(3, idValidator);
            cstmt.registerOutParameter(4, java.sql.Types.NVARCHAR);
            
            cstmt.execute();
            String msg = cstmt.getString(4);
            return msg != null && msg.startsWith("SUCCESS");
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}