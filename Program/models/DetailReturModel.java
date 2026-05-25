package models;

import config.Database;
import entities.DetailRetur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailReturModel {
    public List<DetailRetur> getAll() throws SQLException {
        List<DetailRetur> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_retur";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DetailRetur(
                    rs.getInt("id_transaksi"),
                    rs.getInt("id_retur"),
                    rs.getInt("id_produk"),
                    rs.getString("sku"),
                    rs.getInt("kuantitas"),
                    rs.getString("alasan")
                ));
            }
        }
        return list;
    }

    public void insert(DetailRetur dr) throws SQLException {
        String sql = "INSERT INTO detail_retur (id_transaksi, id_retur, id_produk, sku, kuantitas, alasan) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dr.getIdTransaksi());
            pstmt.setInt(2, dr.getIdRetur());
            pstmt.setInt(3, dr.getIdProduk());
            pstmt.setString(4, dr.getSku());
            pstmt.setInt(5, dr.getKuantitas());
            pstmt.setString(6, dr.getAlasan());
            pstmt.executeUpdate();
        }
    }

    public void update(DetailRetur dr) throws SQLException {
        String sql = "UPDATE detail_retur SET kuantitas = ?, alasan = ? WHERE id_transaksi = ? AND id_retur = ? AND id_produk = ? AND sku = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dr.getKuantitas());
            pstmt.setString(2, dr.getAlasan());
            pstmt.setInt(3, dr.getIdTransaksi());
            pstmt.setInt(4, dr.getIdRetur());
            pstmt.setInt(5, dr.getIdProduk());
            pstmt.setString(6, dr.getSku());
            pstmt.executeUpdate();
        }
    }

    public void delete(int idTransaksi, int idRetur, int idProduk, String sku) throws SQLException {
        String sql = "DELETE FROM detail_retur WHERE id_transaksi = ? AND id_retur = ? AND id_produk = ? AND sku = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTransaksi);
            pstmt.setInt(2, idRetur);
            pstmt.setInt(3, idProduk);
            pstmt.setString(4, sku);
            pstmt.executeUpdate();
        }
    }
}
