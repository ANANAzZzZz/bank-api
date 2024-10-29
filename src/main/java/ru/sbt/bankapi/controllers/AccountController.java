package ru.sbt.bankapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbt.bankapi.dto.AccountDto;
import ru.sbt.bankapi.exceptions.CommonBankApiException;
import ru.sbt.bankapi.services.interfaces.AccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountServiceImp;

    @Operation(summary = "Получить все счета", description = "Метод возвращает список всех счетов")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка счетов",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class)))
    @GetMapping()
    public List<AccountDto> getAllAccounts() {
        return accountServiceImp.getAllAccounts();
    }

    @Operation(summary = "Получить счет по идентификатору",
        description = "Метод возвращает счет по указанному идентификатору")
    @ApiResponse(responseCode = "200", description = "Успешное получение счета",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class)))
    @ApiResponse(responseCode = "404", description = "Счет не найден")
    @GetMapping("/{accountId}")
    public AccountDto getAccountById(@PathVariable Long accountId) {
        return accountServiceImp.getAccountById(accountId).orElseThrow(
            () -> new CommonBankApiException("Счет не найден", HttpStatus.NOT_FOUND)
        );
    }

    @Operation(summary = "Создать новый счет", description = "Метод создает новый счет для указанного пользователя")
    @ApiResponse(responseCode = "200", description = "Успешное создание нового счета",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class)))
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @PostMapping("/{userId}")
    public AccountDto createAccount(@PathVariable Long userId) {
        return accountServiceImp.saveAccount(userId).orElseThrow(
            () -> new CommonBankApiException("Пользователь не найден", HttpStatus.NOT_FOUND)
        );
    }

    @Operation(summary = "Удалить счет по ID", description = "Метод удаляет счет по указанному идентификатору")
    @ApiResponse(responseCode = "200", description = "Успешное удаление счета",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Счет не найден",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountId) {
        return accountServiceImp.deleteAccount(accountId);
    }
}