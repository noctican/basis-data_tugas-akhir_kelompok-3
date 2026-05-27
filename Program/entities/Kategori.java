package entities;

public class Kategori {
    private int idKategori;
    private String namaKategori;

    public Kategori() {}
    public Kategori(int id, String nama) { this.idKategori = id; this.namaKategori = nama; }

    public int getIdKategori() { return idKategori; }
    public void setIdKategori(int id) { this.idKategori = id; }
    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String nama) { this.namaKategori = nama; }

    @Override
    public String toString() { return namaKategori; }
}
