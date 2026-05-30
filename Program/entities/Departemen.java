package entities;

public class Departemen {
    private int idDepartemen;
    private Integer idManajer;
    private String namaDepartemen;
    private String lokasi;
    private String deskripsiTugas;

    public Departemen() {}

    public int getIdDepartemen() { return idDepartemen; }
    public void setIdDepartemen(int id) { this.idDepartemen = id; }
    public Integer getIdManajer() { return idManajer; }
    public void setIdManajer(Integer id) { this.idManajer = id; }
    public String getNamaDepartemen() { return namaDepartemen; }
    public void setNamaDepartemen(String n) { this.namaDepartemen = n; }
    public String getLokasi() { return lokasi; }
    public void setLokasi(String l) { this.lokasi = l; }
    public String getDeskripsiTugas() { return deskripsiTugas; }
    public void setDeskripsiTugas(String d) { this.deskripsiTugas = d; }

    @Override
    public String toString() { return namaDepartemen; }
}
