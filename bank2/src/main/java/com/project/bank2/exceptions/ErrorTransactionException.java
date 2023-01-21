package com.project.bank2.exceptions;

import lombok.Getter;

@Getter
public class ErrorTransactionException extends Exception {
    private String href;

    public ErrorTransactionException(String message, String href) {
        super(message);
        this.href = href;
    }
}
