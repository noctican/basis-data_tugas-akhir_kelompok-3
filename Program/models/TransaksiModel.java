package models;

import config.Database;
import entities.Transaksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiModel {
    public List<Transaksi> getAll() throws SQLException {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Transaksi(
                    rs.getInt("id_transaksi"),
                    rs.getTimestamp("tanggal_transaksi"),
                    rs.getBigDecimal("total_pembelian"),
                    rs.getString("status_pembayaran"),
                    rs.getString("metode_pembayaran")
                ));
            }
        }
        return list;
    }

    public void insert(Transaksi t) throws SQLException {
        String sql = "INSERT INTO transaksi (tanggal_transaksi, total_pembelian, status_pembayaran, metode_pembayaran) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, t.getTanggalTransaksi());
            pstmt.setBigDecimal(2, t.getTotalPembelian());
            pstmt.setString(3, t.getStatusPembayaran());
            pstmt.setString(4, t.getMetodePembayaran());
            pstmt.executeUpdate();
        }
    }

    public void update(Transaksi t) throws SQLException {
        String sql = "UPDATE transaksi SET tanggal_transaksi = ?, total_pembelian = ?, status_pembayaran = ?, metode_pembayaran = ? WHERE id_transaksi = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, t.getTanggalTransaksi());
            pstmt.setBigDecimal(2, t.getTotalPembelian());
            pstmt.setString(3, t.getStatusPembayaran());
            pstmt.setString(4, t.getMetodePembayaran());
            pstmt.setInt(5, t.getIdTransaksi());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM transaksi WHERE id_transaksi = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
