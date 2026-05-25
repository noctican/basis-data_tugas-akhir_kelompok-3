package entities;

public class Pengguna {
    private int idPengguna;
    private String email;
    private String namaDepan;
    private String namaBelakang;
    private String nomorTelepon;
    private String password;

    public Pengguna() {}

    public Pengguna(int idPengguna, String email, String namaDepan, String namaBelakang, String nomorTelepon, String password) {
        this.idPengguna = idPengguna;
        this.email = email;
        this.namaDepan = namaDepan;
        this.namaBelakang = namaBelakang;
        this.nomorTelepon = nomorTelepon;
        this.password = password;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNamaDepan() {
        return namaDepan;
    }

    public void setNamaDepan(String namaDepan) {
        this.namaDepan = namaDepan;
    }

    public String getNamaBelakang() {
        return namaBelakang;
    }

    public void setNamaBelakang(String namaBelakang) {
        this.namaBelakang = namaBelakang;
    }

    public String getNomorTelepon() {
        return nomorTelepon;
    }

    public void setNomorTelepon(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return namaDepan + " " + (namaBelakang != null ? namaBelakang : "") + " (" + email + ")";
    }
}
