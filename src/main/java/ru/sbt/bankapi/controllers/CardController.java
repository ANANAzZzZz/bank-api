package ru.sbt.bankapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbt.bankapi.dto.BalanceDto;
import ru.sbt.bankapi.dto.MoneyTransferDto;
import ru.sbt.bankapi.dto.SecureCardDto;
import ru.sbt.bankapi.dto.UnsecureCardDto;
import ru.sbt.bankapi.exceptions.CommonBankApiException;
import ru.sbt.bankapi.services.interfaces.CardService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardController {
    private final CardService cardServiceImpl;

    @Operation(summary = "Получить все карты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "404", description = "Карты не найдены")
    })
    @GetMapping()
    public List<SecureCardDto> getCards() {
        return cardServiceImpl.getAllCards();
    }

    @Operation(summary = "Получить баланс карты по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @GetMapping("/balance/{cardId}")
    public BalanceDto getCardBalanceById(@PathVariable Long cardId) {
        return cardServiceImpl.getCardBalanceById(cardId).orElseThrow(
            () -> new CommonBankApiException("Карта не найдена", HttpStatus.NOT_FOUND)
        );
    }

    @Operation(summary = "Создать карту по ID счета")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "400", description = "Неверный счет")
    })
    @PostMapping("/{accountId}")
    public UnsecureCardDto createCardByAccountId(@PathVariable Long accountId) {
        return cardServiceImpl.createCardByAccountId(accountId).orElseThrow(
            () -> new CommonBankApiException("Неверный счет", HttpStatus.BAD_REQUEST)
        );
    }

    @Operation(summary = "Пополнить баланс карты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "400", description = "Неверные учетные данные")
    })
    @PostMapping("/topUpBalance")
    public UnsecureCardDto topUpBalance(@Validated @RequestBody MoneyTransferDto moneyTransferDto) {
        return cardServiceImpl.topUpCardBalance(moneyTransferDto).orElseThrow(
            () -> new CommonBankApiException("Неверные учетные данные", HttpStatus.BAD_REQUEST)
        );
    }

    @Operation(summary = "Снять деньги с карты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "400", description = "Ошибка снятия денег")
    })
    @PostMapping("/withdraw")
    public MoneyTransferDto withdrawMoneyFromCard(@Validated @RequestBody MoneyTransferDto moneyTransferDto) {
        return cardServiceImpl.withdrawMoney(moneyTransferDto).orElseThrow(
            () -> new CommonBankApiException("Ошибка снятия средств", HttpStatus.BAD_REQUEST)
        );
    }

    @Operation(summary = "Удалить карту по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Long cardId) {
        return cardServiceImpl.deleteCard(cardId);
    }
}
