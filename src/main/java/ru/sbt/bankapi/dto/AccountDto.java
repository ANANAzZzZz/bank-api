package ru.sbt.bankapi.dto;

import java.util.List;

public record AccountDto(
    Long id,
    String number,
    Long userId,
    List<Long> cardId
) {
}
