package ru.sbt.bankapi.services.implementations;

import ru.sbt.bankapi.dto.BalanceDto;
import ru.sbt.bankapi.dto.SecureCardDto;
import ru.sbt.bankapi.dto.MoneyTransferDto;
import ru.sbt.bankapi.dto.UnsecureCardDto;
import ru.sbt.bankapi.models.Account;
import ru.sbt.bankapi.models.Card;
import ru.sbt.bankapi.repositories.AccountRepository;
import ru.sbt.bankapi.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sbt.bankapi.services.interfaces.CardService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    /**
     * Метод getAllCards возвращает список всех карт из базы данных.
     * Он использует репозиторий cardRepository для получения всех карт и преобразует их в список
     * объектов SecureCardDto.
     * Каждый объект SecureCardDto содержит идентификатор карты и ее номер.
     *
     * @return список всех карт в формате SecureCardDto
     */
    @Override
    public List<SecureCardDto> getAllCards() {
        return cardRepository.findAll().stream().filter(card -> !card.getIs_deleted() && card.getIs_issued()).map(card -> new SecureCardDto(
                card.getId(),
                card.getNumber()))
            .collect(Collectors.toList()
            );
    }

    /**
     * Метод createCardByAccountId создает новую карту и связывает ее с указанным счетом.
     * Он сначала ищет счет в базе данных по идентификатору. Если счет не найден, метод возвращает пустой Optional.
     * Если счет найден, метод генерирует новую карту с помощью метода generateCard.
     * Затем метод сохраняет сгенерированную карту в базе данных с помощью cardRepository.save.
     * После сохранения карты метод создает объект UnsecureCardDto с информацией о карте и возвращает его в Optional.
     *
     * @param accountId идентификатор счета, с которым должна быть связана новая карта
     * @return Optional, содержащий объект UnsecureCardDto с информацией о новой карте или пустой Optional, если счет не найден
     */
    @Transactional
    @Override
    public Optional<UnsecureCardDto> createCardByAccountId(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            return Optional.empty();
        }

        Card generatedCard = generateCard(account.get());

        Card card = cardRepository.save(generatedCard);

        return Optional.of(new UnsecureCardDto(
            card.getId(),
            card.getNumber(),
            card.getExpiredate(),
            card.getCvv(),
            card.getBalance()
        ));
    }

    /**
     * Метод для пополнения баланса карты.
     *
     * @param moneyTransferDto объект с данными о пополнении баланса карты
     * @return опциональное значение типа UnsecureCardDto, содержащее данные о пополненной карте, или пустой опционал, если карта не найдена
     */
    @Override
    @Transactional
    public Optional<UnsecureCardDto> topUpCardBalance(MoneyTransferDto moneyTransferDto) {
        Optional<Card> card = cardRepository.findByNumber(moneyTransferDto.cardNumber());

        if (card.isEmpty() || card.get().getIs_deleted() || !card.get().getIs_issued()) {
            return Optional.empty();
        }

        card.get().setBalance(card.get().getBalance().add(moneyTransferDto.amount()));

        cardRepository.save(card.get());

        return Optional.of(new UnsecureCardDto(
            card.get().getId(),
            card.get().getNumber(),
            card.get().getExpiredate(),
            card.get().getCvv(),
            card.get().getBalance()
        ));
    }

    /**
     * Получает баланс карты по ее идентификатору.
     *
     * @param cardId идентификатор карты
     * @return объект Optional, содержащий объект BalanceDto с информацией о балансе карты, если карта существует, не удалена и выпущена, иначе - пустой Optional
     */
    @Override
    public Optional<BalanceDto> getCardBalanceById(Long cardId) {
        Optional<Card> card = cardRepository.findById(cardId);

        if (card.isEmpty() || card.get().getIs_deleted() || !card.get().getIs_issued()) {
            return Optional.empty();
        }

        return Optional.of(new BalanceDto(
            card.get().getId(),
            card.get().getBalance()
        ));
    }

    /**
     * Метод для удаления карты по ее идентификатору.
     *
     * @param cardId идентификатор карты
     * @return ответный объект с кодом состояния 200 и сообщением "Карта успешно удалена", если карта найдена и удалена, или с кодом состояния 404 и сообщением "Карта не найдена", если карта не найдена
     */
    @Override
    @Transactional
    public ResponseEntity<String> deleteCard(Long cardId) {
        Optional<Card> card = cardRepository.findById(cardId);

        if (card.isEmpty() || card.get().getIs_deleted()) {
            return ResponseEntity.status(404).body("Карта не найдена");
        }

        card.get().setIs_deleted(true);
        cardRepository.save(card.get());

        return ResponseEntity.status(200).body("Карта успешно удалена");
    }

    /**
     * Метод для генерации новой карты для указанного аккаунта.
     *
     * @param account аккаунт, для которого нужно сгенерировать новую карту
     * @return новая карта с уникальным номером, датой и CVV-кодом, с балансом 0 и связанным с указанным аккаунтом
     */
    private Card generateCard(Account account) {
        Random random = new Random();

        // генерация номера карты
        StringBuilder cardNumberSb = new StringBuilder("4023");
        IntStream.range(0, 12).forEach(i -> cardNumberSb.append(random.nextInt(0, 9)));

        // генерация даты
        StringBuilder cardDateSb = new StringBuilder();
        cardDateSb.append(LocalDate.now().getMonthValue() < 10 ? "0" + LocalDate.now().getMonthValue() : LocalDate.now().getMonthValue());
        cardDateSb.append("/");
        cardDateSb.append(LocalDateTime.now().getYear() % 100 + 10);

        return new Card(
            cardNumberSb.toString(),
            cardDateSb.toString(),
            String.valueOf(random.nextInt(100, 999)),
            BigDecimal.valueOf(0),
            account
        );
    }

    /**
     * Метод {@code findCardByNumber} возвращает карту по номеру.
     * Если карта не существует, или она удалена или не выпущена, метод вернет пустой {@link Optional}.
     *
     * @param number номер карты
     * @return {@link Optional} с картой, если она существует, не удалена и выпущена, в противном случае пустой {@link Optional}
     */
    @Override
    public Optional<Card> findCardByNumber(String number) {
        Optional<Card> card = cardRepository.findByNumber(number);

        if (card.isEmpty() || card.get().getIs_deleted() || !card.get().getIs_issued()) {
            return Optional.empty();
        }
        return card;
    }

    /**
     * Метод {@code findCardById} возвращает карту по идентификатору.
     * Если карта не существует, или она удалена или не выпущена, метод вернет пустой {@link Optional}.
     *
     * @param id идентификатор карты
     * @return {@link Optional} с картой, если она существует, не удалена и выпущена, в противном случае пустой {@link Optional}
     */
    @Override
    public Optional<Card> findCardById(Long id) {
        return cardRepository.findById(id);
    }

    /**
     * Метод withdrawMoney выполняет операцию снятия денежных средств с карты.
     * @param moneyTransferDto объект, содержащий информацию о номеру карты и сумме, которую нужно снять.
     * @return возвращает объект MoneyTransferDto, содержащий информацию о номеру карты и текущем балансе
     * после снятия денег. Если операция не удалась (например, карта не найдена или недостаточно средств),
     * возвращается пустой Optional.
     */
    @Override
    @Transactional
    public Optional<MoneyTransferDto> withdrawMoney(MoneyTransferDto moneyTransferDto) {
        Optional<Card> card = findCardByNumber(moneyTransferDto.cardNumber());

        if (card.isEmpty() || card.get().getBalance().subtract(moneyTransferDto.amount()).signum() < 0) {
            return Optional.empty();
        }

        card.get().setBalance(card.get().getBalance().subtract(moneyTransferDto.amount()));
        cardRepository.save(card.get());

        return Optional.of(new MoneyTransferDto(
            moneyTransferDto.cardNumber(),
            card.get().getBalance()
        ));
    }

    /**
     * Метод для подтверждения выпуска карты.
     *
     * @param cardId идентификатор карты.
     * @return {@link ResponseEntity<String>} с сообщением о результате операции.
     */
    @Override
    @Transactional
    public ResponseEntity<String> approveCardIssue(Long cardId) {
        Optional<Card> card = findCardById(cardId);

        if (card.isEmpty() || card.get().getIs_issued()) {
            return ResponseEntity.status(404).body("Крата не найдена");
        }

        card.get().setIs_issued(true);
        cardRepository.save(card.get());

        return ResponseEntity.ok().body("Выпуск карты успешно подтвержден");
    }
}
