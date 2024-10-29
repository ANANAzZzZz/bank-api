package ru.sbt.bankapi.services.interfaces;

import ru.sbt.bankapi.dto.AccountDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Optional<AccountDto> getAccountById(Long accountId);

    List<AccountDto> getAllAccounts();

    Optional<AccountDto> saveAccount(Long userId);

    ResponseEntity<String> deleteAccount(Long accountId);
}
