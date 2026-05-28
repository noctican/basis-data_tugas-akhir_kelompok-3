package models;

import config.DatabaseConfig;
import entities.*;
import helpers.DBHelper;
import helpers.GUIHelper;
import models.PaymentModel.HasilPembayaran;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class UserModel {
    
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

    public Karyawan getKaryawanById (int idPengguna) {
        String sql = "SELECT * FROM karyawan WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPengguna);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Karyawan k = new Karyawan();
                    k.setIdPengguna(idPengguna);
                    k.setIdDepartemen(rs.getInt("id_departemen"));
                    k.setJabatan(rs.getString("jabatan"));
                    return k;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Departemen getDepartemenById(int idDepartemen) {
        String sql = "SELECT * FROM departemen WHERE id_departemen = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idDepartemen);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Departemen d = new Departemen();
                    d.setIdDepartemen(idDepartemen);
                    d.setIdManajer((Integer) rs.getObject("id_manajer"));
                    d.setNamaDepartemen(rs.getString("nama_departemen"));
                    d.setLokasi(rs.getString("lokasi"));
                    d.setDeskripsiTugas(rs.getString("deskripsi_tugas"));
                    return d;
                }    
            }
        } catch (SQLException e) { e.printStackTrace(); } 
        return null; 
    }

    public boolean updateDepartemen(Departemen d) {
        String sql = "UPDATE departemen SET nama_departemen = ?, lokasi = ?, deskripsi_tugas = ?, id_manajer = ? WHERE id_departemen = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, d.getNamaDepartemen());
            pstmt.setString(2, d.getLokasi());
            pstmt.setString(3, d.getDeskripsiTugas());
            if (d.getIdManajer() != null) {
                pstmt.setInt(4, d.getIdManajer());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setInt(5, d.getIdDepartemen());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public Pengguna getPenggunaById(int idPengguna) {
        String sql = "SELECT * FROM pengguna WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPengguna);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Pengguna p = new Pengguna();
                    p.setIdPengguna(idPengguna);
                    p.setEmail(rs.getString("email"));
                    p.setNamaDepan(rs.getString("nama_depan"));
                    p.setNamaBelakang(rs.getString("nama_belakang"));
                    p.setPassword(rs.getString("password"));
                    p.setNomorTelepon(rs.getString("nomor_telepon"));
                    return p;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;    
    }

    public Pelanggan getPelangganById(int idPengguna) {
        String sql = "SELECT * FROM pelanggan WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPengguna);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Pelanggan p = new Pelanggan();
                    p.setIdPengguna(idPengguna);
                    p.setJenisMember(rs.getString("jenis_member"));
                    p.setTanggalBergabung(rs.getDate("tanggal_bergabung"));
                    p.setPoin(rs.getInt("poin"));
                    p.setUsedVoucherPercentage(rs.getBoolean("is_used_voucher_percentage"));
                    p.setUsedVoucherPrice(rs.getBoolean("is_used_voucher_price"));
                    return p;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Karyawan> getKaryawanByDepartemen(int idDepartemen) {
        List<Karyawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan WHERE id_departemen = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idDepartemen);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Karyawan k = new Karyawan();
                    k.setIdPengguna(rs.getInt("id_pengguna"));
                    k.setIdDepartemen(idDepartemen);
                    k.setJabatan(rs.getString("jabatan"));
                    list.add(k);
                }
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

    public boolean addEmployee(Pengguna p, int idDept, String jabatan) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

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

    public boolean updateEmployee(Pengguna p, Karyawan k) {
        String sqlPengguna = "UPDATE pengguna SET email = ?, nama_depan = ?, nama_belakang = ?, password = ?, nomor_telepon = ? WHERE id_pengguna = ?";
        String sqlKaryawan = "UPDATE karyawan SET jabatan = ? WHERE id_pengguna = ?";
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtP = conn.prepareStatement(sqlPengguna)) {
                stmtP.setString(1, p.getEmail());
                stmtP.setString(2, p.getNamaDepan());
                stmtP.setString(3, p.getNamaBelakang());
                stmtP.setString(4, p.getPassword());
                stmtP.setString(5, p.getNomorTelepon());
                stmtP.setInt(6, p.getIdPengguna());
                stmtP.executeUpdate();
            }
            try (PreparedStatement stmtK = conn.prepareStatement(sqlKaryawan)) {
                stmtK.setString(1, k.getJabatan());
                stmtK.setInt(2, k.getIdPengguna());
                stmtK.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException ex) {
            DBHelper.rollback(conn);
            ex.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    public boolean deleteEmployee(int idPengguna) {
        String sqlKaryawan = "DELETE FROM karyawan WHERE id_pengguna = ?";
        String sqlPengguna = "DELETE FROM pengguna WHERE id_pengguna = ?";
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtK = conn.prepareStatement(sqlKaryawan)) {
                stmtK.setInt(1, idPengguna);
                stmtK.executeUpdate();
            }
            try (PreparedStatement stmtP = conn.prepareStatement(sqlPengguna)) {
                stmtP.setInt(1, idPengguna);
                stmtP.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException ex) {
            DBHelper.rollback(conn);
            ex.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

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

    public boolean addMember(Member m) {
        String sql = "INSERT INTO member (jenis, poin, voucher_price, voucher_percentage) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getJenis());
            pstmt.setInt(2, m.getPoin());
            pstmt.setBigDecimal(3, m.getVoucherPrice());
            pstmt.setBigDecimal(4, m.getVoucherPercentage());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateMember(Member m) {
        String sql = "UPDATE member SET poin = ?, voucher_price = ?, voucher_percentage = ? WHERE jenis = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, m.getPoin());
            pstmt.setBigDecimal(2, m.getVoucherPrice());
            pstmt.setBigDecimal(3, m.getVoucherPercentage());
            pstmt.setString(4, m.getJenis());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteMember(String jenis) {
        String sql = "DELETE FROM member WHERE jenis = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jenis);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Object[]> getAllCustomers() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.*, pl.jenis_member FROM pengguna p JOIN pelanggan pl ON p.id_pengguna = pl.id_pengguna";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pengguna p = new Pengguna();
                p.setIdPengguna(rs.getInt("id_pengguna"));
                p.setEmail(rs.getString("email"));
                p.setNamaDepan(rs.getString("nama_depan"));
                p.setNamaBelakang(rs.getString("nama_belakang"));
                p.setPassword(rs.getString("password"));
                p.setNomorTelepon(rs.getString("nomor_telepon"));
                
                String jenisMember = rs.getString("jenis_member");
                
                list.add(new Object[]{p, jenisMember});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addCustomer(Pengguna p, String jenisMember) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

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

    public boolean updateCustomer(Pengguna p, String jenisMember) {
        String sqlPengguna = "UPDATE pengguna SET email = ?, nama_depan = ?, nama_belakang = ?, password = ?, nomor_telepon = ? WHERE id_pengguna = ?";
        String sqlPelanggan = "UPDATE pelanggan SET jenis_member = ? WHERE id_pengguna = ?";
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtP = conn.prepareStatement(sqlPengguna)) {
                stmtP.setString(1, p.getEmail());
                stmtP.setString(2, p.getNamaDepan());
                stmtP.setString(3, p.getNamaBelakang());
                stmtP.setString(4, p.getPassword());
                stmtP.setString(5, p.getNomorTelepon());
                stmtP.setInt(6, p.getIdPengguna());
                stmtP.executeUpdate();
            }
            try (PreparedStatement stmtC = conn.prepareStatement(sqlPelanggan)) {
                stmtC.setString(1, jenisMember);
                stmtC.setInt(2, p.getIdPengguna());
                stmtC.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException ex) {
            DBHelper.rollback(conn);
            ex.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    public boolean deleteCustomer(int idPengguna) {
        String sqlPelanggan = "DELETE FROM pelanggan WHERE id_pengguna = ?";
        String sqlPengguna = "DELETE FROM pengguna WHERE id_pengguna = ?";
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtC = conn.prepareStatement(sqlPelanggan)) {
                stmtC.setInt(1, idPengguna);
                stmtC.executeUpdate();
            }
            try (PreparedStatement stmtP = conn.prepareStatement(sqlPengguna)) {
                stmtP.setInt(1, idPengguna);
                stmtP.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException ex) {
            DBHelper.rollback(conn);
            ex.printStackTrace();
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

    public boolean updateAlamat(int idAlamat, String provinsi, String kota, String jalan, String namaPenerima, String noTelp) {
        String sql = "UPDATE alamat_pelanggan SET provinsi = ?, kota = ?, jalan = ?, nama_penerima = ?, no_telp = ? WHERE id_alamat = ?";
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, provinsi);
            ps.setString(2, kota);
            ps.setString(3, jalan);
            ps.setString(4, namaPenerima);
            ps.setString(5, noTelp);
            ps.setInt(6, idAlamat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAlamat(int idAlamat) {
        String sql = "DELETE FROM alamat_pelanggan WHERE id_alamat = ?";
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAlamat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addAlamat(int idPengguna, String provinsi, String kota, String jalan, String namaPenerima, String noTelp) {
        String sql = "INSERT INTO alamat_pelanggan (id_pengguna, provinsi, kota, jalan, nama_penerima, no_telp) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPengguna);
            ps.setString(2, provinsi);
            ps.setString(3, kota);
            ps.setString(4, jalan);
            ps.setString(5, namaPenerima);
            ps.setString(6, noTelp);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfile(Pengguna p) {
        String sql = "UPDATE pengguna SET nama_depan = ?, nama_belakang = ?, nomor_telepon = ? WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNamaDepan());
            pstmt.setString(2, p.getNamaBelakang());
            pstmt.setString(3, p.getNomorTelepon());
            pstmt.setInt(4, p.getIdPengguna());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updatePassword(int idPengguna, String oldPass, String newPass) {
        String sql = "UPDATE pengguna SET password = ? WHERE id_pengguna = ? AND password = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPass);
            pstmt.setInt(2, idPengguna);
            pstmt.setString(3, oldPass);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<RiwayatTopup> getTopupHistory(int idCustomer) {
        List<RiwayatTopup> list = new ArrayList<>();
        String sql = "SELECT * FROM riwayat_topup WHERE id_pengguna = ? ORDER BY tanggal_topup DESC";
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCustomer);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RiwayatTopup r = new RiwayatTopup();
                    r.setIdPengguna(rs.getInt("id_pengguna"));
                    r.setIdTopup(rs.getInt("id_topup"));
                    r.setTanggalTopup(rs.getTimestamp("tanggal_topup"));
                    r.setNominal(rs.getBigDecimal("nominal"));
                    r.setStatus(rs.getString("status"));
                    list.add(r);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addTopup(int idCustomer, BigDecimal nominal) {
        String sqlRT = "INSERT INTO riwayat_topup (id_pengguna, nominal, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlRT)) {
            pstmt.setInt(1, idCustomer);
            pstmt.setBigDecimal(2, nominal);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<RiwayatTopup> getAllTopupsFiltered(String statusFilter) {
        List<RiwayatTopup> list = new ArrayList<>();
        String sql = "SELECT * FROM riwayat_topup";
        if (statusFilter != null && !statusFilter.equals("ALL")) {
            sql += " WHERE status = ?";
        }
        sql += " ORDER BY tanggal_topup ASC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (statusFilter != null && !statusFilter.equals("ALL")) {
                pstmt.setString(1, statusFilter);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RiwayatTopup r = new RiwayatTopup();
                    r.setIdPengguna(rs.getInt("id_pengguna"));
                    r.setIdTopup(rs.getInt("id_topup"));
                    r.setTanggalTopup(rs.getTimestamp("tanggal_topup"));
                    r.setNominal(rs.getBigDecimal("nominal"));
                    r.setStatus(rs.getString("status"));
                    list.add(r);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean processTopup(int idTopup, boolean isApproved) {
        String sql = "{CALL sp_ResponseTopup(?, ?, ?)}";

        try (Connection conn = DatabaseConfig.getConnection();
            CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idTopup);
            cs.setBoolean(2, isApproved);
            cs.registerOutParameter(3, Types.NVARCHAR);

            cs.execute();

            String pesan = cs.getString(3);
            boolean sukses = pesan != null && pesan.startsWith("SUCCESS");

            if(sukses) GUIHelper.showInfo(null, pesan);
            else GUIHelper.showError(null, pesan);

            return sukses;
        } catch (SQLException e) {
            e.printStackTrace();
            GUIHelper.showError(null, "ERROR: " + e.getMessage());
            return false;
        }
    }
}