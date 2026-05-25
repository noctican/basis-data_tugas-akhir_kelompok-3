package entities;

public class Karyawan {
    private int idPengguna;
    private int idDepartemen;
    private String jabatan;
    private String namaLengkap; // Helper
    private String namaDepartemen; // Helper

    public Karyawan() {}

    public Karyawan(int idPengguna, int idDepartemen, String jabatan) {
        this.idPengguna = idPengguna;
        this.idDepartemen = idDepartemen;
        this.jabatan = jabatan;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public int getIdDepartemen() {
        return idDepartemen;
    }

    public void setIdDepartemen(int idDepartemen) {
        this.idDepartemen = idDepartemen;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getNamaDepartemen() {
        return namaDepartemen;
    }

    public void setNamaDepartemen(String namaDepartemen) {
        this.namaDepartemen = namaDepartemen;
    }

    @Override
    public String toString() {
        return namaLengkap != null ? namaLengkap + " (" + jabatan + ")" : String.valueOf(idPengguna);
    }
}
