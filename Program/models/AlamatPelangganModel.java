package models;

import config.Database;
import entities.AlamatPelanggan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlamatPelangganModel {
    public List<AlamatPelanggan> getAll() throws SQLException {
        List<AlamatPelanggan> list = new ArrayList<>();
        String sql = "SELECT ap.*, p.nama_depan + ' ' + ISNULL(p.nama_belakang, '') as nama_lengkap " +
                     "FROM alamat_pelanggan ap " +
                     "JOIN pengguna p ON ap.id_pengguna = p.id_pengguna";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AlamatPelanggan ap = new AlamatPelanggan(
                    rs.getInt("id_pengguna"),
                    rs.getInt("id_alamat"),
                    rs.getString("provinsi"),
                    rs.getString("kota"),
                    rs.getString("jalan"),
                    rs.getString("nama_penerima"),
                    rs.getString("no_telp")
                );
                ap.setNamaPelanggan(rs.getString("nama_lengkap"));
                list.add(ap);
            }
        }
        return list;
    }

    public void insert(AlamatPelanggan ap) throws SQLException {
        String sql = "INSERT INTO alamat_pelanggan (id_pengguna, provinsi, kota, jalan, nama_penerima, no_telp) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ap.getIdPengguna());
            pstmt.setString(2, ap.getProvinsi());
            pstmt.setString(3, ap.getKota());
            pstmt.setString(4, ap.getJalan());
            pstmt.setString(5, ap.getNamaPenerima());
            pstmt.setString(6, ap.getNoTelp());
            pstmt.executeUpdate();
        }
    }

    public void update(AlamatPelanggan ap) throws SQLException {
        String sql = "UPDATE alamat_pelanggan SET provinsi = ?, kota = ?, jalan = ?, nama_penerima = ?, no_telp = ? WHERE id_pengguna = ? AND id_alamat = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ap.getProvinsi());
            pstmt.setString(2, ap.getKota());
            pstmt.setString(3, ap.getJalan());
            pstmt.setString(4, ap.getNamaPenerima());
            pstmt.setString(5, ap.getNoTelp());
            pstmt.setInt(6, ap.getIdPengguna());
            pstmt.setInt(7, ap.getIdAlamat());
            pstmt.executeUpdate();
        }
    }

    public void delete(int idPengguna, int idAlamat) throws SQLException {
        String sql = "DELETE FROM alamat_pelanggan WHERE id_pengguna = ? AND id_alamat = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPengguna);
            pstmt.setInt(2, idAlamat);
            pstmt.executeUpdate();
        }
    }
}
