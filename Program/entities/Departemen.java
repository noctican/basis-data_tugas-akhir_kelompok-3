package entities;

public class Departemen {
    private int idDepartemen;
    private Integer idManajer; // Can be null
    private String namaDepartemen;
    private String lokasi;
    private String deskripsiTugas;

    public Departemen() {}

    public Departemen(int idDepartemen, Integer idManajer, String namaDepartemen, String lokasi, String deskripsiTugas) {
        this.idDepartemen = idDepartemen;
        this.idManajer = idManajer;
        this.namaDepartemen = namaDepartemen;
        this.lokasi = lokasi;
        this.deskripsiTugas = deskripsiTugas;
    }

    public int getIdDepartemen() {
        return idDepartemen;
    }

    public void setIdDepartemen(int idDepartemen) {
        this.idDepartemen = idDepartemen;
    }

    public Integer getIdManajer() {
        return idManajer;
    }

    public void setIdManajer(Integer idManajer) {
        this.idManajer = idManajer;
    }

    public String getNamaDepartemen() {
        return namaDepartemen;
    }

    public void setNamaDepartemen(String namaDepartemen) {
        this.namaDepartemen = namaDepartemen;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getDeskripsiTugas() {
        return deskripsiTugas;
    }

    public void setDeskripsiTugas(String deskripsiTugas) {
        this.deskripsiTugas = deskripsiTugas;
    }

    @Override
    public String toString() {
        return namaDepartemen;
    }
}
