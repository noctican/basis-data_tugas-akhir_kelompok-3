package entities;

public class Keranjang {
    private int idKeranjang;
    private Integer idPengguna; // Unique per pelanggan
    private String namaPelanggan; // Helper

    public Keranjang() {}

    public Keranjang(int idKeranjang, Integer idPengguna) {
        this.idKeranjang = idKeranjang;
        this.idPengguna = idPengguna;
    }

    public int getIdKeranjang() {
        return idKeranjang;
    }

    public void setIdKeranjang(int idKeranjang) {
        this.idKeranjang = idKeranjang;
    }

    public Integer getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(Integer idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    @Override
    public String toString() {
        return "Keranjang #" + idKeranjang + " (" + (namaPelanggan != null ? namaPelanggan : idPengguna) + ")";
    }
}
