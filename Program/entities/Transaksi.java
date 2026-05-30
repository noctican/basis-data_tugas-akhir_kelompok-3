package entities;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaksi {
    private int idTransaksi;
    private Timestamp tanggalTransaksi;
    private BigDecimal totalPembelian;
    private String statusPembayaran;
    private String metodePembayaran;

    public Transaksi() {}
    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int id) { this.idTransaksi = id; }
    public Timestamp getTanggalTransaksi() { return tanggalTransaksi; }
    public void setTanggalTransaksi(Timestamp t) { this.tanggalTransaksi = t; }
    public BigDecimal getTotalPembelian() { return totalPembelian; }
    public void setTotalPembelian(BigDecimal t) { this.totalPembelian = t; }
    public String getStatusPembayaran() { return statusPembayaran; }
    public void setStatusPembayaran(String s) { this.statusPembayaran = s; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String m) { this.metodePembayaran = m; }

    private Pengiriman pengiriman; 

    public Pengiriman getPengiriman() {
        return pengiriman;
    }

    public void setPengiriman(Pengiriman pengiriman) {
        this.pengiriman = pengiriman;
    }
}
