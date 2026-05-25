package models;

import config.Database;
import entities.Karyawan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KaryawanModel {
    public List<Karyawan> getAll() throws SQLException {
        List<Karyawan> list = new ArrayList<>();
        String sql = "SELECT k.*, p.nama_depan + ' ' + ISNULL(p.nama_belakang, '') as nama_lengkap, d.nama_departemen " +
                     "FROM karyawan k " +
                     "JOIN pengguna p ON k.id_pengguna = p.id_pengguna " +
                     "JOIN departemen d ON k.id_departemen = d.id_departemen";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Karyawan k = new Karyawan(
                    rs.getInt("id_pengguna"),
                    rs.getInt("id_departemen"),
                    rs.getString("jabatan")
                );
                k.setNamaLengkap(rs.getString("nama_lengkap"));
                k.setNamaDepartemen(rs.getString("nama_departemen"));
                list.add(k);
            }
        }
        return list;
    }

    public void insert(Karyawan k) throws SQLException {
        String sql = "INSERT INTO karyawan (id_pengguna, id_departemen, jabatan) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, k.getIdPengguna());
            pstmt.setInt(2, k.getIdDepartemen());
            pstmt.setString(3, k.getJabatan());
            pstmt.executeUpdate();
        }
    }

    public void update(Karyawan k) throws SQLException {
        String sql = "UPDATE karyawan SET id_departemen = ?, jabatan = ? WHERE id_pengguna = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, k.getIdDepartemen());
            pstmt.setString(2, k.getJabatan());
            pstmt.setInt(3, k.getIdPengguna());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM karyawan WHERE id_pengguna = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
