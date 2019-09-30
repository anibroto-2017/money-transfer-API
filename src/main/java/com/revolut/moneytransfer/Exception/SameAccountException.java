package com.revolut.moneytransfer.Exception;

import com.revolut.moneytransfer.constant.MoneyTransferConstants;

public class SameAccountException extends Exception {
    public SameAccountException() {
        super(MoneyTransferConstants.SAME_ACCOUNT_NUMBER);
    }
}
