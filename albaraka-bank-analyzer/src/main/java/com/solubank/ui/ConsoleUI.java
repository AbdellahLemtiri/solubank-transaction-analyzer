package com.solubank.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.solubank.entity.Client;
import com.solubank.entity.Compte;
import com.solubank.entity.CompteCourant;
import com.solubank.entity.CompteEpargne;
import com.solubank.entity.Transaction;
import com.solubank.entity.enums.TypeTransaction;
import com.solubank.exception.ResourceNotFoundException;
import com.solubank.exception.SoldeInsuffisantException;
import com.solubank.service.ClientService;
import com.solubank.service.CompteService;
import com.solubank.service.RapportService;
import com.solubank.service.TransactionService;

public class ConsoleUI {

    private final ClientService clientService;
    private final CompteService compteService;
    private final TransactionService transactionService;
    private final RapportService rapportService;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ConsoleUI(ClientService clientService, CompteService compteService,
                     TransactionService transactionService, RapportService rapportService) {
        this.clientService = clientService;
        this.compteService = compteService;
        this.transactionService = transactionService;
        this.rapportService = rapportService;
        this.scanner = new Scanner(System.in);
    }

    public void demarrer() {
        boolean continuer = true;
        while (continuer) {
            afficherMenuPrincipal();
            System.out.print("Choisissez une option : ");
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1" -> gererClients();
                case "2" -> gererComptes();
                case "3" -> gererTransactions();
                case "4" -> afficherAnalysesEtRapports();
                case "0" -> {
                    System.out.println("\nFermeture de l'application. Au revoir !");
                    continuer = false;
                }
                default -> System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void afficherMenuPrincipal() {
        System.out.println("\n=======================================================");
        System.out.println("                 BANQUE AL BARAKA                       ");
        System.out.println("=======================================================");
        System.out.println("1. Gestion des Clients");
        System.out.println("2. Gestion des Comptes");
        System.out.println("3. Opérations & Transactions Financières");
        System.out.println("4. Détection d'Anomalies & Rapports Stratégiques");
        System.out.println("0. Quitter");
        System.out.println("=======================================================");
    }

    // ==========================================
    //  CLIENTS
    // ==========================================
    private void gererClients() {
        System.out.println("\n--- GESTION DES CLIENTS ---");
        System.out.println("1. Ajouter un nouveau client");
        System.out.println("2. Modifier un client");
        System.out.println("3. Supprimer un client");
        System.out.println("4. Rechercher un client par ID ou Nom");
        System.out.println("5. Lister tous les clients (avec soldes globaux)");
        System.out.println("0. Retour");
        System.out.print("Votre choix : ");

        String choix = scanner.nextLine().trim();
        try {
            switch (choix) {
                case "1" -> {
                    System.out.print("Nom complet : ");
                    String nom = scanner.nextLine().trim();
                    System.out.print("Adresse email : ");
                    String email = scanner.nextLine().trim();
                    Client nouveau = clientService.ajouter(nom, email);
                    System.out.println("Client enregistré avec succès ! Identifiant : " + nouveau.id());
                }
                case "2" -> {
                    System.out.print("Identifiant du client à modifier : ");
                    Long id = Long.parseLong(scanner.nextLine().trim());
                    System.out.print("Nouveau nom complet : ");
                    String nom = scanner.nextLine().trim();
                    System.out.print("Nouvel email : ");
                    String email = scanner.nextLine().trim();
                    clientService.modifier(id, nom, email);
                    System.out.println("Client mis à jour.");
                }
                case "3" -> {
                    System.out.print("Identifiant du client à supprimer : ");
                    Long id = Long.parseLong(scanner.nextLine().trim());
                    clientService.supprimer(id);
                    System.out.println("Client supprimé avec succès.");
                }
                case "4" -> {
                    System.out.print("Entrez le nom ou une partie du nom : ");
                    String nom = scanner.nextLine().trim();
                    List<Client> clients = clientService.trouverParNom(nom);
                    if (clients.isEmpty()) {
                        System.out.println("Aucun client trouvé.");
                    } else {
                        clients.forEach(c -> System.out.printf("[ID: %d] %s | Email: %s%n", c.id(), c.nom(), c.email()));
                    }
                }
                case "5" -> {
                    List<Client> clients = clientService.listerTous();
                    System.out.println("\n--- Liste Générale des Clients ---");
                    for (Client c : clients) {
                        double totalSolde = clientService.getSoldeTotalClient(c.id());
                        int nbComptes = clientService.getNombreComptesClient(c.id());
                        System.out.printf("[ID: %d] %-20s | Email: %-25s | Comptes: %d | Solde Total: %.2f DH%n",
                                c.id(), c.nom(), c.email(), nbComptes, totalSolde);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ==========================================
    //  COMPTES
    // ==========================================
    private void gererComptes() {
        System.out.println("\n--- GESTION DES COMPTES BANCAIRES ---");
        System.out.println("1. Ouvrir un compte courant");
        System.out.println("2. Ouvrir un compte épargne");
        System.out.println("3. Consulter les comptes d'un client");
        System.out.println("4. Trouver le compte au solde Maximum / Minimum");
        System.out.println("0. Retour");
        System.out.print("Votre choix : ");

        String choix = scanner.nextLine().trim();
        try {
            switch (choix) {
                case "1" -> {
                    System.out.print("Identifiant du client titulaire : ");
                    Long clientId = Long.parseLong(scanner.nextLine().trim());
                    System.out.print("Numéro de compte : ");
                    String numero = scanner.nextLine().trim();
                    System.out.print("Solde initial : ");
                    Double solde = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Découvert autorisé : ");
                    Double decouvert = Double.parseDouble(scanner.nextLine().trim());

                    CompteCourant cc = compteService.creerCompteCourant(numero, solde, clientId, decouvert);
                    System.out.println("Compte courant créé avec succès ! ID: " + cc.getId());
                }
                case "2" -> {
                    System.out.print("Identifiant du client titulaire : ");
                    Long clientId = Long.parseLong(scanner.nextLine().trim());
                    System.out.print("Numéro de compte : ");
                    String numero = scanner.nextLine().trim();
                    System.out.print("Solde initial : ");
                    Double solde = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Taux d'intérêt annuel (en %) : ");
                    Double taux = Double.parseDouble(scanner.nextLine().trim());

                    CompteEpargne ce = compteService.creerCompteEpargne(numero, solde, clientId, taux);
                    System.out.println("Compte épargne créé avec succès ! ID: " + ce.getId());
                }
                case "3" -> {
                    System.out.print("Identifiant du client : ");
                    Long clientId = Long.parseLong(scanner.nextLine().trim());
                    List<Compte> comptes = compteService.trouverParClient(clientId);
                    if (comptes.isEmpty()) {
                        System.out.println("Ce client ne possède aucun compte.");
                    } else {
                        comptes.forEach(System.out::println);
                    }
                }
                case "4" -> {
                    compteService.getCompteSoldeMax().ifPresentOrElse(
                            c -> System.out.println("Compte avec le solde le plus élevé : " + c),
                            () -> System.out.println("Aucun compte disponible.")
                    );
                    compteService.getCompteSoldeMin().ifPresent(
                            c -> System.out.println("Compte avec le solde le plus bas    : " + c)
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ==========================================
    //  TRANSACTIONS
    // ==========================================
    private void gererTransactions() {
        System.out.println("\n--- OPÉRATIONS FINANCIÈRES ---");
        System.out.println("1. Effectuer un versement (dépôt)");
        System.out.println("2. Effectuer un retrait");
        System.out.println("3. Effectuer un virement compte à compte");
        System.out.println("4. Historique des transactions d'un compte");
        System.out.println("0. Retour");
        System.out.print("Votre choix : ");

        String choix = scanner.nextLine().trim();
        try {
            switch (choix) {
                case "1" -> {
                    System.out.print("Identifiant du compte récepteur : ");
                    Long compteId = Long.parseLong(scanner.nextLine().trim());
                    System.out.print("Montant à verser : ");
                    Double montant = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Lieu de l'opération  : ");
                    String lieu = scanner.nextLine().trim();

                    Transaction t = transactionService.effectuerVersement(compteId, montant, lieu);
                    System.out.printf("Versement validé ! Réf: %d | Nouveau solde actualisé.%n", t.id());
                }
                case "2" -> {
                    System.out.print("Identifiant du compte débité : ");
                    Long compteId = Long.parseLong(scanner.nextLine().trim());
                    System.out.print("Montant à retirer : ");
                    Double montant = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Lieu de l'opération : ");
                    String lieu = scanner.nextLine().trim();

                    Transaction t = transactionService.effectuerRetrait(compteId, montant, lieu);
                    System.out.printf("Retrait validé ! Réf: %d%n", t.id());
                }
                case "3" -> {
                    System.out.print("Identifiant du compte source (débit) : ");
                    Long sourceId = Long.parseLong(scanner.nextLine().trim());
                    System.out.print("Identifiant du compte destinataire (crédit) : ");
                    Long destId = Long.parseLong(scanner.nextLine().trim());
                    System.out.print("Montant du virement : ");
                    Double montant = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Lieu d'émission : ");
                    String lieu = scanner.nextLine().trim();

                    transactionService.effectuerVirement(sourceId, destId, montant, lieu);
                    System.out.println("Virement inter-comptes complété avec succès !");
                }
                case "4" -> {
                    System.out.print("Identifiant du compte : ");
                    Long compteId = Long.parseLong(scanner.nextLine().trim());
                    List<Transaction> transactions = transactionService.listerParCompteTriees(compteId);
                    if (transactions.isEmpty()) {
                        System.out.println("Aucune opération enregistrée sur ce compte.");
                    } else {
                        System.out.println("\n--- Historique des Opérations ---");
                        transactions.forEach(t -> System.out.printf("[%s] Réf: %-4d | Type: %-10s | Montant: %10.2f DH | Lieu: %-15s%n",
                                t.date().format(DATE_FORMATTER), t.id(), t.type(), t.montant(), t.lieu()));
                        System.out.printf("Total cumulé des opérations : %.2f DH%n", transactionService.calculerTotalParCompte(compteId));
                        System.out.printf("Moyenne par opération        : %.2f DH%n", transactionService.calculerMoyenneParCompte(compteId));
                    }
                }
            }
        } catch (SoldeInsuffisantException | ResourceNotFoundException e) {
            System.err.println("Avertissement métier : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur  : " + e.getMessage());
        }
    }

    // ==========================================
    //  RAPPORTS
    // ==========================================
    private void afficherAnalysesEtRapports() {
        System.out.println("\n--- DÉTECTION D'ANOMALIES & AUDIT ---");
        System.out.println("1. Top 5 des clients les plus riches (par solde cumulé)");
        System.out.println("2. Bilan mensuel des volumes par type d'opération");
        System.out.println("3. Détecter les transactions à montant suspect  ");
        System.out.println("4. Détecter les opérations en lieu inhabituel pour un compte");
        System.out.println("5. Détecter la fraude par fréquence excessive ");
        System.out.println("6. Identifier les comptes inactifs");
        System.out.println("0. Retour");
        System.out.print("Votre choix : ");

        String choix = scanner.nextLine().trim();
        try {
            switch (choix) {
                case "1" -> {
                    List<Map.Entry<Client, Double>> top5 = rapportService.getTop5ClientsParSolde();
                    System.out.println("\n--- TOP 5 DES CLIENTS  ---");
                    int rang = 1;
                    for (var entry : top5) {
                        System.out.printf("%d. [ID: %d] %-20s | Solde Global: %.2f DH%n",
                                rang++, entry.getKey().id(), entry.getKey().nom(), entry.getValue());
                    }
                }
                case "2" -> {
                    System.out.print("Année : ");
                    int annee = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Mois (1 à 12) : ");
                    int mois = Integer.parseInt(scanner.nextLine().trim());

                    Map<TypeTransaction, Double> volumes = rapportService.getRapportMensuelVolume(annee, mois);
                    System.out.printf("\n--- Bilan Mensuel (%02d/%d) ---%n", mois, annee);
                    volumes.forEach((type, total) -> System.out.printf("- %-12s : %.2f DH%n", type, total));
                }
                case "3" -> {
                    System.out.print("Définir le seuil d'alerte en DH : ");
                    Double seuil = Double.parseDouble(scanner.nextLine().trim());
                    List<Transaction> suspects = rapportService.detecterMontantsSuspects(seuil);
                    if (suspects.isEmpty()) {
                        System.out.println("Aucune opération suspecte détectée au-dessus de ce seuil.");
                    } else {
                        System.out.printf("ALERTE : %d transaction(s) dépassent le seuil de %.2f DH :%n", suspects.size(), seuil);
                        suspects.forEach(t -> System.out.printf("! [Compte #%d] Réf: %d | Montant: %.2f DH | Date: %s | Lieu: %s%n",
                                t.idCompte(), t.id(), t.montant(), t.date().format(DATE_FORMATTER), t.lieu()));
                    }
                }
                case "4" -> {
                    System.out.print("Identifiant du compte à auditer : ");
                    Long compteId = Long.parseLong(scanner.nextLine().trim());
                    System.out.print("Lieu habituel du client : ");
                    String lieuHabituel = scanner.nextLine().trim();

                    List<Transaction> insolites = rapportService.detecterLieuxInhabituels(compteId, lieuHabituel);
                    if (insolites.isEmpty()) {
                        System.out.println("Aucune opération hors du secteur habituel.");
                    } else {
                        System.out.println("ALERTE : Transactions détectées en dehors de " + lieuHabituel + " :");
                        insolites.forEach(t -> System.out.printf("! Réf: %d | Montant: %.2f DH | Lieu détecté: %s%n",
                                t.id(), t.montant(), t.lieu()));
                    }
                }
                case "5" -> {
                    System.out.print("Identifiant du compte à inspecter : ");
                    Long compteId = Long.parseLong(scanner.nextLine().trim());
                    List<Transaction> rafal = rapportService.detecterFrequenceExcessive(compteId);
                    if (rafal.isEmpty()) {
                        System.out.println("Comportement normal : aucune opération rapprochée .");
                    } else {
                        System.out.println("ALERTE SUSPICION DE FRAUDE (Opérations en rafale < 1 min) :");
                        rafal.forEach(t -> System.out.printf("! Réf: %d | Date: %s | Montant: %.2f DH%n",
                                t.id(), t.date().format(DATE_FORMATTER), t.montant()));
                    }
                }
                case "6" -> {
                    System.out.print("Période d'inactivité minimale en mois : ");
                    int mois = Integer.parseInt(scanner.nextLine().trim());
                    List<Compte> inactifs = rapportService.identifierComptesInactifs(mois);
                    if (inactifs.isEmpty()) {
                        System.out.println("Tous les comptes sont actifs sur cette période.");
                    } else {
                        System.out.printf("ALERTE : %d compte(s) sans mouvement depuis plus de %d mois :%n", inactifs.size(), mois);
                        inactifs.forEach(c -> System.out.printf("- [ID: %d] Numéro: %s | Solde stagnant: %.2f DH%n",
                                c.getId(), c.getNumero(), c.getSolde()));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'analyse : " + e.getMessage());
        }
    }
}