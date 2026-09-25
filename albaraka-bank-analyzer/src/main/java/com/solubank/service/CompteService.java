package com.solubank.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.solubank.dao.ClientDAO;
import com.solubank.dao.CompteDAO;
import com.solubank.entity.Compte;
import com.solubank.entity.CompteCourant;
import com.solubank.entity.CompteEpargne;
import com.solubank.exception.ResourceNotFoundException;

public class CompteService {

    private final CompteDAO compteDAO;
    private final ClientDAO clientDAO;

    public CompteService(CompteDAO compteDAO, ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
        this.compteDAO = compteDAO;
    }

    public CompteCourant creerCompteCourant(String numero, Double soldeInitial, Long clientId,
            Double decouvertAutorise) {
        var compte = new CompteCourant(null, numero, soldeInitial, clientId, decouvertAutorise);
        return (CompteCourant) compteDAO.save(compte);
    }

    public CompteEpargne creerCompteEpargne(String numero, Double soldeInitial, Long clientId, Double tauxInteret) {
        var compte = new CompteEpargne(null, numero, soldeInitial, clientId, tauxInteret);
        return (CompteEpargne) compteDAO.save(compte);
    }

    public boolean modifierSolde(Long id, double solde) {
        Compte cmpt = compteDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException("Compte introuvable !"));
        cmpt.setSolde(solde);
        return compteDAO.update(cmpt);
    }

    public Optional<Compte> trouverParId(Long id) {
        return compteDAO.findById(id);
    }

    public Optional<Compte> trouverParNumero(String numero) {
        return compteDAO.findByNumero(numero);
    }

    public List<Compte> trouverParClient(Long clientId) {
        return compteDAO.findByClientId(clientId);
    }

    public Optional<Compte> getCompteSoldeMin() {
        return compteDAO.findAll().stream().min(Comparator.comparingDouble(Compte::getSolde));

    }

    
}
