package ru.sbt.bankapi.services.interfaces;

import org.springframework.http.ResponseEntity;
import ru.sbt.bankapi.dto.rq.BankTransactionRequest;
import ru.sbt.bankapi.dto.rs.BankTransactionResponse;
import ru.sbt.bankapi.models.BankTransaction;

import java.util.Optional;

public interface BankTransactionService {
    Optional<BankTransactionResponse> commitBankTransaction(BankTransactionRequest bankTransactionRequest);

    ResponseEntity<String> approveBankTransaction(Long bankTransactionId);

    Optional<BankTransaction> findUnapprovedTransactionById(Long id);
}