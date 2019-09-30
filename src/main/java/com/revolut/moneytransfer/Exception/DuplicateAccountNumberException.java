package com.revolut.moneytransfer.Exception;

import com.revolut.moneytransfer.constant.MoneyTransferConstants;

public class DuplicateAccountNumberException extends Exception {
    public DuplicateAccountNumberException(long accountNumber) {
        super(MoneyTransferConstants.DUPLICATE_ACCOUNT_NUMBER + accountNumber);
    }
}
