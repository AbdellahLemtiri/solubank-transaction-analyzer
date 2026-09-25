package com.solubank.entity;

import com.solubank.entity.enums.TypeCompte;

public final class CompteEpargne extends Compte {

    private Double tauxInteret;

    public CompteEpargne(Long id, String numero, Double solde, Long idClient, Double tauxInteret) {
        super(id, numero, solde, idClient);
        this.tauxInteret = (tauxInteret != null) ? tauxInteret : 0.0;
    }

    public Double getTauxInteret() {
        return tauxInteret;
    }

    public void setTauxInteret(Double tauxInteret) {
        this.tauxInteret = tauxInteret;
    }

    @Override
    public TypeCompte getType() {
        return TypeCompte.EPARGNE;
    }

    @Override
    public String toString() {
        return "CompteEpargne{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", solde=" + solde +
                ", idClient=" + idClient +
                ", tauxInteret=" + tauxInteret + "%" +
                '}';
    }
}