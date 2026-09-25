package com.solubank;

import java.sql.Connection;

import com.solubank.util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println(" Connexion réussie à la base de données ");
            }
        } catch (Exception e) {
            System.err.println(" Échec de la connexion : " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}