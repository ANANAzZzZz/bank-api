package ru.sbt.bankapi.dto;

import java.math.BigDecimal;

public record BalanceDto(
    Long cardId,
    BigDecimal balance
) {
}
