package ru.sbt.bankapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sbt.bankapi.models.BankTransaction;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
}
