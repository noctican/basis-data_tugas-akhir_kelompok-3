package models;

import config.Database;
import entities.DetailKeranjang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailKeranjangModel {
    public List<DetailKeranjang> getAll() throws SQLException {
        List<DetailKeranjang> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_keranjang";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DetailKeranjang(
                    rs.getInt("id_keranjang"),
                    rs.getInt("id_produk"),
                    rs.getString("sku"),
                    rs.getInt("kuantitas"),
                    rs.getBigDecimal("sub_total")
                ));
            }
        }
        return list;
    }

    public void insert(DetailKeranjang dk) throws SQLException {
        String sql = "INSERT INTO detail_keranjang (id_keranjang, id_produk, sku, kuantitas, sub_total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dk.getIdKeranjang());
            pstmt.setInt(2, dk.getIdProduk());
            pstmt.setString(3, dk.getSku());
            pstmt.setInt(4, dk.getKuantitas());
            pstmt.setBigDecimal(5, dk.getSubTotal());
            pstmt.executeUpdate();
        }
    }

    public void update(DetailKeranjang dk) throws SQLException {
        String sql = "UPDATE detail_keranjang SET kuantitas = ?, sub_total = ? WHERE id_keranjang = ? AND id_produk = ? AND sku = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dk.getKuantitas());
            pstmt.setBigDecimal(2, dk.getSubTotal());
            pstmt.setInt(3, dk.getIdKeranjang());
            pstmt.setInt(4, dk.getIdProduk());
            pstmt.setString(5, dk.getSku());
            pstmt.executeUpdate();
        }
    }

    public void delete(int idKeranjang, int idProduk, String sku) throws SQLException {
        String sql = "DELETE FROM detail_keranjang WHERE id_keranjang = ? AND id_produk = ? AND sku = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idKeranjang);
            pstmt.setInt(2, idProduk);
            pstmt.setString(3, sku);
            pstmt.executeUpdate();
        }
    }
}
