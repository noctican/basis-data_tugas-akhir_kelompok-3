package models;

import config.Database;
import entities.Pengiriman;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PengirimanModel {
    public List<Pengiriman> getAll() throws SQLException {
        List<Pengiriman> list = new ArrayList<>();
        String sql = "SELECT * FROM pengiriman";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Pengiriman(
                    rs.getString("no_resi"),
                    rs.getInt("id_transaksi"),
                    rs.getString("nama_ekspedisi"),
                    rs.getString("jalan"),
                    rs.getString("nama_penerima"),
                    rs.getString("kota"),
                    rs.getString("no_telp"),
                    rs.getInt("kode_pos"),
                    rs.getString("provinsi"),
                    rs.getString("status_pengiriman"),
                    rs.getBigDecimal("biaya_pengiriman"),
                    rs.getTimestamp("tanggal_pengiriman")
                ));
            }
        }
        return list;
    }

    public void insert(Pengiriman p) throws SQLException {
        String sql = "INSERT INTO pengiriman (no_resi, id_transaksi, nama_ekspedisi, jalan, nama_penerima, kota, no_telp, kode_pos, provinsi, status_pengiriman, biaya_pengiriman, tanggal_pengiriman) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNoResi());
            pstmt.setInt(2, p.getIdTransaksi());
            pstmt.setString(3, p.getNamaEkspedisi());
            pstmt.setString(4, p.getJalan());
            pstmt.setString(5, p.getNamaPenerima());
            pstmt.setString(6, p.getKota());
            pstmt.setString(7, p.getNoTelp());
            pstmt.setInt(8, p.getKodePos());
            pstmt.setString(9, p.getProvinsi());
            pstmt.setString(10, p.getStatusPengiriman());
            pstmt.setBigDecimal(11, p.getBiayaPengiriman());
            pstmt.setTimestamp(12, p.getTanggalPengiriman());
            pstmt.executeUpdate();
        }
    }

    public void update(Pengiriman p) throws SQLException {
        String sql = "UPDATE pengiriman SET nama_ekspedisi = ?, jalan = ?, nama_penerima = ?, kota = ?, no_telp = ?, kode_pos = ?, provinsi = ?, status_pengiriman = ?, biaya_pengiriman = ?, tanggal_pengiriman = ? WHERE id_transaksi = ? AND no_resi = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNamaEkspedisi());
            pstmt.setString(2, p.getJalan());
            pstmt.setString(3, p.getNamaPenerima());
            pstmt.setString(4, p.getKota());
            pstmt.setString(5, p.getNoTelp());
            pstmt.setInt(6, p.getKodePos());
            pstmt.setString(7, p.getProvinsi());
            pstmt.setString(8, p.getStatusPengiriman());
            pstmt.setBigDecimal(9, p.getBiayaPengiriman());
            pstmt.setTimestamp(10, p.getTanggalPengiriman());
            pstmt.setInt(11, p.getIdTransaksi());
            pstmt.setString(12, p.getNoResi());
            pstmt.executeUpdate();
        }
    }

    public void delete(int idTransaksi, String noResi) throws SQLException {
        String sql = "DELETE FROM pengiriman WHERE id_transaksi = ? AND no_resi = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTransaksi);
            pstmt.setString(2, noResi);
            pstmt.executeUpdate();
        }
    }
}
