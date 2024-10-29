package ru.sbt.bankapi.services.interfaces;

import org.springframework.http.ResponseEntity;
import ru.sbt.bankapi.dto.BalanceDto;
import ru.sbt.bankapi.dto.MoneyTransferDto;
import ru.sbt.bankapi.dto.SecureCardDto;
import ru.sbt.bankapi.dto.UnsecureCardDto;
import ru.sbt.bankapi.models.Card;

import java.util.List;
import java.util.Optional;

public interface CardService {
    List<SecureCardDto> getAllCards();

    Optional<UnsecureCardDto> createCardByAccountId(Long accountId);

    Optional<UnsecureCardDto> topUpCardBalance(MoneyTransferDto moneyTransferDto);

    Optional<BalanceDto> getCardBalanceById(Long cardId);

    ResponseEntity<String> deleteCard(Long cardId);

    Optional<Card> findCardByNumber(String number);

    Optional<Card> findCardById(Long id);

    Optional<MoneyTransferDto> withdrawMoney(MoneyTransferDto moneyTransferDto);

    ResponseEntity<String> approveCardIssue(Long cardId);
}
