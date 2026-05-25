package entities;

public class SubKategori {
    private int idKategori;
    private int idSubKategori;
    private String namaSubKategori;
    private String namaKategori; // Helper for display

    public SubKategori() {}

    public SubKategori(int idKategori, int idSubKategori, String namaSubKategori) {
        this.idKategori = idKategori;
        this.idSubKategori = idSubKategori;
        this.namaSubKategori = namaSubKategori;
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

    public String getNamaSubKategori() {
        return namaSubKategori;
    }

    public void setNamaSubKategori(String namaSubKategori) {
        this.namaSubKategori = namaSubKategori;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    @Override
    public String toString() {
        return namaSubKategori;
    }
}
