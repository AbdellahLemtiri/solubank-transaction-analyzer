package com.solubank.entity;

import com.solubank.entity.enums.TypeCompte;

public final class CompteCourant extends Compte {

    private Double decouvertAutorise;

    public CompteCourant(Long id, String numero, Double solde, Long idClient, Double decouvertAutorise) {
        super(id, numero, solde, idClient);
        this.decouvertAutorise = (decouvertAutorise != null) ? decouvertAutorise : 0.0;
    }

    public Double getDecouvertAutorise() {
        return decouvertAutorise;
    }

    public void setDecouvertAutorise(Double decouvertAutorise) {
        this.decouvertAutorise = decouvertAutorise;
    }

    @Override
    public TypeCompte getType() {
        return TypeCompte.COURANT;
    }

    @Override
    public String toString() {
        return "CompteCourant{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", solde=" + solde +
                ", idClient=" + idClient +
                ", decouvertAutorise=" + decouvertAutorise +
                '}';
    }
}