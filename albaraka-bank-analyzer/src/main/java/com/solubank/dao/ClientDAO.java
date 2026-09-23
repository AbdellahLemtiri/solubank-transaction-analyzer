package com.solubank.dao;

import java.util.List;
import java.util.Optional;

import com.solubank.entity.Client;

public interface ClientDAO {
    Client save(Client client);

    boolean update(Client client);

    boolean delet(Long id);

    Optional<Client> findById();

    List<Client> findByNom();

    List<Client> findAll();
}
