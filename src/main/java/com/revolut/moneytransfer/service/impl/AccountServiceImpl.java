package com.revolut.moneytransfer.service.impl;

import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.dao.AccountDao;
import com.revolut.moneytransfer.dao.impl.AccountDaoImpl;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.service.AccountService;

import javax.ws.rs.BadRequestException;
import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {

    private static AccountServiceImpl instance;
    private AccountDao accountDao = AccountDaoImpl.getInstance();

    private AccountServiceImpl() {
    }

    public static AccountServiceImpl getInstance() {
        if (instance == null) {
            synchronized (AccountServiceImpl.class) {
                if (instance == null) {
                    instance = new AccountServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Account createAccount(long accountNumber, String accountHolderFirstName, String accountHolderLastName, BigDecimal debitLimit) throws BadRequestException, DuplicateAccountNumberException {
        return accountDao.createAccount(accountNumber, accountHolderFirstName, accountHolderLastName, debitLimit);
    }

    @Override
    public Account getAccountDetails(long accountNumber) throws AccountNotFoundException {
        return accountDao.getAccountDetails(accountNumber);
    }

    @Override
    public boolean checkIfAccountNumberExists(long accountNumber) {
        return accountDao.checkIfAccountNumberExists(accountNumber);
    }

    @Override
    public void clearAllAccounts() {
        accountDao.clearAllAccounts();
    }
}
