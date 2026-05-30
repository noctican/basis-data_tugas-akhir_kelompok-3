package entities;

import java.math.BigDecimal;

public class Produk {
    private int idProduk;
    private int idKategori;
    private int idSubKategori;
    private String namaProduk;
    private String deskripsiProduk;
    private BigDecimal hargaBase;
    private String demografi;
    private String aktivitas;

    public Produk() {}

    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int id) { this.idProduk = id; }
    public int getIdKategori() { return idKategori; }
    public void setIdKategori(int id) { this.idKategori = id; }
    public int getIdSubKategori() { return idSubKategori; }
    public void setIdSubKategori(int id) { this.idSubKategori = id; }
    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String nama) { this.namaProduk = nama; }
    public String getDeskripsiProduk() { return deskripsiProduk; }
    public void setDeskripsiProduk(String d) { this.deskripsiProduk = d; }
    public BigDecimal getHargaBase() { return hargaBase; }
    public void setHargaBase(BigDecimal h) { this.hargaBase = h; }
    public String getDemografi() { return demografi; }
    public void setDemografi(String d) { this.demografi = d; }
    public String getAktivitas() { return aktivitas; }
    public void setAktivitas(String a) { this.aktivitas = a; }

    @Override
    public String toString() { return namaProduk; }
}
