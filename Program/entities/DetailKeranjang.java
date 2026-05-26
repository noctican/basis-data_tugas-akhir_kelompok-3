package entities;

import java.math.BigDecimal;

public class DetailKeranjang {
    private int idKeranjang;
    private int idProduk;
    private String sku;
    private int kuantitas;
    private BigDecimal subTotal;

    public DetailKeranjang() {}
    public int getIdKeranjang() { return idKeranjang; }
    public void setIdKeranjang(int id) { this.idKeranjang = id; }
    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int id) { this.idProduk = id; }
    public String getSku() { return sku; }
    public void setSku(String s) { this.sku = s; }
    public int getKuantitas() { return kuantitas; }
    public void setKuantitas(int k) { this.kuantitas = k; }
    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal s) { this.subTotal = s; }
}
