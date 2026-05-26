package models;

import config.DatabaseConfig;
import entities.*;
import helpers.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogModel {
    // --- Kategori CRUD ---
    public List<Kategori> getAllKategori() {
        List<Kategori> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Kategori(rs.getInt("id_kategori"), rs.getString("nama_kategori")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addKategori(String nama) {
        String sql = "INSERT INTO kategori (nama_kategori) VALUES (?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- SubKategori CRUD ---
    public List<SubKategori> getSubKategoriByKategori(int idKategori) {
        List<SubKategori> list = new ArrayList<>();
        String sql = "SELECT * FROM sub_kategori WHERE id_kategori = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idKategori);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new SubKategori(rs.getInt("id_sub_kategori"), rs.getInt("id_kategori"), rs.getString("nama_sub_kategori")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addSubKategori(int idKategori, String nama) {
        String sql = "INSERT INTO sub_kategori (id_kategori, nama_sub_kategori) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idKategori);
            pstmt.setString(2, nama);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- Produk CRUD ---
    public List<Produk> searchProduk(String keyword) {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT * FROM produk WHERE nama_produk LIKE ? OR deskripsi_produk LIKE ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produk p = new Produk();
                    p.setIdProduk(rs.getInt("id_produk"));
                    p.setIdKategori(rs.getInt("id_kategori"));
                    p.setIdSubKategori(rs.getInt("id_sub_kategori"));
                    p.setNamaProduk(rs.getString("nama_produk"));
                    p.setDeskripsiProduk(rs.getString("deskripsi_produk"));
                    p.setHargaBase(rs.getBigDecimal("harga_base"));
                    p.setDemografi(rs.getString("demografi"));
                    p.setAktivitas(rs.getString("aktivitas"));
                    list.add(p);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addProduk(Produk p) {
        String sql = "INSERT INTO produk (id_kategori, id_sub_kategori, nama_produk, deskripsi_produk, harga_base, demografi, aktivitas) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getIdKategori());
            pstmt.setInt(2, p.getIdSubKategori());
            pstmt.setString(3, p.getNamaProduk());
            pstmt.setString(4, p.getDeskripsiProduk());
            pstmt.setBigDecimal(5, p.getHargaBase());
            pstmt.setString(6, p.getDemografi());
            pstmt.setString(7, p.getAktivitas());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- VarianProduk CRUD ---
    public List<VarianProduk> getVariansByProduk(int idProduk) {
        List<VarianProduk> list = new ArrayList<>();
        String sql = "SELECT * FROM varian_produk WHERE id_produk = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduk);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    VarianProduk v = new VarianProduk();
                    v.setIdProduk(rs.getInt("id_produk"));
                    v.setSku(rs.getString("sku"));
                    v.setStock(rs.getInt("stock"));
                    v.setWarna(rs.getString("warna"));
                    v.setHargaVarian(rs.getBigDecimal("harga_varian"));
                    v.setUkuran(rs.getString("ukuran"));
                    list.add(v);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addVarian(VarianProduk v) {
        String sql = "INSERT INTO varian_produk (id_produk, sku, stock, warna, harga_varian, ukuran) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, v.getIdProduk());
            pstmt.setString(2, v.getSku());
            pstmt.setInt(3, v.getStock());
            pstmt.setString(4, v.getWarna());
            pstmt.setBigDecimal(5, v.getHargaVarian());
            pstmt.setString(6, v.getUkuran());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
