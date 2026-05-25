package models;

import config.Database;
import entities.Member;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberModel {
    public List<Member> getAll() throws SQLException {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM member";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Member(
                    rs.getString("jenis"),
                    rs.getInt("poin"),
                    rs.getBigDecimal("voucher_price"),
                    rs.getBigDecimal("voucher_percentage")
                ));
            }
        }
        return list;
    }

    public void insert(Member m) throws SQLException {
        String sql = "INSERT INTO member (jenis, poin, voucher_price, voucher_percentage) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getJenis());
            pstmt.setInt(2, m.getPoin());
            pstmt.setBigDecimal(3, m.getVoucherPrice());
            pstmt.setBigDecimal(4, m.getVoucherPercentage());
            pstmt.executeUpdate();
        }
    }

    public void update(Member m) throws SQLException {
        String sql = "UPDATE member SET poin = ?, voucher_price = ?, voucher_percentage = ? WHERE jenis = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, m.getPoin());
            pstmt.setBigDecimal(2, m.getVoucherPrice());
            pstmt.setBigDecimal(3, m.getVoucherPercentage());
            pstmt.setString(4, m.getJenis());
            pstmt.executeUpdate();
        }
    }

    public void delete(String jenis) throws SQLException {
        String sql = "DELETE FROM member WHERE jenis = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jenis);
            pstmt.executeUpdate();
        }
    }
}
