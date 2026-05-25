package models;

import config.Database;
import entities.Pelanggan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PelangganModel {
    public List<Pelanggan> getAll() throws SQLException {
        List<Pelanggan> list = new ArrayList<>();
        String sql = "SELECT pl.*, p.nama_depan + ' ' + ISNULL(p.nama_belakang, '') as nama_lengkap " +
                     "FROM pelanggan pl " +
                     "JOIN pengguna p ON pl.id_pengguna = p.id_pengguna";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pelanggan pl = new Pelanggan(
                    rs.getInt("id_pengguna"),
                    rs.getString("jenis_member"),
                    rs.getDate("tanggal_bergabung"),
                    rs.getInt("poin"),
                    rs.getBoolean("is_used_voucher_percentage"),
                    rs.getBoolean("is_used_voucher_price")
                );
                pl.setNamaLengkap(rs.getString("nama_lengkap"));
                list.add(pl);
            }
        }
        return list;
    }

    public void insert(Pelanggan pl) throws SQLException {
        String sql = "INSERT INTO pelanggan (id_pengguna, jenis_member, tanggal_bergabung, poin, is_used_voucher_percentage, is_used_voucher_price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pl.getIdPengguna());
            pstmt.setString(2, pl.getJenisMember());
            pstmt.setDate(3, pl.getTanggalBergabung());
            pstmt.setInt(4, pl.getPoin());
            pstmt.setBoolean(5, pl.isUsedVoucherPercentage());
            pstmt.setBoolean(6, pl.isUsedVoucherPrice());
            pstmt.executeUpdate();
        }
    }

    public void update(Pelanggan pl) throws SQLException {
        String sql = "UPDATE pelanggan SET jenis_member = ?, tanggal_bergabung = ?, poin = ?, is_used_voucher_percentage = ?, is_used_voucher_price = ? WHERE id_pengguna = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pl.getJenisMember());
            pstmt.setDate(2, pl.getTanggalBergabung());
            pstmt.setInt(3, pl.getPoin());
            pstmt.setBoolean(4, pl.isUsedVoucherPercentage());
            pstmt.setBoolean(5, pl.isUsedVoucherPrice());
            pstmt.setInt(6, pl.getIdPengguna());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pelanggan WHERE id_pengguna = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
