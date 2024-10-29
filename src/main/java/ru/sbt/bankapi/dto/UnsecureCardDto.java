package ru.sbt.bankapi.dto;

import java.math.BigDecimal;

public record UnsecureCardDto(
    Long id,
    String number,
    String expiredate,
    String cvv,
    BigDecimal balance
) {
}
