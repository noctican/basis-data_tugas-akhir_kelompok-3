package session;

import entities.Pengguna;

public class UserSession {
    public enum Role {
        KARYAWAN,
        PELANGGAN
    }

    private static Pengguna currentUser;
    private static Role role;

    public static void login(Pengguna user, Role userRole) {
        currentUser = user;
        role = userRole;
    }

    public static void logout() {
        currentUser = null;
        role = null;
    }

    public static Pengguna getCurrentUser() {
        return currentUser;
    }

    public static Role getRole() {
        return role;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
