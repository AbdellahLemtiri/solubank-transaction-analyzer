package com.solubank.entity;

import com.solubank.entity.enums.TypeCompte;
public abstract sealed class Compte permits CompteCourant, CompteEpargne {

    protected Long id;
    protected String numero;
    protected Double solde;
    protected Long idClient;

    public Compte(Long id, String numero, Double solde, Long idClient) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Le numéro de compte ne peut pas être vide.");
        }
        this.id = id;
        this.numero = numero;
        this.solde = (solde != null) ? solde : 0.0;
        this.idClient = idClient;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Double getSolde() {
        return solde;
    }

    public void setSolde(Double solde) {
        this.solde = solde;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public abstract TypeCompte getType();
}