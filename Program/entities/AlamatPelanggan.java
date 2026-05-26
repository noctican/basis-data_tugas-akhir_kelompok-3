package entities;

public class AlamatPelanggan {
    private int idPengguna;
    private int idAlamat;
    private String provinsi;
    private String kota;
    private String jalan;
    private String namaPenerima;
    private String noTelp;

    public AlamatPelanggan() {}

    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int id) { this.idPengguna = id; }
    public int getIdAlamat() { return idAlamat; }
    public void setIdAlamat(int id) { this.idAlamat = id; }
    public String getProvinsi() { return provinsi; }
    public void setProvinsi(String p) { this.provinsi = p; }
    public String getKota() { return kota; }
    public void setKota(String k) { this.kota = k; }
    public String getJalan() { return jalan; }
    public void setJalan(String j) { this.jalan = j; }
    public String getNamaPenerima() { return namaPenerima; }
    public void setNamaPenerima(String n) { this.namaPenerima = n; }
    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String n) { this.noTelp = n; }
}
