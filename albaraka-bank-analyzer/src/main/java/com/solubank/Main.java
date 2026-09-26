package com.solubank;

import com.solubank.dao.ClientDAO;
import com.solubank.dao.CompteDAO;
import com.solubank.dao.TransactionDAO;
import com.solubank.dao.impl.ClientDAOImpl;
import com.solubank.dao.impl.CompteDAOImpl;
import com.solubank.dao.impl.TransactionDAOImpl;
import com.solubank.service.ClientService;
import com.solubank.service.CompteService;
import com.solubank.service.RapportService;
import com.solubank.service.TransactionService;
import com.solubank.ui.ConsoleUI;
import com.solubank.util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        try {
            ClientDAO clientDAO = new ClientDAOImpl();
            CompteDAO compteDAO = new CompteDAOImpl();
            TransactionDAO transactionDAO = new TransactionDAOImpl();
            ClientService clientService = new ClientService(clientDAO, compteDAO);
            CompteService compteService = new CompteService(compteDAO, clientDAO);
            TransactionService transactionService = new TransactionService(transactionDAO, compteDAO);
            RapportService rapportService = new RapportService(clientDAO, compteDAO, transactionDAO);
            ConsoleUI ui = new ConsoleUI(clientService, compteService, transactionService, rapportService);
            ui.demarrer();
        } catch (Exception e) {
            System.err.println("Erreur critique au démarrage : " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}