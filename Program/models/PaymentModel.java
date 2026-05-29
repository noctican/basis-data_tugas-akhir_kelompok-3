package models;

import config.DatabaseConfig;
import helpers.DBHelper;

import java.sql.*;
import java.math.BigDecimal;

public class PaymentModel {

    // ----------------------------------------------------------------
    // Struktur hasil agar UI bisa cek sukses/gagal + pesan
    // ----------------------------------------------------------------
    public static class HasilPembayaran {
        public final boolean sukses;
        public final String pesan;

        public HasilPembayaran(boolean sukses, String pesan) {
            this.sukses = sukses;
            this.pesan  = pesan;
        }
    }

    // ----------------------------------------------------------------
    // 1. Bayar transaksi (wallet)
    // ----------------------------------------------------------------
    public HasilPembayaran bayarTransaksi(int idPengguna, int idTransaksi) {
        String sql = "{CALL sp_BayarTransaksi(?, ?, ?)}";

        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idPengguna);
            cs.setInt(2, idTransaksi);
            cs.registerOutParameter(3, Types.NVARCHAR);

            cs.execute();

            String pesan = cs.getString(3);
            boolean sukses = pesan != null && pesan.startsWith("SUKSES");
            return new HasilPembayaran(sukses, pesan);

        } catch (SQLException e) {
            e.printStackTrace();
            return new HasilPembayaran(false, "ERROR: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // 2. Batalkan / gagalkan pembayaran secara manual
    // ----------------------------------------------------------------
    public HasilPembayaran gagalkanPembayaran(int idTransaksi) {
        String sql = "{CALL sp_GagalkanPembayaran(?, ?)}";

        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idTransaksi);
            cs.registerOutParameter(2, Types.NVARCHAR);

            cs.execute();

            String pesan = cs.getString(2);
            boolean sukses = pesan != null && pesan.startsWith("SUKSES");
            return new HasilPembayaran(sukses, pesan);

        } catch (SQLException e) {
            e.printStackTrace();
            return new HasilPembayaran(false, "ERROR: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // 3. Cek & expire semua transaksi PENDING > 10 menit
    //    Dipanggil saat panel dibuka atau dari timer
    // ----------------------------------------------------------------
    public int cekExpiredPembayaran() {
        String sql = "{CALL sp_CekExpiredPembayaran(?)}";

        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            return cs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // -1 = error
        }
    }

    // ----------------------------------------------------------------
    // Helper: ambil info transaksi (id, status, total, tanggal, sisa waktu)
    // ----------------------------------------------------------------
    public Object[] getInfoTransaksi(int idTransaksi, int idPengguna) {
        String sql =
            "SELECT t.id_transaksi, t.status_pembayaran, t.total_pembelian, " +
            "       t.tanggal_transaksi, t.metode_pembayaran, " +
            "       DATEDIFF(SECOND, t.tanggal_transaksi, GETDATE()) AS detik_berlalu, " +
            "       p.wallet " +
            "FROM   transaksi t " +
            "JOIN   pelanggan_transaksi pt ON t.id_transaksi = pt.id_transaksi " +
            "JOIN   pelanggan p ON p.id_pengguna = pt.id_pengguna " +
            "WHERE  t.id_transaksi = ? AND pt.id_pengguna = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTransaksi);
            ps.setInt(2, idPengguna);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_transaksi"),
                        rs.getString("status_pembayaran"),
                        rs.getBigDecimal("total_pembelian"),
                        rs.getTimestamp("tanggal_transaksi"),
                        rs.getString("metode_pembayaran"),
                        rs.getInt("detik_berlalu"),
                        rs.getBigDecimal("wallet")
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ----------------------------------------------------------------
    // Helper: ambil semua transaksi milik pelanggan
    // ----------------------------------------------------------------
    public java.util.List<Object[]> getTransaksiPelanggan(int idPengguna) {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql =
            "SELECT t.id_transaksi, t.tanggal_transaksi, t.total_pembelian, " +
            "       t.status_pembayaran, t.metode_pembayaran, " +
            "       DATEDIFF(SECOND, t.tanggal_transaksi, GETDATE()) AS detik_berlalu " +
            "FROM   transaksi t " +
            "JOIN   pelanggan_transaksi pt ON t.id_transaksi = pt.id_transaksi " +
            "WHERE  pt.id_pengguna = ? " +
            "ORDER  BY t.tanggal_transaksi DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPengguna);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id_transaksi"),
                        rs.getTimestamp("tanggal_transaksi"),
                        rs.getBigDecimal("total_pembelian"),
                        rs.getString("status_pembayaran"),
                        rs.getString("metode_pembayaran"),
                        rs.getInt("detik_berlalu")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
