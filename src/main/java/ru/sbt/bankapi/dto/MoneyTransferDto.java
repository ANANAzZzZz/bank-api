package ru.sbt.bankapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

public record MoneyTransferDto(
    @NotNull
    @Pattern(regexp = "[0-9]{16}")
    String cardNumber,
    @NotNull
    @Range(min = 1)
    BigDecimal amount
) {
}
