package entities;

public class PelangganTransaksi {
    private int idPengguna;
    private int idTransaksi;
    private String namaPelanggan; // Helper

    public PelangganTransaksi() {}

    public PelangganTransaksi(int idPengguna, int idTransaksi) {
        this.idPengguna = idPengguna;
        this.idTransaksi = idTransaksi;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }
}
