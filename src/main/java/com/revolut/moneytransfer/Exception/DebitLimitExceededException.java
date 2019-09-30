package com.revolut.moneytransfer.Exception;

import com.revolut.moneytransfer.constant.MoneyTransferConstants;

public class DebitLimitExceededException extends Exception {
    public DebitLimitExceededException(long accountNumber) {
        super(MoneyTransferConstants.DEBIT_LIMIT_EXCEEDED + accountNumber);
    }
}
