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
    private String namaSubKategori; // Helper

    public Produk() {}

    public Produk(int idProduk, int idKategori, int idSubKategori, String namaProduk, String deskripsiProduk, BigDecimal hargaBase, String demografi, String aktivitas) {
        this.idProduk = idProduk;
        this.idKategori = idKategori;
        this.idSubKategori = idSubKategori;
        this.namaProduk = namaProduk;
        this.deskripsiProduk = deskripsiProduk;
        this.hargaBase = hargaBase;
        this.demografi = demografi;
        this.aktivitas = aktivitas;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(int idProduk) {
        this.idProduk = idProduk;
    }

    public int getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(int idKategori) {
        this.idKategori = idKategori;
    }

    public int getIdSubKategori() {
        return idSubKategori;
    }

    public void setIdSubKategori(int idSubKategori) {
        this.idSubKategori = idSubKategori;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public String getDeskripsiProduk() {
        return deskripsiProduk;
    }

    public void setDeskripsiProduk(String deskripsiProduk) {
        this.deskripsiProduk = deskripsiProduk;
    }

    public BigDecimal getHargaBase() {
        return hargaBase;
    }

    public void setHargaBase(BigDecimal hargaBase) {
        this.hargaBase = hargaBase;
    }

    public String getDemografi() {
        return demografi;
    }

    public void setDemografi(String demografi) {
        this.demografi = demografi;
    }

    public String getAktivitas() {
        return aktivitas;
    }

    public void setAktivitas(String aktivitas) {
        this.aktivitas = aktivitas;
    }

    public String getNamaSubKategori() {
        return namaSubKategori;
    }

    public void setNamaSubKategori(String namaSubKategori) {
        this.namaSubKategori = namaSubKategori;
    }

    @Override
    public String toString() {
        return namaProduk;
    }
}
