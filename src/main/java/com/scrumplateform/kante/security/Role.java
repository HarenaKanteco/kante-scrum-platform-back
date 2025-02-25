package com.scrumplateform.kante.security;

public enum Role {
    SCRUM,
    DEV,
    BACKEND,
    FRONTEND, 
    FULLSTACK, 
    DESIGNER;

    public static boolean isValidRole(String role) {
        try {
            Role.valueOf(role.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isDeveloperRole(Role role) {
        return role == DEV || role == BACKEND || role == FRONTEND || role == FULLSTACK;
    }
}
