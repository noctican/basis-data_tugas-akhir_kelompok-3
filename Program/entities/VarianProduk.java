package entities;

import java.math.BigDecimal;

public class VarianProduk {
    private int idProduk;
    private String sku;
    private int stock;
    private String warna;
    private BigDecimal hargaVarian;
    private String ukuran;

    public VarianProduk() {}

    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int id) { this.idProduk = id; }
    public String getSku() { return sku; }
    public void setSku(String s) { this.sku = s; }
    public int getStock() { return stock; }
    public void setStock(int s) { this.stock = s; }
    public String getWarna() { return warna; }
    public void setWarna(String w) { this.warna = w; }
    public BigDecimal getHargaVarian() { return hargaVarian; }
    public void setHargaVarian(BigDecimal h) { this.hargaVarian = h; }
    public String getUkuran() { return ukuran; }
    public void setUkuran(String u) { this.ukuran = u; }

    @Override
    public String toString() { return warna + " (" + ukuran + ") - SKU: " + sku; }
}
