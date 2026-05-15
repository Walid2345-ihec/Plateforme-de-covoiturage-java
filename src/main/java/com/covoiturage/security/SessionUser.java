package com.covoiturage.security;

import jakarta.servlet.http.HttpSession;

public final class SessionUser {
    private SessionUser() {
    }

    public static void login(HttpSession session, String cin, String role, String name) {
        session.setAttribute("cin", cin);
        session.setAttribute("role", role);
        session.setAttribute("name", name);
    }

    public static boolean isLogged(HttpSession session) {
        return session.getAttribute("cin") != null && session.getAttribute("role") != null;
    }

    public static boolean hasRole(HttpSession session, String role) {
        return role.equals(session.getAttribute("role"));
    }

    public static String cin(HttpSession session) {
        return (String) session.getAttribute("cin");
    }

    public static String role(HttpSession session) {
        return (String) session.getAttribute("role");
    }
}
