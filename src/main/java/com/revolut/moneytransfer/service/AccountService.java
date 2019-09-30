package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.model.Account;

import javax.ws.rs.BadRequestException;
import java.math.BigDecimal;

public interface AccountService {

    public Account createAccount(long accountNumber, String accountHolderFirstName, String accountHolderLastName, BigDecimal debitLimit)
            throws BadRequestException, DuplicateAccountNumberException;

    public Account getAccountDetails(long accountNumber) throws AccountNotFoundException;

    public boolean checkIfAccountNumberExists(long accountNumber);

    public void clearAllAccounts();
}
