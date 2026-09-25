package com.solubank.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.solubank.dao.CompteDAO;
import com.solubank.dao.TransactionDAO;
import com.solubank.entity.Compte;
import com.solubank.entity.CompteCourant;
import com.solubank.entity.CompteEpargne;
import com.solubank.entity.Transaction;
import com.solubank.entity.enums.TypeTransaction;
import com.solubank.exception.ResourceNotFoundException;
import com.solubank.exception.SoldeInsuffisantException;

public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final CompteDAO compteDAO;

    public TransactionService(TransactionDAO transactionDAO, CompteDAO compteDAO) {
        this.transactionDAO = transactionDAO;
        this.compteDAO = compteDAO;
    }

    public Transaction effectuerVersement(Long compteId, Double montant, String lieu) {
        Compte compte = compteDAO.findById(compteId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable : " + compteId));

        compte.setSolde(compte.getSolde() + montant);
        compteDAO.update(compte);

        var transaction = new Transaction(null, LocalDateTime.now(), montant, TypeTransaction.VERSEMENT, lieu, compteId);
        return transactionDAO.save(transaction);
    }

    public Transaction effectuerRetrait(Long compteId, Double montant, String lieu) {
        Compte compte = compteDAO.findById(compteId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable : " + compteId));

        validerSolvabilite(compte, montant);

        compte.setSolde(compte.getSolde() - montant);
        compteDAO.update(compte);

        var transaction = new Transaction(null, LocalDateTime.now(), montant, TypeTransaction.RETRAIT, lieu, compteId);
        return transactionDAO.save(transaction);
    }

    public void effectuerVirement(Long compteSourceId, Long compteDestId, Double montant, String lieu) {
        if (compteSourceId.equals(compteDestId)) {
            throw new IllegalArgumentException("Les comptes source et destination doivent être distincts.");
        }

        Compte source = compteDAO.findById(compteSourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte source introuvable : " + compteSourceId));
        Compte destination = compteDAO.findById(compteDestId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte destination introuvable : " + compteDestId));

        validerSolvabilite(source, montant);

        source.setSolde(source.getSolde() - montant);
        destination.setSolde(destination.getSolde() + montant);

        compteDAO.update(source);
        compteDAO.update(destination);

        var transSource = new Transaction(null, LocalDateTime.now(), montant, TypeTransaction.VIREMENT, lieu + " (Débit)", compteSourceId);
        var transDest = new Transaction(null, LocalDateTime.now(), montant, TypeTransaction.VIREMENT, lieu + " (Crédit)", compteDestId);

        transactionDAO.save(transSource);
        transactionDAO.save(transDest);
    }

    private void validerSolvabilite(Compte compte, Double montant) {
        if (compte instanceof CompteCourant cc) {
            if ((compte.getSolde() + cc.getDecouvertAutorise()) < montant) {
                throw new SoldeInsuffisantException("Fonds insuffisants en tenant compte du découvert autorisé.");
            }
        } else if (compte instanceof CompteEpargne) {
            if (compte.getSolde() < montant) {
                throw new SoldeInsuffisantException("Solde insuffisant : aucun découvert n'est permis sur un compte épargne.");
            }
        }
    }

    public List<Transaction> listerParCompteTriees(Long compteId) {
        return transactionDAO.findByCompteId(compteId).stream()
                .sorted(Comparator.comparing(Transaction::date).reversed())
                .toList();
    }

    public List<Transaction> filtrerTransactions(Predicate<Transaction> critere) {
        return transactionDAO.findAll().stream()
                .filter(critere)
                .toList();
    }

    public Map<TypeTransaction, List<Transaction>> regrouperParType() {
        return transactionDAO.findAll().stream()
                .collect(Collectors.groupingBy(Transaction::type));
    }

    public Double calculerTotalParCompte(Long compteId) {
        return transactionDAO.findByCompteId(compteId).stream()
                .mapToDouble(Transaction::montant)
                .sum();
    }

    public Double calculerMoyenneParCompte(Long compteId) {
        return transactionDAO.findByCompteId(compteId).stream()
                .mapToDouble(Transaction::montant)
                .average()
                .orElse(0.0);
    }
}