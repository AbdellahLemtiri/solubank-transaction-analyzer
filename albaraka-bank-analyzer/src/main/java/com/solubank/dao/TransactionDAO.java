package com.solubank.dao;

import com.solubank.entity.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findByCompteId(Long compteId);
    List<Transaction> findAll();
}