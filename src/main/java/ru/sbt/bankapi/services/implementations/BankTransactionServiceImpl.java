package ru.sbt.bankapi.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sbt.bankapi.dto.MoneyTransferDto;
import ru.sbt.bankapi.dto.UnsecureCardDto;
import ru.sbt.bankapi.dto.rq.BankTransactionRequest;
import ru.sbt.bankapi.dto.rs.BankTransactionResponse;
import ru.sbt.bankapi.models.BankTransaction;
import ru.sbt.bankapi.models.Card;
import ru.sbt.bankapi.repositories.BankTransactionRepository;
import ru.sbt.bankapi.services.interfaces.BankTransactionService;
import ru.sbt.bankapi.services.interfaces.CardService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankTransactionServiceImpl implements BankTransactionService {
    private final BankTransactionRepository bankTransactionRepository;
    private final CardService cardServiceImpl;

    /**
     * Метод commitBankTransaction выполняет транзакцию между банковскими картами.
     * Если метод validateAvailabilityOfTransactionCards возвращает false, то метод возвращает пустой Optional.
     * В противном случае, метод создает новый объект BankTransaction на основе данных из BankTransactionRequest,
     * сохраняет его в базе данных с помощью bankTransactionRepository.save,
     * а затем создает новый объект BankTransactionResponse на основе данных из BankTransaction и возвращает его в виде Optional.
     *
     * @param bankTransactionRequest объект, содержащий данные о транзакции
     * @return Optional с объектом BankTransactionResponse, если транзакция прошла успешно, или пустой Optional, если не прошла
     */
    @Override
    @Transactional
    public Optional<BankTransactionResponse> commitBankTransaction(BankTransactionRequest bankTransactionRequest) {
        if (!validateAvailabilityOfTransactionCards(bankTransactionRequest)) {
            return Optional.empty();
        }

        BankTransaction bankTransaction = new BankTransaction(
            bankTransactionRequest.getAmount(),
            bankTransactionRequest.getSender_card_number(),
            bankTransactionRequest.getReceiver_card_number()
        );

        bankTransactionRepository.save(bankTransaction);

        return Optional.of(
            new BankTransactionResponse(
                bankTransaction.getId(),
                bankTransaction.getAmount(),
                bankTransaction.getSender_card_number(),
                bankTransaction.getReceiver_card_number(),
                bankTransaction.getCreated_at()
            )
        );
    }

    /**
     * Метод validateAvailabilityOfTransactionCards проверяет наличие банковских карт по номерам, указанным в BankTransactionRequest.
     * Если обе карты существуют, метод возвращает true, в противном случае - false.
     *
     * @param bankTransactionRequest объект, содержащий данные о транзакции
     * @return true, если обе карты существуют, false - в противном случае
     */
    private Boolean validateAvailabilityOfTransactionCards(BankTransactionRequest bankTransactionRequest) {
        Optional<Card> senderCard = cardServiceImpl.findCardByNumber(bankTransactionRequest.getSender_card_number());
        Optional<Card> receiverCard = cardServiceImpl.findCardByNumber(bankTransactionRequest.getReceiver_card_number());

        return senderCard.isPresent() && receiverCard.isPresent();
    }

    /**
     * Метод для подтверждения банковской транзакции.
     * Производит перевод средст с одной карты, на другую.
     * @param bankTransactionId идентификатор транзакции
     * @return ответ с кодом состояния 200 и сообщением "Транзакция выполнена успешно", если транзакция найдена и успешно подтверждена,
     * или ответ с кодом состояния 404 и сообщением "Транзакция не найдена", если транзакция не найдена.
     */
    @Override
    @Transactional
    public ResponseEntity<String> approveBankTransaction(Long bankTransactionId) {
        Optional<BankTransaction> bankTransaction = findUnapprovedTransactionById(bankTransactionId);

        if (bankTransaction.isEmpty()) {
            return ResponseEntity.status(404).body("Транзакция не найдена");
        }

        MoneyTransferDto senderMoneyTransferDto = new MoneyTransferDto(
            bankTransaction.get().getSender_card_number(),
            bankTransaction.get().getAmount()
        );

        MoneyTransferDto receiverTransferDto = new MoneyTransferDto(
            bankTransaction.get().getReceiver_card_number(),
            bankTransaction.get().getAmount()
        );

        Optional<MoneyTransferDto> withdrawMoney = cardServiceImpl.withdrawMoney(senderMoneyTransferDto);
        Optional<UnsecureCardDto> topUpMoney = cardServiceImpl.topUpCardBalance(receiverTransferDto);

        if (withdrawMoney.isEmpty() || topUpMoney.isEmpty()) {
            return ResponseEntity.status(400).body("Ошибка при выполнении транзакции");
        }

        bankTransaction.get().setIs_completed(true);
        bankTransactionRepository.save(bankTransaction.get());
        
        return ResponseEntity.status(200).body("Транзакция выполнена успешно");
    }

    /**
     * Метод {@code findUnapprovedTransactionById} ищет необработанную транзакцию по её идентификатору.
     *
     * @param id идентификатор транзакции
     * @return опциональное значение типа {@link BankTransaction}, содержащее необработанную транзакцию, если она существует, или пустое значение, если транзакция не найдена или уже обработана
     */
    @Override
    public Optional<BankTransaction> findUnapprovedTransactionById(Long id) {
        Optional<BankTransaction> bankTransaction = bankTransactionRepository.findById(id);

        if (bankTransaction.isEmpty() || bankTransaction.get().getIs_completed()) {
            return Optional.empty();
        }
        return bankTransaction;
    }
}
