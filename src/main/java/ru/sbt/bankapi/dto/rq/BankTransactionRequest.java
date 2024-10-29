package ru.sbt.bankapi.dto.rq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankTransactionRequest {
    @Range(min = 1)
    private BigDecimal amount;
    @NotBlank
    @Pattern(regexp = "[0-9]{16}")
    private String sender_card_number;
    @NotBlank
    @Pattern(regexp = "[0-9]{16}")
    private String receiver_card_number;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankTransactionRequest that = (BankTransactionRequest) o;
        return Objects.equals(amount, that.amount) && Objects.equals(sender_card_number, that.sender_card_number) && Objects.equals(receiver_card_number, that.receiver_card_number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, sender_card_number, receiver_card_number);
    }

    @Override
    public String toString() {
        return "BankTransactionRequest{" +
            "amount=" + amount +
            ", sender_card_number='" + sender_card_number + '\'' +
            ", receiver_card_number='" + receiver_card_number + '\'' +
            '}';
    }
}
