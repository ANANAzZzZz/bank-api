package ru.sbt.bankapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "card")
@Getter
@Setter
@NoArgsConstructor
public class Card extends AbstractEntity {
    private String number;

    private String expiredate;

    private String cvv;

    private BigDecimal balance;

    private Boolean is_issued;

    @JsonBackReference
    @ManyToOne()
    @JoinColumn(name = "account_id")

    private Account account;

    public Card(String number, String expiredate, String cvv, BigDecimal balance, Account account) {
        this.number = number;
        this.expiredate = expiredate;
        this.cvv = cvv;
        this.balance = balance;
        this.account = account;
        this.is_issued = false;
    }
}
