package com.revolut.moneytransfer.Exception;

import com.revolut.moneytransfer.constant.MoneyTransferConstants;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(long accountNumber){
        super(MoneyTransferConstants.ACCOUNT_NOT_FOUND + accountNumber);
    }
}
