package models;

import config.Database;
import entities.DetailTransaksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailTransaksiModel {
    public List<DetailTransaksi> getAll() throws SQLException {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_transaksi";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DetailTransaksi(
                    rs.getInt("id_produk"),
                    rs.getString("sku"),
                    rs.getInt("id_transaksi"),
                    rs.getBigDecimal("harga_pembelian"),
                    rs.getInt("kuantitas")
                ));
            }
        }
        return list;
    }

    public void insert(DetailTransaksi dt) throws SQLException {
        String sql = "INSERT INTO detail_transaksi (id_produk, sku, id_transaksi, harga_pembelian, kuantitas) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dt.getIdProduk());
            pstmt.setString(2, dt.getSku());
            pstmt.setInt(3, dt.getIdTransaksi());
            pstmt.setBigDecimal(4, dt.getHargaPembelian());
            pstmt.setInt(5, dt.getKuantitas());
            pstmt.executeUpdate();
        }
    }

    public void update(DetailTransaksi dt) throws SQLException {
        String sql = "UPDATE detail_transaksi SET harga_pembelian = ?, kuantitas = ? WHERE id_transaksi = ? AND id_produk = ? AND sku = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, dt.getHargaPembelian());
            pstmt.setInt(2, dt.getKuantitas());
            pstmt.setInt(3, dt.getIdTransaksi());
            pstmt.setInt(4, dt.getIdProduk());
            pstmt.setString(5, dt.getSku());
            pstmt.executeUpdate();
        }
    }

    public void delete(int idTransaksi, int idProduk, String sku) throws SQLException {
        String sql = "DELETE FROM detail_transaksi WHERE id_transaksi = ? AND id_produk = ? AND sku = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTransaksi);
            pstmt.setInt(2, idProduk);
            pstmt.setString(3, sku);
            pstmt.executeUpdate();
        }
    }
}
