package com.solubank.service;

import java.util.List;

import com.solubank.dao.ClientDAO;
import com.solubank.dao.CompteDAO;
import com.solubank.entity.Client;
import com.solubank.entity.Compte;
import com.solubank.exception.ResourceNotFoundException;

public class ClientService {

    private final ClientDAO clientDAO;
    private final CompteDAO compteDAO;

    public ClientService(ClientDAO clientDAO, CompteDAO compteDAO) {
        this.clientDAO = clientDAO;
        this.compteDAO = compteDAO;
    }

    public Client Ajouter(String nom, String email) {
        Client client = new Client(null, nom, email);
        return clientDAO.save(client);
    }

    public boolean modifier(Long id, String nom, String email) {
        clientDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client introuvable !"));
        return clientDAO.update(new Client(id, nom, email));
    }

    public boolean supprimer(Long id) {
        clientDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client introuvable !"));
        return clientDAO.delete(id);
    }

    public List<Client> rechercherParNom(String nom) {
        return clientDAO.findByNom(nom);
    }

    public Client rechercherParId(Long id) {
        return clientDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client introuvable !"));

    }

    public List<Client> listerTous() {
        return clientDAO.findAll();
    }

    public Double getSoldeTotalClient(Long id) {
        return compteDAO.findByClientId(id).stream().mapToDouble(Compte::getSolde).sum();
    }

    public int getNombreDeClient(Long id) {
        return compteDAO.findByClientId(id).size();
    }

}
