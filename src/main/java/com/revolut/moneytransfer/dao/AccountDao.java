package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.Exception.AccountNotFoundException;
import com.revolut.moneytransfer.Exception.DuplicateAccountNumberException;
import com.revolut.moneytransfer.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    public Account createAccount(long accountNumber, String accountHolderFirstName, String accountHolderLastName, BigDecimal debitLimit)
            throws DuplicateAccountNumberException;

    public Account getAccountDetails(long accountNumber) throws AccountNotFoundException;

    public boolean checkIfAccountNumberExists(long accountNumber);

    public void clearAllAccounts();
}
