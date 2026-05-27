package models;

import config.DatabaseConfig;
import entities.*;
import helpers.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderModel {
    public List<Transaksi> getAllOrders() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi ORDER BY tanggal_transaksi DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Transaksi t = new Transaksi();
                t.setIdTransaksi(rs.getInt("id_transaksi"));
                t.setTanggalTransaksi(rs.getTimestamp("tanggal_transaksi"));
                t.setTotalPembelian(rs.getBigDecimal("total_pembelian"));
                t.setStatusPembayaran(rs.getString("status_pembayaran"));
                t.setMetodePembayaran(rs.getString("metode_pembayaran"));
                list.add(t);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    public boolean shipOrder(int idTransaksi, String resi) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            String sqlP = "INSERT INTO pengiriman (no_resi, id_transaksi, nama_ekspedisi, status_pengiriman) VALUES (?, ?, ?, 'SHIPPED')";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlP)) {
                pstmt.setString(1, resi);
                pstmt.setInt(2, idTransaksi);
                pstmt.setString(3, "-");
                pstmt.executeUpdate();
            }

            String sqlT = "UPDATE transaksi SET status_pembayaran = 'SHIPPED' WHERE id_transaksi = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlT)) {
                pstmt.setInt(1, idTransaksi);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            DBHelper.rollback(conn);
            e.printStackTrace();
            return false;
        } finally {
            try { 
                if (conn != null) conn.setAutoCommit(true); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}