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
    public String getNoResi() { return noResi; }
    public void setNoResi(String n) { this.noResi = n; }
    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int id) { this.idTransaksi = id; }
    public String getStatusPengiriman() { return statusPengiriman; }
    public void setStatusPengiriman(String s) { this.statusPengiriman = s; }
}
