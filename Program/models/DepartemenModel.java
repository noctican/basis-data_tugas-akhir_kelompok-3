package models;

import config.Database;
import entities.Departemen;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartemenModel {
    public List<Departemen> getAll() throws SQLException {
        List<Departemen> list = new ArrayList<>();
        String sql = "SELECT * FROM departemen";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Departemen(
                    rs.getInt("id_departemen"),
                    (Integer) rs.getObject("id_manajer"),
                    rs.getString("nama_departemen"),
                    rs.getString("lokasi"),
                    rs.getString("deskripsi_tugas")
                ));
            }
        }
        return list;
    }

    public void insert(Departemen d) throws SQLException {
        String sql = "INSERT INTO departemen (id_manajer, nama_departemen, lokasi, deskripsi_tugas) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (d.getIdManajer() == null) pstmt.setNull(1, Types.INTEGER);
            else pstmt.setInt(1, d.getIdManajer());
            pstmt.setString(2, d.getNamaDepartemen());
            pstmt.setString(3, d.getLokasi());
            pstmt.setString(4, d.getDeskripsiTugas());
            pstmt.executeUpdate();
        }
    }

    public void update(Departemen d) throws SQLException {
        String sql = "UPDATE departemen SET id_manajer = ?, nama_departemen = ?, lokasi = ?, deskripsi_tugas = ? WHERE id_departemen = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (d.getIdManajer() == null) pstmt.setNull(1, Types.INTEGER);
            else pstmt.setInt(1, d.getIdManajer());
            pstmt.setString(2, d.getNamaDepartemen());
            pstmt.setString(3, d.getLokasi());
            pstmt.setString(4, d.getDeskripsiTugas());
            pstmt.setInt(5, d.getIdDepartemen());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM departemen WHERE id_departemen = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
