package entities;

import java.sql.Date;

public class Retur {
    private int idRetur;
    private int idTransaksi;
    private Date tanggalPengembalian;
    private String status;
    private Integer idValidator;

    public Retur() {}
    public int getIdRetur() { return idRetur; }
    public void setIdRetur(int id) { this.idRetur = id; }
    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int id) { this.idTransaksi = id; }
    public Date getTanggalPengembalian() { return tanggalPengembalian; }
    public void setTanggalPengembalian(Date t) { this.tanggalPengembalian = t; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public Integer getIdValidator() { return idValidator; }
    public void setIdValidator(Integer id) { this.idValidator = id; }
}
