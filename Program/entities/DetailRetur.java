package entities;

public class DetailRetur {
    private int idTransaksi;
    private int idRetur;
    private int idProduk;
    private String sku;
    private int kuantitas;
    private String alasan;

    public DetailRetur() {}
    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int id) { this.idTransaksi = id; }
    public int getIdRetur() { return idRetur; }
    public void setIdRetur(int id) { this.idRetur = id; }
    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int id) { this.idProduk = id; }
    public String getSku() { return sku; }
    public void setSku(String s) { this.sku = s; }
    public int getKuantitas() { return kuantitas; }
    public void setKuantitas(int k) { this.kuantitas = k; }
    public String getAlasan() { return alasan; }
    public void setAlasan(String a) { this.alasan = a; }
}
