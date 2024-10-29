package ru.sbt.bankapi.dto.rs;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BankTransactionResponse(
    Long id,
    BigDecimal amount,
    String sender_card_number,
    String receiver_card_number,
    LocalDate created_at
) {
}
