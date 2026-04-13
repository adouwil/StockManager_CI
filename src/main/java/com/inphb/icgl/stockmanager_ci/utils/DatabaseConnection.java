package com.inphb.icgl.stockmanager_ci.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Connexion JDBC à MySQL.

public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/stockmanager_ci";
    private static final String USER     = "root";
    private static final String PASSWORD = "";   // ← à adapter

    private static DatabaseConnection instance = null;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /*
      Retourne l'instance unique, en recréant la connexion si nécessaire.
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        // Reconnexion automatique si la connexion est perdue
        if (connection == null || connection.isClosed()) {
            //  Chargement explicite du driver
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL introuvable : " +
                        e.getMessage());
            }
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    // Fermer la connexion (appeler à la fermeture de l'application)
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Erreur fermeture : " + e.getMessage());
        }
    }
}
