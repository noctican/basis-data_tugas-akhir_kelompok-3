package entities;

import java.math.BigDecimal;

public class VarianProduk {
    private int idProduk;
    private String sku;
    private int stock;
    private String warna;
    private BigDecimal hargaVarian;
    private String ukuran;
    private String namaProduk; // Helper

    public VarianProduk() {}

    public VarianProduk(int idProduk, String sku, int stock, String warna, BigDecimal hargaVarian, String ukuran) {
        this.idProduk = idProduk;
        this.sku = sku;
        this.stock = stock;
        this.warna = warna;
        this.hargaVarian = hargaVarian;
        this.ukuran = ukuran;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(int idProduk) {
        this.idProduk = idProduk;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }

    public BigDecimal getHargaVarian() {
        return hargaVarian;
    }

    public void setHargaVarian(BigDecimal hargaVarian) {
        this.hargaVarian = hargaVarian;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    @Override
    public String toString() {
        return sku + " (" + warna + ", " + ukuran + ")";
    }
}
