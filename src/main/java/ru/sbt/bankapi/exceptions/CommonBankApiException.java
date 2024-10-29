package ru.sbt.bankapi.exceptions;

import org.springframework.http.HttpStatus;


/**
 * Класс исключения, представляющий общую ошибку банка.
 */
public class CommonBankApiException extends RuntimeException {
    private HttpStatus status;

    public CommonBankApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
