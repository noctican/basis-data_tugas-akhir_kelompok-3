package models;

import config.Database;
import entities.PelangganTransaksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PelangganTransaksiModel {
    public List<PelangganTransaksi> getAll() throws SQLException {
        List<PelangganTransaksi> list = new ArrayList<>();
        String sql = "SELECT pt.*, p.nama_depan + ' ' + ISNULL(p.nama_belakang, '') as nama_lengkap " +
                     "FROM pelanggan_transaksi pt " +
                     "JOIN pengguna p ON pt.id_pengguna = p.id_pengguna";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PelangganTransaksi pt = new PelangganTransaksi(
                    rs.getInt("id_pengguna"),
                    rs.getInt("id_transaksi")
                );
                pt.setNamaPelanggan(rs.getString("nama_lengkap"));
                list.add(pt);
            }
        }
        return list;
    }

    public void insert(PelangganTransaksi pt) throws SQLException {
        String sql = "INSERT INTO pelanggan_transaksi (id_pengguna, id_transaksi) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pt.getIdPengguna());
            pstmt.setInt(2, pt.getIdTransaksi());
            pstmt.executeUpdate();
        }
    }

    public void delete(int idPengguna, int idTransaksi) throws SQLException {
        String sql = "DELETE FROM pelanggan_transaksi WHERE id_pengguna = ? AND id_transaksi = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPengguna);
            pstmt.setInt(2, idTransaksi);
            pstmt.executeUpdate();
        }
    }
}
