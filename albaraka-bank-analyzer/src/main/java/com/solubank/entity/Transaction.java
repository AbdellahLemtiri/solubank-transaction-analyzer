package com.solubank.entity;

import java.time.LocalDateTime;

import com.solubank.entity.enums.TypeTransaction;
 
public record Transaction(
        Long id,
        LocalDateTime date,
        Double montant,
        TypeTransaction type,
        String lieu,
        Long idCompte
) {
     
    public Transaction {
        if (montant == null || montant <= 0) {
            throw new IllegalArgumentException("Le montant de la transaction doit être strictement positif.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Le type de transaction est obligatoire.");
        }
        if (lieu == null || lieu.isBlank()) {
            throw new IllegalArgumentException("Le lieu de l'opération est obligatoire.");
        }
        if (idCompte == null) {
            throw new IllegalArgumentException("L'identifiant du compte associé est obligatoire.");
        }
        if (date == null) {
            date = LocalDateTime.now();
        }
    }
}