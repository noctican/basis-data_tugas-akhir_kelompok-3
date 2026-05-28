package models;

import config.DatabaseConfig;
import entities.Pengguna;
import session.UserSession;
import java.sql.*;

public class AuthModel {
    public boolean login(String email, String password) {
        String query = "SELECT * FROM pengguna WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pengguna user = new Pengguna(
                        rs.getInt("id_pengguna"),
                        rs.getString("email"),
                        rs.getString("nama_depan"),
                        rs.getString("nama_belakang"),
                        rs.getString("nomor_telepon"),
                        rs.getString("password")
                    );
                    
                    UserSession.Role role = determineRole(user);
                    if (role != null) {
                        UserSession.login(user, role);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private UserSession.Role determineRole(Pengguna user) {
        String queryKaryawan = "SELECT 1 FROM karyawan WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryKaryawan)) {
            stmt.setInt(1, user.getIdPengguna());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return UserSession.Role.KARYAWAN;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String queryPelanggan = "SELECT 1 FROM pelanggan WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryPelanggan)) {
            stmt.setInt(1, user.getIdPengguna());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return UserSession.Role.PELANGGAN;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
