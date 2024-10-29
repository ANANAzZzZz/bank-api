package ru.sbt.bankapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbt.bankapi.dto.rq.BankTransactionRequest;
import ru.sbt.bankapi.dto.rs.BankTransactionResponse;
import ru.sbt.bankapi.exceptions.CommonBankApiException;
import ru.sbt.bankapi.services.interfaces.BankTransactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bankTransaction")
public class BankTransactionController {
    private final BankTransactionService bankTransactionServiceImpl;

    @Operation(summary = "Произвести создание транзакции перевода денег")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "400", description = "Неудачная попытка выполнения транзакции")
    })
    @PostMapping("/commit")
    public BankTransactionResponse makeBankTransaction(@Validated @RequestBody BankTransactionRequest bankTransactionRequest) {
        return bankTransactionServiceImpl.commitBankTransaction(bankTransactionRequest).orElseThrow(
            () -> new CommonBankApiException("Неудачная попытка выполнения транзакции", HttpStatus.BAD_REQUEST)
        );
    }
}
