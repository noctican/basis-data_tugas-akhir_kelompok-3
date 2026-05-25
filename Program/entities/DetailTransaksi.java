package entities;

import java.math.BigDecimal;

public class DetailTransaksi {
    private int idProduk;
    private String sku;
    private int idTransaksi;
    private BigDecimal hargaPembelian;
    private int kuantitas;

    public DetailTransaksi() {}

    public DetailTransaksi(int idProduk, String sku, int idTransaksi, BigDecimal hargaPembelian, int kuantitas) {
        this.idProduk = idProduk;
        this.sku = sku;
        this.idTransaksi = idTransaksi;
        this.hargaPembelian = hargaPembelian;
        this.kuantitas = kuantitas;
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

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public BigDecimal getHargaPembelian() {
        return hargaPembelian;
    }

    public void setHargaPembelian(BigDecimal hargaPembelian) {
        this.hargaPembelian = hargaPembelian;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }
}
