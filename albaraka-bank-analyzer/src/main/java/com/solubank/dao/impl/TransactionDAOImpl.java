package com.solubank.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.solubank.dao.TransactionDAO;
import com.solubank.entity.Transaction;
import com.solubank.entity.enums.TypeTransaction;
import com.solubank.util.DatabaseConnection;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public Transaction save(Transaction transaction) {
        String sql = """
                    INSERT INTO "Transaction" (date, montant, type, lieu, id_compte)
                    VALUES (?, ?, ?, ?, ?) RETURNING id, date
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1,
                    Timestamp.valueOf(transaction.date() != null ? transaction.date() : LocalDateTime.now()));
            ps.setDouble(2, transaction.montant());
            ps.setString(3, transaction.type().name());
            ps.setString(4, transaction.lieu());
            ps.setLong(5, transaction.idCompte());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Long generatedId = rs.getLong("id");
                    LocalDateTime opDate = rs.getTimestamp("date").toLocalDateTime();
                    return new Transaction(
                            generatedId,
                            opDate,
                            transaction.montant(),
                            transaction.type(),
                            transaction.lieu(),
                            transaction.idCompte());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la transaction : " + e.getMessage(), e);
        }
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        String sql = "SELECT * FROM \"Transaction\" WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la transaction par ID : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findByCompteId(Long compteId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM \"Transaction\" WHERE id_compte = ? ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, compteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des transactions du compte : " + e.getMessage(),
                    e);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM \"Transaction\" ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les transactions : " + e.getMessage(),
                    e);
        }
        throw new RuntimeException("Erreur lors de la récupération de les transactions ! ");

    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getLong("id"),
                rs.getTimestamp("date").toLocalDateTime(),
                rs.getDouble("montant"),
                TypeTransaction.valueOf(rs.getString("type")),
                rs.getString("lieu"),
                rs.getLong("id_compte"));
    }
}