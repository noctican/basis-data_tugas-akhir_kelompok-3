package entities;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Pengiriman {
    private String noResi;
    private int idTransaksi;
    private String namaEkspedisi;
    private String jalan;
    private String namaPenerima;
    private String kota;
    private String noTelp;
    private int kodePos;
    private String provinsi;
    private String statusPengiriman;
    private BigDecimal biayaPengiriman;
    private Timestamp tanggalPengiriman;

    public Pengiriman() {}

    public Pengiriman(String noResi, int idTransaksi, String namaEkspedisi, String jalan, String namaPenerima, String kota, String noTelp, int kodePos, String provinsi, String statusPengiriman, BigDecimal biayaPengiriman, Timestamp tanggalPengiriman) {
        this.noResi = noResi;
        this.idTransaksi = idTransaksi;
        this.namaEkspedisi = namaEkspedisi;
        this.jalan = jalan;
        this.namaPenerima = namaPenerima;
        this.kota = kota;
        this.noTelp = noTelp;
        this.kodePos = kodePos;
        this.provinsi = provinsi;
        this.statusPengiriman = statusPengiriman;
        this.biayaPengiriman = biayaPengiriman;
        this.tanggalPengiriman = tanggalPengiriman;
    }

    public String getNoResi() {
        return noResi;
    }

    public void setNoResi(String noResi) {
        this.noResi = noResi;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getNamaEkspedisi() {
        return namaEkspedisi;
    }

    public void setNamaEkspedisi(String namaEkspedisi) {
        this.namaEkspedisi = namaEkspedisi;
    }

    public String getJalan() {
        return jalan;
    }

    public void setJalan(String jalan) {
        this.jalan = jalan;
    }

    public String getNamaPenerima() {
        return namaPenerima;
    }

    public void setNamaPenerima(String namaPenerima) {
        this.namaPenerima = namaPenerima;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public int getKodePos() {
        return kodePos;
    }

    public void setKodePos(int kodePos) {
        this.kodePos = kodePos;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getStatusPengiriman() {
        return statusPengiriman;
    }

    public void setStatusPengiriman(String statusPengiriman) {
        this.statusPengiriman = statusPengiriman;
    }

    public BigDecimal getBiayaPengiriman() {
        return biayaPengiriman;
    }

    public void setBiayaPengiriman(BigDecimal biayaPengiriman) {
        this.biayaPengiriman = biayaPengiriman;
    }

    public Timestamp getTanggalPengiriman() {
        return tanggalPengiriman;
    }

    public void setTanggalPengiriman(Timestamp tanggalPengiriman) {
        this.tanggalPengiriman = tanggalPengiriman;
    }
}
