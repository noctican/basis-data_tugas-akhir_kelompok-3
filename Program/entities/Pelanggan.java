package entities;

import java.sql.Date;

public class Pelanggan {
    private int idPengguna;
    private String jenisMember;
    private Date tanggalBergabung;
    private int poin;
    private boolean isUsedVoucherPercentage;
    private boolean isUsedVoucherPrice;

    public Pelanggan() {}

    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int id) { this.idPengguna = id; }
    public String getJenisMember() { return jenisMember; }
    public void setJenisMember(String j) { this.jenisMember = j; }
    public Date getTanggalBergabung() { return tanggalBergabung; }
    public void setTanggalBergabung(Date d) { this.tanggalBergabung = d; }
    public int getPoin() { return poin; }
    public void setPoin(int p) { this.poin = p; }
    public boolean isUsedVoucherPercentage() { return isUsedVoucherPercentage; }
    public void setUsedVoucherPercentage(boolean u) { this.isUsedVoucherPercentage = u; }
    public boolean isUsedVoucherPrice() { return isUsedVoucherPrice; }
    public void setUsedVoucherPrice(boolean u) { this.isUsedVoucherPrice = u; }
}
