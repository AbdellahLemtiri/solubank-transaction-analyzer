package com.solubank.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.solubank.dao.CompteDAO;
import com.solubank.entity.Compte;
import com.solubank.entity.CompteCourant;
import com.solubank.entity.CompteEpargne;
import com.solubank.entity.enums.TypeCompte;
import com.solubank.util.DatabaseConnection;

public class CompteDAOImpl implements CompteDAO {

    @Override
    public Compte save(Compte compte) {
        String sql = """
                    INSERT INTO "Compte" (numero, solde, type_compte, decouvert_autorise, taux_interet, id_client)
                    VALUES (?, ?, ?, ?, ?, ?) RETURNING id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, compte.getNumero());
            ps.setDouble(2, compte.getSolde());
            ps.setString(3, compte.getType().name());

            
            if (compte instanceof CompteCourant courant) {
                ps.setDouble(4, courant.getDecouvertAutorise());
                ps.setDouble(5, 0.0);
            } else if (compte instanceof CompteEpargne epargne) {
                ps.setDouble(4, 0.0);
                ps.setDouble(5, epargne.getTauxInteret());
            }

            ps.setLong(6, compte.getIdClient());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    compte.setId(rs.getLong("id"));
                    return compte;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du compte : " + e.getMessage(), e);
        }
        return compte;
    }

    @Override
    public boolean update(Compte compte) {
        String sql = """
                    UPDATE "Compte"
                    SET solde = ?, decouvert_autorise = ?, taux_interet = ?
                    WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, compte.getSolde());

            if (compte instanceof CompteCourant courant) {
                ps.setDouble(2, courant.getDecouvertAutorise());
                ps.setDouble(3, 0.0);
            } else if (compte instanceof CompteEpargne epargne) {
                ps.setDouble(2, 0.0);
                ps.setDouble(3, epargne.getTauxInteret());
            }

            ps.setLong(4, compte.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du compte : " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM \"Compte\" WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du compte : " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Compte> findById(Long id) {
        String sql = "SELECT * FROM \"Compte\" WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompte(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du compte par ID : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Compte> findByNumero(String numero) {
        String sql = "SELECT * FROM \"Compte\" WHERE numero = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompte(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du compte par numéro : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Compte> findByClientId(Long clientId) {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM \"Compte\" WHERE id_client = ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comptes.add(mapResultSetToCompte(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des comptes du client : " + e.getMessage(), e);
        }
        return comptes;
    }

    @Override
    public List<Compte> findAll() {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM \"Compte\" ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                comptes.add(mapResultSetToCompte(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les comptes : " + e.getMessage(), e);
        }
        return comptes;
    }

 
    private Compte mapResultSetToCompte(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String numero = rs.getString("numero");
        Double solde = rs.getDouble("solde");
        Long idClient = rs.getLong("id_client");
        TypeCompte type = TypeCompte.valueOf(rs.getString("type_compte"));

        return switch (type) {
            case COURANT -> new CompteCourant(id, numero, solde, idClient, rs.getDouble("decouvert_autorise"));
            case EPARGNE -> new CompteEpargne(id, numero, solde, idClient, rs.getDouble("taux_interet"));
        };
    }
}