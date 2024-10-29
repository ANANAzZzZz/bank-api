package ru.sbt.bankapi.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sbt.bankapi.dto.AccountDto;
import ru.sbt.bankapi.models.Account;
import ru.sbt.bankapi.models.User;
import ru.sbt.bankapi.repositories.AccountRepository;
import ru.sbt.bankapi.services.interfaces.AccountService;
import ru.sbt.bankapi.services.interfaces.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userServiceImpl;

    /**
     * Получает объект {@link AccountDto} по идентификатору счета.
     * Если счет с указанным идентификатором не найден, возвращает {@link Optional#empty()}.
     *
     * @param accountId идентификатор счета
     * @return {@link Optional} с {@link AccountDto}, если счет найден, иначе {@link Optional#empty()}
     */
    @Override
    public Optional<AccountDto> getAccountById(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);

        if (account.isEmpty() || account.get().getIs_deleted()) {
            return Optional.empty();
        }

        return Optional.of(new AccountDto(
            account.get().getId(),
            account.get().getNumber(),
            account.get().getUser().getId(),
            account.get().getCards().stream().mapToLong(card -> card.getId()).boxed().collect(Collectors.toList())
        ));
    }

    /**
     * Получает список всех счетов.
     *
     * @return список {@link AccountDto}
     */
    @Override
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream().filter(account -> !account.getIs_deleted()).map(account -> new AccountDto(
            account.getId(),
            account.getNumber(),
            account.getUser().getId(),
            account.getCards().stream().mapToLong(card -> card.getId()).boxed().collect(Collectors.toList())
        )).collect(Collectors.toList());
    }

    /**
     * Сохраняет новый счет для указанного пользователя.
     * Если пользователь с указанным идентификатором не найден, возвращает {@link Optional#empty()}.
     *
     * @param userId идентификатор пользователя
     * @return {@link Optional} с {@link AccountDto}, если счет успешно сохранен, иначе {@link Optional#empty()}
     */
    @Override
    @Transactional
    public Optional<AccountDto> saveAccount(Long userId) {
        Optional<User> user = userServiceImpl.findById(userId);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        Random random = new Random();
        StringBuilder AccountNumSb = new StringBuilder();
        IntStream.range(0, 20).forEach(i -> AccountNumSb.append(random.nextInt(0, 9)));

        Account account = accountRepository.save(new Account(
            AccountNumSb.toString(),
            user.get()
        ));

        return Optional.of(new AccountDto(
            account.getId(),
            account.getNumber(),
            account.getUser().getId(),
            account.getCards().stream().mapToLong(card -> card.getId()).boxed().collect(Collectors.toList())
        ));
    }

    /**
     * Удаляет счет по идентификатору.
     * Если счет с указанным идентификатором не найден, возвращает {@link ResponseEntity}
     * с кодом 404 и сообщением "Счет не найден".
     *
     * @param accountId идентификатор счета
     * @return {@link ResponseEntity} с кодом 200 и сообщением "Счет успешно удален", если счет успешно удален,
     * или {@link ResponseEntity} с кодом 404 и сообщением "Счет не найден", если счет не найден
     */
    @Override
    @Transactional
    public ResponseEntity<String> deleteAccount(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);

        if (account.isEmpty() || account.get().getIs_deleted()) {
            return ResponseEntity.status(404).body("Счет не найден");
        }

        account.get().setIs_deleted(true);
        accountRepository.save(account.get());

        return ResponseEntity.status(200).body("Счет успешно удален");
    }
}
