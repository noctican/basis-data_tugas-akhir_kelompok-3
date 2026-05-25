package models;

import config.Database;
import entities.SubKategori;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubKategoriModel {
    public List<SubKategori> getAll() throws SQLException {
        List<SubKategori> list = new ArrayList<>();
        String sql = "SELECT sk.*, k.nama_kategori FROM sub_kategori sk JOIN kategori k ON sk.id_kategori = k.id_kategori";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                SubKategori sk = new SubKategori(rs.getInt("id_kategori"), rs.getInt("id_sub_kategori"), rs.getString("nama_sub_kategori"));
                sk.setNamaKategori(rs.getString("nama_kategori"));
                list.add(sk);
            }
        }
        return list;
    }

    public void insert(SubKategori sk) throws SQLException {
        String sql = "INSERT INTO sub_kategori (id_kategori, nama_sub_kategori) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sk.getIdKategori());
            pstmt.setString(2, sk.getNamaSubKategori());
            pstmt.executeUpdate();
        }
    }

    public void update(SubKategori sk) throws SQLException {
        String sql = "UPDATE sub_kategori SET nama_sub_kategori = ? WHERE id_kategori = ? AND id_sub_kategori = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sk.getNamaSubKategori());
            pstmt.setInt(2, sk.getIdKategori());
            pstmt.setInt(3, sk.getIdSubKategori());
            pstmt.executeUpdate();
        }
    }

    public void delete(int idKategori, int idSubKategori) throws SQLException {
        String sql = "DELETE FROM sub_kategori WHERE id_kategori = ? AND id_sub_kategori = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idKategori);
            pstmt.setInt(2, idSubKategori);
            pstmt.executeUpdate();
        }
    }
}
