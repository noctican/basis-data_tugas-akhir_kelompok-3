package models;

import config.Database;
import entities.Pengguna;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenggunaModel {
    public List<Pengguna> getAll() throws SQLException {
        List<Pengguna> list = new ArrayList<>();
        String sql = "SELECT * FROM pengguna";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Pengguna(
                    rs.getInt("id_pengguna"),
                    rs.getString("email"),
                    rs.getString("nama_depan"),
                    rs.getString("nama_belakang"),
                    rs.getString("nomor_telepon"),
                    rs.getString("password")
                ));
            }
        }
        return list;
    }

    public void insert(Pengguna p) throws SQLException {
        String sql = "INSERT INTO pengguna (email, nama_depan, nama_belakang, nomor_telepon, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getEmail());
            pstmt.setString(2, p.getNamaDepan());
            pstmt.setString(3, p.getNamaBelakang());
            pstmt.setString(4, p.getNomorTelepon());
            pstmt.setString(5, p.getPassword());
            pstmt.executeUpdate();
        }
    }

    public void update(Pengguna p) throws SQLException {
        String sql = "UPDATE pengguna SET email = ?, nama_depan = ?, nama_belakang = ?, nomor_telepon = ?, password = ? WHERE id_pengguna = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getEmail());
            pstmt.setString(2, p.getNamaDepan());
            pstmt.setString(3, p.getNamaBelakang());
            pstmt.setString(4, p.getNomorTelepon());
            pstmt.setString(5, p.getPassword());
            pstmt.setInt(6, p.getIdPengguna());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pengguna WHERE id_pengguna = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
