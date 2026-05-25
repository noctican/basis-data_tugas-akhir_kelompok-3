package models;

import config.Database;
import entities.Keranjang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KeranjangModel {
    public List<Keranjang> getAll() throws SQLException {
        List<Keranjang> list = new ArrayList<>();
        String sql = "SELECT k.*, p.nama_depan + ' ' + ISNULL(p.nama_belakang, '') as nama_lengkap " +
                     "FROM keranjang k " +
                     "JOIN pengguna p ON k.id_pengguna = p.id_pengguna";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Keranjang k = new Keranjang(
                    rs.getInt("id_keranjang"),
                    (Integer) rs.getObject("id_pengguna")
                );
                k.setNamaPelanggan(rs.getString("nama_lengkap"));
                list.add(k);
            }
        }
        return list;
    }

    public void insert(Keranjang k) throws SQLException {
        String sql = "INSERT INTO keranjang (id_pengguna) VALUES (?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (k.getIdPengguna() == null) pstmt.setNull(1, Types.INTEGER);
            else pstmt.setInt(1, k.getIdPengguna());
            pstmt.executeUpdate();
        }
    }

    public void update(Keranjang k) throws SQLException {
        String sql = "UPDATE keranjang SET id_pengguna = ? WHERE id_keranjang = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (k.getIdPengguna() == null) pstmt.setNull(1, Types.INTEGER);
            else pstmt.setInt(1, k.getIdPengguna());
            pstmt.setInt(2, k.getIdKeranjang());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM keranjang WHERE id_keranjang = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
