package entities;

import java.math.BigDecimal;

public class DetailTransaksi {
    private int idProduk;
    private String sku;
    private int idTransaksi;
    private BigDecimal hargaPembelian;
    private int kuantitas;

    public DetailTransaksi() {}
    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int id) { this.idProduk = id; }
    public String getSku() { return sku; }
    public void setSku(String s) { this.sku = s; }
    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int id) { this.idTransaksi = id; }
    public BigDecimal getHargaPembelian() { return hargaPembelian; }
    public void setHargaPembelian(BigDecimal h) { this.hargaPembelian = h; }
    public int getKuantitas() { return kuantitas; }
    public void setKuantitas(int k) { this.kuantitas = k; }
}
