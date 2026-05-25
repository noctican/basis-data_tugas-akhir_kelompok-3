package entities;

public class AlamatPelanggan {
    private int idPengguna;
    private int idAlamat;
    private String provinsi;
    private String kota;
    private String jalan;
    private String namaPenerima;
    private String noTelp;
    private String namaPelanggan; // Helper

    public AlamatPelanggan() {}

    public AlamatPelanggan(int idPengguna, int idAlamat, String provinsi, String kota, String jalan, String namaPenerima, String noTelp) {
        this.idPengguna = idPengguna;
        this.idAlamat = idAlamat;
        this.provinsi = provinsi;
        this.kota = kota;
        this.jalan = jalan;
        this.namaPenerima = namaPenerima;
        this.noTelp = noTelp;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public int getIdAlamat() {
        return idAlamat;
    }

    public void setIdAlamat(int idAlamat) {
        this.idAlamat = idAlamat;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getJalan() {
        return jalan;
    }

    public void setJalan(String jalan) {
        this.jalan = jalan;
    }

    public String getNamaPenerima() {
        return namaPenerima;
    }

    public void setNamaPenerima(String namaPenerima) {
        this.namaPenerima = namaPenerima;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    @Override
    public String toString() {
        return jalan + ", " + kota + " (Penerima: " + namaPenerima + ")";
    }
}
