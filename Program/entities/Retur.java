package entities;

import java.sql.Date;

public class Retur {
    private int idRetur;
    private Integer idTransaksi;
    private Date tanggalPengembalian;
    private String status;
    private Integer idValidator; // id_pengguna from karyawan

    public Retur() {}

    public Retur(int idRetur, Integer idTransaksi, Date tanggalPengembalian, String status, Integer idValidator) {
        this.idRetur = idRetur;
        this.idTransaksi = idTransaksi;
        this.tanggalPengembalian = tanggalPengembalian;
        this.status = status;
        this.idValidator = idValidator;
    }

    public int getIdRetur() {
        return idRetur;
    }

    public void setIdRetur(int idRetur) {
        this.idRetur = idRetur;
    }

    public Integer getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(Integer idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public Date getTanggalPengembalian() {
        return tanggalPengembalian;
    }

    public void setTanggalPengembalian(Date tanggalPengembalian) {
        this.tanggalPengembalian = tanggalPengembalian;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIdValidator() {
        return idValidator;
    }

    public void setIdValidator(Integer idValidator) {
        this.idValidator = idValidator;
    }

    @Override
    public String toString() {
        return "Retur #" + idRetur + " (" + status + ")";
    }
}
