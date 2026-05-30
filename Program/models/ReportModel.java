package models;

import config.DatabaseConfig;
import helpers.NumberHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportModel {

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
                list.add(new Object[] { rs.getString("nama_produk"), rs.getInt("total_qty") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getTop5SpendingCustomers() {
        List<Object[]> list = new ArrayList<>();
        String query = "EXEC sp_get_top_5_spending_customers ?";
        
        try (Connection conn = DatabaseConfig.getConnection(); 
            PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setNull(1, java.sql.Types.DATE); 
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String namaPelanggan = rs.getString("full_name");
                    double totalBelanja = rs.getDouble("total_spend");
                    
                    String hargaFormatRp = NumberHelper.formatNumber(totalBelanja, true, true);
                    list.add(new Object[]{
                        namaPelanggan,
                        hargaFormatRp
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

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
                    list.add(new Object[] { rs.getString("nama_produk"), rs.getInt("frequency") });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
