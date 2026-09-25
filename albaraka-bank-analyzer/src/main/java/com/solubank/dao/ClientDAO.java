package com.solubank.dao;

import java.util.List;
import java.util.Optional;

import com.solubank.entity.Client;

public interface ClientDAO {
    Client save(Client client);

    boolean update(Client client);

    boolean delete(Long id);

    Optional<Client> findById(Long id);

    List<Client> findByNom(String nom);

    List<Client> findAll();
}
