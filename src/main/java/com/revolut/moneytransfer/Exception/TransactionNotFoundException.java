package com.revolut.moneytransfer.Exception;

import com.revolut.moneytransfer.constant.MoneyTransferConstants;

public class TransactionNotFoundException extends Exception {

    public TransactionNotFoundException(long transactionid) {
        super(MoneyTransferConstants.TRANSACTION_NOT_FOUND + transactionid);
    }
}
