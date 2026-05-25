package models;

import config.Database;
import entities.VarianProduk;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VarianProdukModel {
    public List<VarianProduk> getAll() throws SQLException {
        List<VarianProduk> list = new ArrayList<>();
        String sql = "SELECT vp.*, p.nama_produk FROM varian_produk vp JOIN produk p ON vp.id_produk = p.id_produk";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                VarianProduk vp = new VarianProduk(
                    rs.getInt("id_produk"),
                    rs.getString("sku"),
                    rs.getInt("stock"),
                    rs.getString("warna"),
                    rs.getBigDecimal("harga_varian"),
                    rs.getString("ukuran")
                );
                vp.setNamaProduk(rs.getString("nama_produk"));
                list.add(vp);
            }
        }
        return list;
    }

    public void insert(VarianProduk vp) throws SQLException {
        String sql = "INSERT INTO varian_produk (id_produk, sku, stock, warna, harga_varian, ukuran) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vp.getIdProduk());
            pstmt.setString(2, vp.getSku());
            pstmt.setInt(3, vp.getStock());
            pstmt.setString(4, vp.getWarna());
            pstmt.setBigDecimal(5, vp.getHargaVarian());
            pstmt.setString(6, vp.getUkuran());
            pstmt.executeUpdate();
        }
    }

    public void update(VarianProduk vp) throws SQLException {
        String sql = "UPDATE varian_produk SET stock = ?, warna = ?, harga_varian = ?, ukuran = ? WHERE id_produk = ? AND sku = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vp.getStock());
            pstmt.setString(2, vp.getWarna());
            pstmt.setBigDecimal(3, vp.getHargaVarian());
            pstmt.setString(4, vp.getUkuran());
            pstmt.setInt(5, vp.getIdProduk());
            pstmt.setString(6, vp.getSku());
            pstmt.executeUpdate();
        }
    }

    public void delete(int idProduk, String sku) throws SQLException {
        String sql = "DELETE FROM varian_produk WHERE id_produk = ? AND sku = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduk);
            pstmt.setString(2, sku);
            pstmt.executeUpdate();
        }
    }
}
