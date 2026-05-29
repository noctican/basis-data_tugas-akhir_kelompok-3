package models;

import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductAnalysisModel {

    // Memanggil procedure pertama (butuh parameter ID Produk Target)
    public List<Object[]> getTop3ProductsBoughtTogether(int productId) {
        List<Object[]> resultList = new ArrayList<>();
        String sql = "{call sp_Ambil_Top3_Produk_Dibeli_Bersama(?)}";

        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String namaProduk = rs.getString("nama_produk");
                    int jumlah = rs.getInt("jumlah_dibeli_bersama");
                    resultList.add(new Object[]{namaProduk, jumlah});
                }
            }
        } catch (SQLException e) {
            System.err.println("Error running sp_Ambil_Top3_Produk_Dibeli_Bersama: " + e.getMessage());
        }
        return resultList;
    }

    // Memanggil procedure kedua (tanpa parameter)
    public List<Object[]> getTop3GlobalTogether() {
        List<Object[]> resultList = new ArrayList<>();
        String sql = "{call sp_Ambil_Top3_Produk_Paling_Sering_Dibeli_Bersamaan}";

        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement stmt = conn.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String namaProduk = rs.getString("nama_produk");
                int jumlah = rs.getInt("jumlah_muncul");
                resultList.add(new Object[]{namaProduk, jumlah});
            }
        } catch (SQLException e) {
            System.err.println("Error running sp_Ambil_Top3_Produk_Paling_Sering_Dibeli_Bersamaan: " + e.getMessage());
        }
        return resultList;
    }

    public List<String> getRecommendationsForCustomer(int productId) {
        List<String> recommendations = new ArrayList<>();
        String sql = "{call sp_Ambil_Top3_Produk_Dibeli_Bersama(?)}";

        try (Connection conn = config.DatabaseConfig.getConnection(); // Sesuaikan dengan config kamu
            java.sql.CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, productId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recommendations.add(rs.getString("nama_produk"));
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error loading recommendations: " + e.getMessage());
        }
        return recommendations;
    }
}