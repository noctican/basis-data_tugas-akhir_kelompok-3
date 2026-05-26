package entities;

public class SubKategori {
    private int idSubKategori;
    private int idKategori;
    private String namaSubKategori;

    public SubKategori() {}
    public SubKategori(int idSub, int idKat, String nama) {
        this.idSubKategori = idSub;
        this.idKategori = idKat;
        this.namaSubKategori = nama;
    }

    public int getIdSubKategori() { return idSubKategori; }
    public void setIdSubKategori(int id) { this.idSubKategori = id; }
    public int getIdKategori() { return idKategori; }
    public void setIdKategori(int id) { this.idKategori = id; }
    public String getNamaSubKategori() { return namaSubKategori; }
    public void setNamaSubKategori(String nama) { this.namaSubKategori = nama; }

    @Override
    public String toString() { return namaSubKategori; }
}
