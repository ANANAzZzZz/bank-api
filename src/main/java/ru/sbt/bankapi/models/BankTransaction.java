package ru.sbt.bankapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bank_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction extends AbstractEntity {
    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "sender_card_number")
    private String sender_card_number;

    @Column(name = "receiver_card_number")
    private String receiver_card_number;

    @Column(name = "created_At")
    private LocalDate created_at;

    @Column(name = "is_completed")
    private Boolean is_completed;

    public BankTransaction(BigDecimal amount, String sender_card_number, String receiver_card_number) {
        this.amount = amount;
        this.sender_card_number = sender_card_number;
        this.receiver_card_number = receiver_card_number;
        this.created_at = LocalDate.now();
        this.is_completed = false;
    }
}