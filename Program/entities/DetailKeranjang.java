package entities;

import java.math.BigDecimal;

public class DetailKeranjang {
    private int idKeranjang;
    private int idProduk;
    private String sku;
    private int kuantitas;
    private BigDecimal subTotal;

    public DetailKeranjang() {}

    public DetailKeranjang(int idKeranjang, int idProduk, String sku, int kuantitas, BigDecimal subTotal) {
        this.idKeranjang = idKeranjang;
        this.idProduk = idProduk;
        this.sku = sku;
        this.kuantitas = kuantitas;
        this.subTotal = subTotal;
    }

    public int getIdKeranjang() {
        return idKeranjang;
    }

    public void setIdKeranjang(int idKeranjang) {
        this.idKeranjang = idKeranjang;
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

    public int getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}
