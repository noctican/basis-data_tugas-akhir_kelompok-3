package models;

import config.DatabaseConfig;
import entities.*;
import helpers.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserModel {
    
    // --- Departemen CRUD ---
    public List<Departemen> getAllDepartemen() {
        List<Departemen> list = new ArrayList<>();
        String sql = "SELECT * FROM departemen";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Departemen d = new Departemen();
                d.setIdDepartemen(rs.getInt("id_departemen"));
                d.setIdManajer((Integer) rs.getObject("id_manajer"));
                d.setNamaDepartemen(rs.getString("nama_departemen"));
                d.setLokasi(rs.getString("lokasi"));
                d.setDeskripsiTugas(rs.getString("deskripsi_tugas"));
                list.add(d);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addDepartemen(Departemen d) {
        String sql = "INSERT INTO departemen (nama_departemen, lokasi, deskripsi_tugas) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, d.getNamaDepartemen());
            pstmt.setString(2, d.getLokasi());
            pstmt.setString(3, d.getDeskripsiTugas());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- Employee Transactional CRUD ---
    public boolean addEmployee(Pengguna p, int idDept, String jabatan) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Pengguna
            String sqlP = "INSERT INTO pengguna (email, nama_depan, nama_belakang, password, nomor_telepon) VALUES (?, ?, ?, ?, ?)";
            int idPengguna = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlP, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, p.getEmail());
                pstmt.setString(2, p.getNamaDepan());
                pstmt.setString(3, p.getNamaBelakang());
                pstmt.setString(4, p.getPassword());
                pstmt.setString(5, p.getNomorTelepon());
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) idPengguna = rs.getInt(1);
                }
            }

            // 2. Insert Karyawan
            String sqlK = "INSERT INTO karyawan (id_pengguna, id_departemen, jabatan) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlK)) {
                pstmt.setInt(1, idPengguna);
                pstmt.setInt(2, idDept);
                pstmt.setString(3, jabatan);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            DBHelper.rollback(conn);
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    // --- Member CRUD ---
    public List<Member> getAllMembers() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM member";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Member m = new Member();
                m.setJenis(rs.getString("jenis"));
                m.setPoin(rs.getInt("poin"));
                m.setVoucherPrice(rs.getBigDecimal("voucher_price"));
                m.setVoucherPercentage(rs.getBigDecimal("voucher_percentage"));
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- Customer Transactional CRUD ---
    public boolean addCustomer(Pengguna p, String jenisMember) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Pengguna
            String sqlP = "INSERT INTO pengguna (email, nama_depan, nama_belakang, password, nomor_telepon) VALUES (?, ?, ?, ?, ?)";
            int idPengguna = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlP, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, p.getEmail());
                pstmt.setString(2, p.getNamaDepan());
                pstmt.setString(3, p.getNamaBelakang());
                pstmt.setString(4, p.getPassword());
                pstmt.setString(5, p.getNomorTelepon());
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) idPengguna = rs.getInt(1);
                }
            }

            // 2. Insert Pelanggan
            String sqlC = "INSERT INTO pelanggan (id_pengguna, jenis_member) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlC)) {
                pstmt.setInt(1, idPengguna);
                pstmt.setString(2, jenisMember);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            DBHelper.rollback(conn);
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    public List<AlamatPelanggan> getAlamatByCustomer(int idCustomer) {
        List<AlamatPelanggan> list = new ArrayList<>();
        String sql = "SELECT * FROM alamat_pelanggan WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCustomer);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AlamatPelanggan a = new AlamatPelanggan();
                    a.setIdPengguna(rs.getInt("id_pengguna"));
                    a.setIdAlamat(rs.getInt("id_alamat"));
                    a.setProvinsi(rs.getString("provinsi"));
                    a.setKota(rs.getString("kota"));
                    a.setJalan(rs.getString("jalan"));
                    a.setNamaPenerima(rs.getString("nama_penerima"));
                    a.setNoTelp(rs.getString("no_telp"));
                    list.add(a);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
