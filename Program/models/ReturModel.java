package models;

import config.Database;
import entities.Retur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturModel {
    public List<Retur> getAll() throws SQLException {
        List<Retur> list = new ArrayList<>();
        String sql = "SELECT * FROM retur";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Retur(
                    rs.getInt("id_retur"),
                    (Integer) rs.getObject("id_transaksi"),
                    rs.getDate("tanggal_pengembalian"),
                    rs.getString("status"),
                    (Integer) rs.getObject("id_validator")
                ));
            }
        }
        return list;
    }

    public void insert(Retur r) throws SQLException {
        String sql = "INSERT INTO retur (id_transaksi, tanggal_pengembalian, status, id_validator) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (r.getIdTransaksi() == null) pstmt.setNull(1, Types.INTEGER);
            else pstmt.setInt(1, r.getIdTransaksi());
            pstmt.setDate(2, r.getTanggalPengembalian());
            pstmt.setString(3, r.getStatus());
            if (r.getIdValidator() == null) pstmt.setNull(4, Types.INTEGER);
            else pstmt.setInt(4, r.getIdValidator());
            pstmt.executeUpdate();
        }
    }

    public void update(Retur r) throws SQLException {
        String sql = "UPDATE retur SET id_transaksi = ?, tanggal_pengembalian = ?, status = ?, id_validator = ? WHERE id_retur = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (r.getIdTransaksi() == null) pstmt.setNull(1, Types.INTEGER);
            else pstmt.setInt(1, r.getIdTransaksi());
            pstmt.setDate(2, r.getTanggalPengembalian());
            pstmt.setString(3, r.getStatus());
            if (r.getIdValidator() == null) pstmt.setNull(4, Types.INTEGER);
            else pstmt.setInt(4, r.getIdValidator());
            pstmt.setInt(5, r.getIdRetur());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM retur WHERE id_retur = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
