package ru.sbt.bankapi.repositories;

import ru.sbt.bankapi.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    public Optional<Card> findByNumber(String number);
}
