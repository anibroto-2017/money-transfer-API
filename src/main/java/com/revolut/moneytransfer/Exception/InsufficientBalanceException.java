package com.revolut.moneytransfer.Exception;

import com.revolut.moneytransfer.constant.MoneyTransferConstants;

public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException() {
        super(MoneyTransferConstants.INSUFFICIENT_BALANCE);
    }
}
