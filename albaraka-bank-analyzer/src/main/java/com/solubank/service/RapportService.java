package com.solubank.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.solubank.dao.ClientDAO;
import com.solubank.dao.CompteDAO;
import com.solubank.dao.TransactionDAO;
import com.solubank.entity.Client;
import com.solubank.entity.Compte;
import com.solubank.entity.Transaction;
import com.solubank.entity.enums.TypeTransaction;

public class RapportService {

    private final ClientDAO clientDAO;
    private final CompteDAO compteDAO;
    private final TransactionDAO transactionDAO;

    public RapportService(ClientDAO clientDAO, CompteDAO compteDAO, TransactionDAO transactionDAO) {
        this.clientDAO = clientDAO;
        this.compteDAO = compteDAO;
        this.transactionDAO = transactionDAO;
    }

    public List<Map.Entry<Client, Double>> getTop5ClientsParSolde() {
        
        Map<Long, Double> totalSoldeParCompte = compteDAO.findAll().stream().collect(Collectors.groupingBy(
                Compte::getIdClient, Collectors.summingDouble(Compte::getSolde)));

        return clientDAO.findAll().stream().map(client -> Map.entry(
                client, totalSoldeParCompte.getOrDefault(client.id(), 0.0)))
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())).limit(5).toList();
    }

    public Map<TypeTransaction, Double> getRapportMensuelVolume(int annee, int mois) {
        return transactionDAO.findAll().stream()
                .filter(t -> t.date().getYear() == annee && t.date().getMonthValue() == mois)
                .collect(Collectors.groupingBy(
                        Transaction::type,
                        Collectors.summingDouble(Transaction::montant)));
    }

    public List<Transaction> detecterMontantsSuspects(Double seuil) {
        return transactionDAO.findAll().stream()
                .filter(t -> t.montant() > seuil)
                .toList();
    }

    public List<Transaction> detecterLieuxInhabituels(Long compteId, String lieuHabituel) {
        return transactionDAO.findByCompteId(compteId).stream()
                .filter(t -> !t.lieu().equalsIgnoreCase(lieuHabituel))
                .toList();
    }

    public List<Transaction> detecterFrequenceExcessive(Long compteId) {
        List<Transaction> transactions = transactionDAO.findByCompteId(compteId).stream()
                .sorted(Comparator.comparing(Transaction::date))
                .toList();

        Set<Transaction> suspectes = new HashSet<>();

        for (int i = 0; i < transactions.size() - 1; i++) {
            Transaction t1 = transactions.get(i);
            Transaction t2 = transactions.get(i + 1);

            long diffSecondes = Math.abs(Duration.between(t1.date(), t2.date()).getSeconds());
            if (diffSecondes < 60) {
                suspectes.add(t1);
                suspectes.add(t2);
            }
        }

        return suspectes.stream()
                .sorted(Comparator.comparing(Transaction::date).reversed())
                .toList();
    }

    public List<Compte> identifierComptesInactifs(int moisInactivite) {
        LocalDateTime dateLimite = LocalDateTime.now().minusMonths(moisInactivite);

        return compteDAO.findAll().stream()
                .filter(compte -> {
                    List<Transaction> transactions = transactionDAO.findByCompteId(compte.getId());
                    if (transactions.isEmpty()) {
                        return true;
                    }
                    Transaction derniereTransaction = transactions.stream()
                            .max(Comparator.comparing(Transaction::date))
                            .orElseThrow();
                    return derniereTransaction.date().isBefore(dateLimite);
                })
                .toList();
    }
}