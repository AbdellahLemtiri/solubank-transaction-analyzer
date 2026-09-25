package com.solubank.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private static Connection connection;

    private static final String CONFIG_FILE = "application.properties";

    private DatabaseConnection() {

    }

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties props = loadProperties();

                Class.forName(props.getProperty("db.driver"));

                connection = DriverManager.getConnection(
                        props.getProperty("db.url"),
                        props.getProperty("db.user"),
                        props.getProperty("db.password"));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC introuvable.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'accès à la base de données : " + e.getMessage(), e);
        }

        return connection;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Fichier de configuration introuvable dans le classpath : " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger le fichier " + CONFIG_FILE, e);
        }
        return properties;
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}