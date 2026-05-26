package models;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportModel {
    
    // a. 5 produk penjualan tertinggi
    public List<Object[]> getTop5SellingProducts() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT TOP 5 p.nama_produk, SUM(dt.kuantitas) as total_qty " +
                     "FROM produk p " +
                     "JOIN detail_transaksi dt ON p.id_produk = dt.id_produk " +
                     "GROUP BY p.nama_produk " +
                     "ORDER BY total_qty DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("nama_produk"), rs.getInt("total_qty")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // b. 5 pelanggan dengan total pembelian tertinggi
    public List<Object[]> getTop5SpendingCustomers() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT TOP 5 pg.nama_depan + ' ' + ISNULL(pg.nama_belakang, '') as full_name, SUM(t.total_pembelian) as total_spend " +
                     "FROM pengguna pg " +
                     "JOIN pelanggan_transaksi pt ON pg.id_pengguna = pt.id_pengguna " +
                     "JOIN transaksi t ON pt.id_transaksi = t.id_transaksi " +
                     "GROUP BY pg.nama_depan, pg.nama_belakang " +
                     "ORDER BY total_spend DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("full_name"), rs.getBigDecimal("total_spend")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // c. 3 barang paling banyak dibeli berbarengan dengan produk tertentu
    public List<Object[]> getFrequentlyBoughtTogether(int idProduk) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT TOP 3 p.nama_produk, COUNT(*) as frequency " +
                     "FROM detail_transaksi dt1 " +
                     "JOIN detail_transaksi dt2 ON dt1.id_transaksi = dt2.id_transaksi " +
                     "JOIN produk p ON dt2.id_produk = p.id_produk " +
                     "WHERE dt1.id_produk = ? AND dt2.id_produk <> ? " +
                     "GROUP BY p.nama_produk " +
                     "ORDER BY frequency DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduk);
            pstmt.setInt(2, idProduk);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("nama_produk"), rs.getInt("frequency")});
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
