package models;

import config.DatabaseConfig;
import entities.Pengiriman;
import entities.Transaksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderModel {

    public List<Transaksi> getAllOrders() {
        return getAllOrdersFiltered("All", "All");
    }

    public List<Transaksi> getAllOrdersFiltered(String paymentStatus, String shippingStatus) {
        List<Transaksi> list = new ArrayList<>();
        
        StringBuilder query = new StringBuilder(
            "SELECT t.id_transaksi, t.tanggal_transaksi, t.total_pembelian, t.status_pembayaran, t.metode_pembayaran, " +
            "p.no_resi, p.status_pengiriman " +
            "FROM transaksi t " +
            "LEFT JOIN pengiriman p ON t.id_transaksi = p.id_transaksi WHERE 1=1 "
        );

        if (paymentStatus != null && !"All".equalsIgnoreCase(paymentStatus)) {
            query.append("AND t.status_pembayaran = ? ");
        }

        if (shippingStatus != null && !"All".equalsIgnoreCase(shippingStatus)) {
            if ("Not Processed".equalsIgnoreCase(shippingStatus) || "Process".equalsIgnoreCase(shippingStatus)) {
                query.append("AND (p.status_pengiriman IS NULL OR p.status_pengiriman = 'Process') ");
            } else {
                query.append("AND p.status_pengiriman = ? ");
            }
        }
        
        query.append("ORDER BY t.id_transaksi DESC");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            if (paymentStatus != null && !"All".equalsIgnoreCase(paymentStatus)) {
                ps.setString(paramIndex++, paymentStatus);
            }
            if (shippingStatus != null && !"All".equalsIgnoreCase(shippingStatus)) {
                if (!"Not Processed".equalsIgnoreCase(shippingStatus) && !"Process".equalsIgnoreCase(shippingStatus)) {
                    ps.setString(paramIndex++, shippingStatus);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaksi t = new Transaksi();
                    t.setIdTransaksi(rs.getInt("id_transaksi"));
                    t.setTanggalTransaksi(rs.getTimestamp("tanggal_transaksi"));
                    t.setTotalPembelian(rs.getBigDecimal("total_pembelian")); // Menggunakan BigDecimal sesuai ResultSet
                    t.setStatusPembayaran(rs.getString("status_pembayaran"));
                    t.setMetodePembayaran(rs.getString("metode_pembayaran"));
                    
                    Pengiriman p = new Pengiriman();
                    String noResi = rs.getString("no_resi");
                    String statusDb = rs.getString("status_pengiriman");

                    if (noResi != null) {
                        p.setNoResi(noResi);
                        p.setStatusPengiriman(statusDb != null ? statusDb : "Process");
                        p.setIdTransaksi(t.getIdTransaksi()); 
                    } else {
                        p.setStatusPengiriman("Process");
                    }
                    
                    t.setPengiriman(p); 
                    
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }

    public boolean updateShippingStatus(int idTransaksi, String noResi, String statusPengiriman) {
        String checkSql = "SELECT COUNT(*) FROM pengiriman WHERE id_transaksi = ?";
        String updateSql = "UPDATE pengiriman SET status_pengiriman = ?, no_resi = ?, tanggal_pengiriman = GETDATE() WHERE id_transaksi = ?";
        String insertSql = "INSERT INTO pengiriman (no_resi, id_transaksi, status_pengiriman, tanggal_pengiriman) VALUES (?, ?, ?, GETDATE())";

        try (Connection conn = DatabaseConfig.getConnection()) {
            boolean exists = false;

            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, idTransaksi);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        exists = true;
                    }
                }
            }

            if (exists) {
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setString(1, statusPengiriman); // 'Delivered' atau 'Canceled'
                    psUpdate.setString(2, noResi);
                    psUpdate.setInt(3, idTransaksi);
                    return psUpdate.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setString(1, noResi);
                    psInsert.setInt(2, idTransaksi);
                    psInsert.setString(3, statusPengiriman); 
                    return psInsert.executeUpdate() > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error saat memperbarui status pengiriman: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}