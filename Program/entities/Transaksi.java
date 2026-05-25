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

    public Transaksi(int idTransaksi, Timestamp tanggalTransaksi, BigDecimal totalPembelian, String statusPembayaran, String metodePembayaran) {
        this.idTransaksi = idTransaksi;
        this.tanggalTransaksi = tanggalTransaksi;
        this.totalPembelian = totalPembelian;
        this.statusPembayaran = statusPembayaran;
        this.metodePembayaran = metodePembayaran;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public Timestamp getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(Timestamp tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public BigDecimal getTotalPembelian() {
        return totalPembelian;
    }

    public void setTotalPembelian(BigDecimal totalPembelian) {
        this.totalPembelian = totalPembelian;
    }

    public String getStatusPembayaran() {
        return statusPembayaran;
    }

    public void setStatusPembayaran(String statusPembayaran) {
        this.statusPembayaran = statusPembayaran;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    @Override
    public String toString() {
        return "Transaksi #" + idTransaksi + " (" + statusPembayaran + ")";
    }
}
