package entities;

public class Karyawan {
    private int idPengguna;
    private int idDepartemen;
    private String jabatan;

    public Karyawan() {}

    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int id) { this.idPengguna = id; }
    public int getIdDepartemen() { return idDepartemen; }
    public void setIdDepartemen(int id) { this.idDepartemen = id; }
    public String getJabatan() { return jabatan; }
    public void setJabatan(String j) { this.jabatan = j; }
}
