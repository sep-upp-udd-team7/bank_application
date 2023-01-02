package com.project.bank1.exceptions;

import lombok.Getter;

@Getter
public class FailedTransactionException extends Exception {
    private  String href;

    public FailedTransactionException(String message, String href) {
        super(message);
        this.href = href;
    }
}
