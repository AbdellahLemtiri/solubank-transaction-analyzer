package com.solubank.dao;

import java.util.List;
import java.util.Optional;

import com.solubank.entity.Compte;

public interface CompteDAO {
    Compte save(Compte compte);
    boolean update(Compte compte);
    boolean delete(Long id);
    Optional<Compte> findById(Long id);
    Optional<Compte> findByNumero(String numero);
    List<Compte> findByClientId(Long clientId);
    List<Compte> findAll();
}