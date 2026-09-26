package com.solubank.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.solubank.dao.ClientDAO;
import com.solubank.entity.Client;
import com.solubank.util.DatabaseConnection;

public class ClientDAOImpl implements ClientDAO {

    @Override
    public Client save(Client client) {
        String sql_request = "INSERT INTO \"Client\" (nom,email) VALUES (?,?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql_request)) {

            ps.setString(1, client.nom());
            ps.setString(2, client.email());

            try (ResultSet res = ps.executeQuery()) {
                if (res.next()) {
                    Long id = res.getLong("id");
                    return new Client(id, client.nom(), client.email());

                }
            }
            throw new RuntimeException("Client non enregistré !");
        } catch (SQLException se) {
            throw new RuntimeException("Client non enregistré !" + se);
        }
    }

    @Override
    public boolean update(Client client) {
        String sql_request = "UPDATE  \"Client\" SET nom = ?,email= ? where id = ? ";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql_request)) {
            ps.setString(1, client.nom());
            ps.setString(2, client.email());
            ps.setLong(3, client.id());
            return ps.executeUpdate() > 0;
        } catch (SQLException es) {
            throw new RuntimeException("information non modifie", es);
        }
    }

    @Override

    public boolean delete(Long id) {
        String sql_request = "DELETE FROM \"Client\" WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql_request)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException es) {
            throw new RuntimeException("Erreur lors de la suppression du client ", es);

        }
    }

    @Override
    public Optional<Client> findById(Long id) {
        String sql = "SELECT id, nom, email FROM \"Client\" WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClient(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du client par ID : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findByNom(String nom) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, nom, email FROM \"Client\" WHERE LOWER(nom) LIKE LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clients.add(mapResultSetToClient(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des clients par nom : " + e.getMessage(), e);
        }
        return clients;
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, nom, email FROM \"Client\" ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les clients : " + e.getMessage(), e);
        }
        return clients;
    }

    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        return new Client(
                rs.getLong("id"),
                rs.getString("nom"),
                rs.getString("email"));
    }
}
