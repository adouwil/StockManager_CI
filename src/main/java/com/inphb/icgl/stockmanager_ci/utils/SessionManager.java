package com.inphb.icgl.stockmanager_ci.utils;

import com.inphb.icgl.stockmanager_ci.model.Utilisateur;

/**
 * Gestionnaire de session — stocke l'utilisateur connecté.
 * Accessible depuis tous les contrôleurs sans paramètre.
 */
public class SessionManager {

    private static Utilisateur utilisateurConnecte = null;

    private SessionManager() {}

    public static void setUtilisateur(Utilisateur u) {
        utilisateurConnecte = u;
    }

    public static Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    public static boolean isConnecte() {
        return utilisateurConnecte != null;
    }

    public static boolean isAdmin() {
        return utilisateurConnecte != null
            && "ADMIN".equals(utilisateurConnecte.getRole());
    }

    public static void logout() {
        utilisateurConnecte = null;
    }
}
