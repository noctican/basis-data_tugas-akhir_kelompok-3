package models;

import config.Database;
import entities.Produk;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdukModel {
    public List<Produk> getAll() throws SQLException {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT p.*, sk.nama_sub_kategori FROM produk p JOIN sub_kategori sk ON p.id_kategori = sk.id_kategori AND p.id_sub_kategori = sk.id_sub_kategori";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produk p = new Produk(
                    rs.getInt("id_produk"),
                    rs.getInt("id_kategori"),
                    rs.getInt("id_sub_kategori"),
                    rs.getString("nama_produk"),
                    rs.getString("deskripsi_produk"),
                    rs.getBigDecimal("harga_base"),
                    rs.getString("demografi"),
                    rs.getString("aktivitas")
                );
                p.setNamaSubKategori(rs.getString("nama_sub_kategori"));
                list.add(p);
            }
        }
        return list;
    }

    public void insert(Produk p) throws SQLException {
        String sql = "INSERT INTO produk (id_kategori, id_sub_kategori, nama_produk, deskripsi_produk, harga_base, demografi, aktivitas) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getIdKategori());
            pstmt.setInt(2, p.getIdSubKategori());
            pstmt.setString(3, p.getNamaProduk());
            pstmt.setString(4, p.getDeskripsiProduk());
            pstmt.setBigDecimal(5, p.getHargaBase());
            pstmt.setString(6, p.getDemografi());
            pstmt.setString(7, p.getAktivitas());
            pstmt.executeUpdate();
        }
    }

    public void update(Produk p) throws SQLException {
        String sql = "UPDATE produk SET id_kategori = ?, id_sub_kategori = ?, nama_produk = ?, deskripsi_produk = ?, harga_base = ?, demografi = ?, aktivitas = ? WHERE id_produk = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getIdKategori());
            pstmt.setInt(2, p.getIdSubKategori());
            pstmt.setString(3, p.getNamaProduk());
            pstmt.setString(4, p.getDeskripsiProduk());
            pstmt.setBigDecimal(5, p.getHargaBase());
            pstmt.setString(6, p.getDemografi());
            pstmt.setString(7, p.getAktivitas());
            pstmt.setInt(8, p.getIdProduk());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM produk WHERE id_produk = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
