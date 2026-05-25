package models;

import config.Database;
import entities.Kategori;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriModel {
    public List<Kategori> getAll() throws SQLException {
        List<Kategori> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Kategori(rs.getInt("id_kategori"), rs.getString("nama_kategori")));
            }
        }
        return list;
    }

    public void insert(Kategori kategori) throws SQLException {
        String sql = "INSERT INTO kategori (nama_kategori) VALUES (?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kategori.getNamaKategori());
            pstmt.executeUpdate();
        }
    }

    public void update(Kategori kategori) throws SQLException {
        String sql = "UPDATE kategori SET nama_kategori = ? WHERE id_kategori = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kategori.getNamaKategori());
            pstmt.setInt(2, kategori.getIdKategori());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM kategori WHERE id_kategori = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
