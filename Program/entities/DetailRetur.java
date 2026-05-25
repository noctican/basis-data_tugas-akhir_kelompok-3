package entities;

public class DetailRetur {
    private int idTransaksi;
    private int idRetur;
    private int idProduk;
    private String sku;
    private int kuantitas;
    private String alasan;

    public DetailRetur() {}

    public DetailRetur(int idTransaksi, int idRetur, int idProduk, String sku, int kuantitas, String alasan) {
        this.idTransaksi = idTransaksi;
        this.idRetur = idRetur;
        this.idProduk = idProduk;
        this.sku = sku;
        this.kuantitas = kuantitas;
        this.alasan = alasan;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public int getIdRetur() {
        return idRetur;
    }

    public void setIdRetur(int idRetur) {
        this.idRetur = idRetur;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(int idProduk) {
        this.idProduk = idProduk;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }
}
