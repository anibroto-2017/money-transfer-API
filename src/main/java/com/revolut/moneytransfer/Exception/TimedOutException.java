package com.revolut.moneytransfer.Exception;

import com.revolut.moneytransfer.constant.MoneyTransferConstants;

public class TimedOutException extends Exception {
    public TimedOutException() {
        super(MoneyTransferConstants.TRANSACTION_TIMED_OUT);
    }
}
