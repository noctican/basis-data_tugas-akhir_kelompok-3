package models;

import config.DatabaseConfig;
import entities.*;
import helpers.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class TransactionModel {
    
    public Keranjang getOrCreateCart(int idPengguna) {
        String select = "SELECT * FROM keranjang WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(select)) {
            pstmt.setInt(1, idPengguna);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Keranjang k = new Keranjang();
                    k.setIdKeranjang(rs.getInt("id_keranjang"));
                    k.setIdPengguna(rs.getInt("id_pengguna"));
                    return k;
                }
            }
            
            String insert = "INSERT INTO keranjang (id_pengguna) VALUES (?)";
            try (PreparedStatement istmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                istmt.setInt(1, idPengguna);
                istmt.executeUpdate();
                try (ResultSet rs = istmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Keranjang k = new Keranjang();
                        k.setIdKeranjang(rs.getInt(1));
                        k.setIdPengguna(idPengguna);
                        return k;
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<DetailKeranjang> getCartDetails(int idKeranjang) {
        List<DetailKeranjang> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_keranjang WHERE id_keranjang = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idKeranjang);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetailKeranjang d = new DetailKeranjang();
                    d.setIdKeranjang(rs.getInt("id_keranjang"));
                    d.setIdProduk(rs.getInt("id_produk"));
                    d.setSku(rs.getString("sku"));
                    d.setKuantitas(rs.getInt("kuantitas"));
                    d.setSubTotal(rs.getBigDecimal("sub_total"));
                    list.add(d);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addToCart(DetailKeranjang d) {
        String check = "SELECT kuantitas, sub_total FROM detail_keranjang WHERE id_keranjang = ? AND id_produk = ? AND sku = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            try (PreparedStatement cstmt = conn.prepareStatement(check)) {
                cstmt.setInt(1, d.getIdKeranjang());
                cstmt.setInt(2, d.getIdProduk());
                cstmt.setString(3, d.getSku());
                try (ResultSet rs = cstmt.executeQuery()) {
                    if (rs.next()) {
                        String update = "UPDATE detail_keranjang SET kuantitas = kuantitas + ?, sub_total = sub_total + ? WHERE id_keranjang = ? AND id_produk = ? AND sku = ?";
                        try (PreparedStatement ustmt = conn.prepareStatement(update)) {
                            ustmt.setInt(1, d.getKuantitas());
                            ustmt.setBigDecimal(2, d.getSubTotal());
                            ustmt.setInt(3, d.getIdKeranjang());
                            ustmt.setInt(4, d.getIdProduk());
                            ustmt.setString(5, d.getSku());
                            return ustmt.executeUpdate() > 0;
                        }
                    } else {
                        String insert = "INSERT INTO detail_keranjang (id_keranjang, id_produk, sku, kuantitas, sub_total) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement istmt = conn.prepareStatement(insert)) {
                            istmt.setInt(1, d.getIdKeranjang());
                            istmt.setInt(2, d.getIdProduk());
                            istmt.setString(3, d.getSku());
                            istmt.setInt(4, d.getKuantitas());
                            istmt.setBigDecimal(5, d.getSubTotal());
                            return istmt.executeUpdate() > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean checkout(int idPengguna, int idKeranjang, String metodeBayar) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            List<DetailKeranjang> items = getCartDetails(idKeranjang);
            if (items.isEmpty()) return false;

            BigDecimal total = BigDecimal.ZERO;
            for (DetailKeranjang item : items) total = total.add(item.getSubTotal());

            int idTransaksi = -1;
            String sqlT = "INSERT INTO transaksi (total_pembelian, metode_pembayaran) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlT, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setBigDecimal(1, total);
                pstmt.setString(2, metodeBayar);
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) idTransaksi = rs.getInt(1);
                }
            }

            String sqlPT = "INSERT INTO pelanggan_transaksi (id_pengguna, id_transaksi) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPT)) {
                pstmt.setInt(1, idPengguna);
                pstmt.setInt(2, idTransaksi);
                pstmt.executeUpdate();
            }

            String sqlDT = "INSERT INTO detail_transaksi (id_transaksi, id_produk, sku, harga_pembelian, kuantitas) VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE varian_produk SET stock = stock - ? WHERE id_produk = ? AND sku = ?";
            try (PreparedStatement pstmtDT = conn.prepareStatement(sqlDT);
                 PreparedStatement pstmtS = conn.prepareStatement(sqlStock)) {
                for (DetailKeranjang item : items) {
                    pstmtDT.setInt(1, idTransaksi);
                    pstmtDT.setInt(2, item.getIdProduk());
                    pstmtDT.setString(3, item.getSku());
                    pstmtDT.setBigDecimal(4, item.getSubTotal().divide(new BigDecimal(item.getKuantitas()), 4, BigDecimal.ROUND_HALF_UP));
                    pstmtDT.setInt(5, item.getKuantitas());
                    pstmtDT.executeUpdate();

                    pstmtS.setInt(1, item.getKuantitas());
                    pstmtS.setInt(2, item.getIdProduk());
                    pstmtS.setString(3, item.getSku());
                    pstmtS.executeUpdate();
                }
            }

            String sqlClear = "DELETE FROM detail_keranjang WHERE id_keranjang = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlClear)) {
                pstmt.setInt(1, idKeranjang);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            DBHelper.rollback(conn);
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    public List<Object[]> getTop5PurchasedItems(int idPengguna) {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT Nama_Produk, Kategori, Total_Jumlah, Harga_Satuan FROM v_Top5BarangPelanggan WHERE ID_Pengguna = ?";
        
        try (Connection conn = DatabaseConfig.getConnection(); 
            PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idPengguna);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("Nama_Produk"),
                        rs.getString("Kategori"),
                        rs.getInt("Total_Jumlah"),
                        rs.getBigDecimal("Harga_Satuan")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateCartQuantity(int idKeranjang, int idProduk, String sku, int newQty) {
        String sql = "UPDATE detail_keranjang SET kuantitas = ?, sub_total = (SELECT harga_base * ? FROM produk WHERE id_produk = ?) WHERE id_keranjang = ? AND id_produk = ? AND sku = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newQty);
            pstmt.setInt(2, newQty);
            pstmt.setInt(3, idProduk);
            pstmt.setInt(4, idKeranjang);
            pstmt.setInt(5, idProduk);
            pstmt.setString(6, sku);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteCartItem(int idKeranjang, int idProduk, String sku) {
        String sql = "DELETE FROM detail_keranjang WHERE id_keranjang = ? AND id_produk = ? AND sku = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idKeranjang);
            pstmt.setInt(2, idProduk);
            pstmt.setString(3, sku);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int checkoutReservasi(int idPengguna, String metodePembayaran, StringBuilder outErrorMessage) {
        String sql = "{call sp_Checkout_Reservasi_HapusKeranjang(?, ?, ?)}";
        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idPengguna);
            cstmt.setString(2, metodePembayaran);
            cstmt.registerOutParameter(3, Types.INTEGER); // @id_transaksi_baru OUTPUT
            
            cstmt.execute();
            return cstmt.getInt(3);
        } catch (SQLException e) {
            outErrorMessage.append(e.getMessage());
            return -1;
        }
    }

    public String bayarTransaksi(int idPengguna, int idTransaksi) {
        String sql = "{call sp_BayarTransaksi(?, ?, ?)}";
        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idPengguna);
            cstmt.setInt(2, idTransaksi);
            cstmt.registerOutParameter(3, Types.NVARCHAR); // @hasil_pesan OUTPUT
            
            cstmt.execute();
            return cstmt.getString(3);
        } catch (SQLException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public int cekExpiredPembayaran() {
        String sql = "{call sp_CekExpiredPembayaran(?)}";
        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.registerOutParameter(1, Types.INTEGER); // @jumlah_expired OUTPUT
            cstmt.execute();
            return cstmt.getInt(1);
        } catch (SQLException e) {
            System.err.println("Gagal cek expired: " + e.getMessage());
            return 0;
        }
    }
    
    public String gagalkanPembayaran(int idTransaksi) {
        String sql = "{call sp_GagalkanPembayaran(?, ?)}";
        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idTransaksi);
            cstmt.registerOutParameter(2, Types.NVARCHAR); // @hasil_pesan OUTPUT
            
            cstmt.execute();
            return cstmt.getString(2);
        } catch (SQLException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public List<Object[]> getEligibleReturnItems(int idPengguna) {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT * FROM (" +
                       "  SELECT t.id_transaksi, p.nama_produk, dt.id_produk, dt.sku, dt.kuantitas, " +
                       "  (dt.kuantitas - ISNULL((SELECT SUM(dr.kuantitas) FROM detail_retur dr JOIN retur r ON dr.id_retur = r.id_retur WHERE dr.id_transaksi = t.id_transaksi AND dr.id_produk = dt.id_produk AND dr.sku = dt.sku AND r.status <> 'Failed'), 0)) AS sisa_kuantitas " +
                       "  FROM transaksi t " +
                       "  JOIN pelanggan_transaksi pt ON t.id_transaksi = pt.id_transaksi " +
                       "  JOIN detail_transaksi dt ON t.id_transaksi = dt.id_transaksi " +
                       "  JOIN produk p ON dt.id_produk = p.id_produk " +
                       "  WHERE pt.id_pengguna = ? AND t.status_pembayaran = 'PAID' " +
                       ") sub WHERE sisa_kuantitas > 0";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idPengguna);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_transaksi"),
                    rs.getString("nama_produk"),
                    rs.getInt("id_produk"),
                    rs.getString("sku"),
                    rs.getInt("kuantitas"),
                    rs.getInt("sisa_kuantitas")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getTransactionsByUser(int idPengguna) {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT t.id_transaksi, t.tanggal_transaksi, t.total_pembelian, " +
                    "t.status_pembayaran, t.metode_pembayaran " +
                    "FROM transaksi t " +
                    "JOIN pelanggan_transaksi pt ON t.id_transaksi = pt.id_transaksi " +
                    "WHERE pt.id_pengguna = ? " +
                    "ORDER BY t.tanggal_transaksi DESC";
                    
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idPengguna);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_transaksi"),
                    rs.getDate("tanggal_transaksi"),
                    rs.getBigDecimal("total_pembelian"),
                    rs.getString("status_pembayaran"),
                    rs.getString("metode_pembayaran")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getTransactionDetails(int idTransaksi) {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT id_produk, sku, harga_pembelian, kuantitas " +
                    "FROM detail_transaksi " +
                    "WHERE id_transaksi = ?";
                    
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                java.math.BigDecimal harga = rs.getBigDecimal("harga_pembelian");
                int qty = rs.getInt("kuantitas");
                java.math.BigDecimal subtotal = harga.multiply(new java.math.BigDecimal(qty));
                
                list.add(new Object[]{
                    rs.getInt("id_produk"),
                    rs.getString("sku"),
                    harga,
                    qty,
                    subtotal
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
