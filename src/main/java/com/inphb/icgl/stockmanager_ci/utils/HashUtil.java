package com.inphb.icgl.stockmanager_ci.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//Utilitaire de hashage SHA-256 des mots de passe.

public class HashUtil {

    private HashUtil() {}

    //Hash un mot de passe en SHA-256 (cryptage) et retourne la représentation
    //hexadécimale en minuscules (64 caractères), identique à MySQL SHA2().

    public static String sha256(String motDePasse) {
        try {
            //Créer un objet capable de calculer une empreinte cryptographique en utilisant l'algorithme SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Convertir le mot de passe (String) en tableau d'octets (byte[]) en encodage UTF-8
            byte[] hashBytes = digest.digest(motDePasse.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                //%02x formate chaque octet en 2 caractères hexadécimaux
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 est toujours disponible dans le JDK standard
            throw new RuntimeException("SHA-256 non disponible", e);
        }
    }
}
