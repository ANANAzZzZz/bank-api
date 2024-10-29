package ru.sbt.bankapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbt.bankapi.services.interfaces.BankTransactionService;
import ru.sbt.bankapi.services.interfaces.CardService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/admin/")
public class AdminController {
    private final BankTransactionService bankTransactionServiceImpl;
    private final CardService cardServiceImpl;

    @Operation(summary = "Подтвердить банковскую транзакцию")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "404", description = "Транзакция не найдена")
    })
    @PostMapping("/approve/bankTransaction/{bankTransactionId}")
    public ResponseEntity<String> approveBankTransaction(@PathVariable Long bankTransactionId) {
        return bankTransactionServiceImpl.approveBankTransaction(bankTransactionId);
    }

    @Operation(summary = "Подтвердить выпуск карты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "404", description = "карта не найдена")
    })
    @PostMapping("/approve/card/{cardId}")
    public ResponseEntity<String> approveCardIssue(@PathVariable Long cardId) {
        return cardServiceImpl.approveCardIssue(cardId);
    }
}
