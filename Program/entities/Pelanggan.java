package entities;

import java.sql.Date;

public class Pelanggan {
    private int idPengguna;
    private String jenisMember;
    private Date tanggalBergabung;
    private int poin;
    private boolean isUsedVoucherPercentage;
    private boolean isUsedVoucherPrice;
    private String namaLengkap; // Helper

    public Pelanggan() {}

    public Pelanggan(int idPengguna, String jenisMember, Date tanggalBergabung, int poin, boolean isUsedVoucherPercentage, boolean isUsedVoucherPrice) {
        this.idPengguna = idPengguna;
        this.jenisMember = jenisMember;
        this.tanggalBergabung = tanggalBergabung;
        this.poin = poin;
        this.isUsedVoucherPercentage = isUsedVoucherPercentage;
        this.isUsedVoucherPrice = isUsedVoucherPrice;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getJenisMember() {
        return jenisMember;
    }

    public void setJenisMember(String jenisMember) {
        this.jenisMember = jenisMember;
    }

    public Date getTanggalBergabung() {
        return tanggalBergabung;
    }

    public void setTanggalBergabung(Date tanggalBergabung) {
        this.tanggalBergabung = tanggalBergabung;
    }

    public int getPoin() {
        return poin;
    }

    public void setPoin(int poin) {
        this.poin = poin;
    }

    public boolean isUsedVoucherPercentage() {
        return isUsedVoucherPercentage;
    }

    public void setUsedVoucherPercentage(boolean usedVoucherPercentage) {
        isUsedVoucherPercentage = usedVoucherPercentage;
    }

    public boolean isUsedVoucherPrice() {
        return isUsedVoucherPrice;
    }

    public void setUsedVoucherPrice(boolean usedVoucherPrice) {
        isUsedVoucherPrice = usedVoucherPrice;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    @Override
    public String toString() {
        return namaLengkap != null ? namaLengkap + " (" + jenisMember + ")" : String.valueOf(idPengguna);
    }
}
