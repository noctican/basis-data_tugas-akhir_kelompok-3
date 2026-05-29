package entities;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class RiwayatTopup {
    private int idPengguna;
    private int idTopup;
    private Timestamp tanggalTopup;
    private BigDecimal nominal;
    private String status;

    public RiwayatTopup() {}

    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int id) { this.idPengguna = id; }
    public int getIdTopup() { return idTopup; }
    public void setIdTopup(int id) { this.idTopup = id; }
    public Timestamp getTanggalTopup() { return tanggalTopup; }
    public void setTanggalTopup(Timestamp t) { this.tanggalTopup = t; }
    public BigDecimal getNominal() { return nominal; }
    public void setNominal(BigDecimal n) { this.nominal = n; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
}
