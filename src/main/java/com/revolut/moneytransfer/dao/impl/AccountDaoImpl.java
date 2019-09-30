package com.revolut.moneytransfer.dao.impl;

import com.revolut.moneytransfer.Exception.AccountNotFoundException;
import com.revolut.moneytransfer.Exception.DuplicateAccountNumberException;
import com.revolut.moneytransfer.constant.MoneyTransferConstants;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.dao.AccountDao;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AccountDaoImpl implements AccountDao {

    private final ConcurrentMap<Long, Account> accountList = new ConcurrentHashMap<>();

    private static AccountDaoImpl instance;
    private AccountDaoImpl(){}

    public static AccountDaoImpl getInstance(){
        if(instance == null){
            synchronized (AccountDaoImpl.class) {
                if(instance == null) {
                    instance = new AccountDaoImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Account createAccount(long accountNumber, String accountHolderFirstName, String accountHolderLastName, BigDecimal debitLimit)
        throws DuplicateAccountNumberException{

        if(!checkIfAccountNumberExists(accountNumber)) {
            Account newAccount = new Account(accountNumber,accountHolderFirstName,accountHolderLastName,debitLimit);
            accountList.put(accountNumber,newAccount);
            return newAccount;
        }
        else throw new DuplicateAccountNumberException(accountNumber);
    }

    @Override
    public Account getAccountDetails(long accountNumber) throws AccountNotFoundException {
        if(checkIfAccountNumberExists(accountNumber)) {
            return accountList.get(accountNumber);
        }
        else throw new AccountNotFoundException(accountNumber);
    }

    @Override
    public boolean checkIfAccountNumberExists(long accountNumber) {
        return accountList.get(accountNumber) == null ? false : true;
    }


    @Override
    public void clearAllAccounts(){
        accountList.clear();
    }
}
