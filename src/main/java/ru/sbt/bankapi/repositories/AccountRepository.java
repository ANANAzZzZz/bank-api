package ru.sbt.bankapi.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sbt.bankapi.models.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    public Optional<Account> findByNumber(String accountNumber);

    @Override
    @EntityGraph(value = "accountWithCards")
    List<Account> findAll();
}
