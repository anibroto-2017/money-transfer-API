package com.revolut.moneytransfer.model;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.revolut.moneytransfer.constant.MoneyTransferConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Account {

    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    long accountNumber;
    String accountHolderFirstName;
    String accountHolderLastName;
    BigDecimal accountBalance;
    BigDecimal debitLimit;
    private final transient Lock lock ;

    private final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(MoneyTransferConstants.INITIAL_BALANCE);

    public Account(long accountNumber, String accountHolderFirstName, String accountHolderLastName, BigDecimal debitLimit) {
        this.accountNumber = accountNumber;
        this.accountHolderFirstName = accountHolderFirstName;
        this.accountHolderLastName = accountHolderLastName;
        this.debitLimit = debitLimit;
        this.accountBalance= INITIAL_BALANCE;
        lock = new ReentrantLock();
    }

    public long getAccountNumber() {
        return accountNumber;
    }
    public String getAccountHolderFirstName() {
        return accountHolderFirstName;
    }
    public String getAccountHolderLastName() {
        return accountHolderLastName;
    }
    public BigDecimal getAccountBalance() {
        try {
            lock.lock();
            return accountBalance;
        } finally {
            lock.unlock();
        }
    }
    public BigDecimal getDebitLimit() {
        return debitLimit;
    }
    public boolean debit(BigDecimal amount) {
    //validate amount
        try {
            if (lock.tryLock(MoneyTransferConstants.WAIT_TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    if (accountBalance.compareTo(amount) > 0) {
                        accountBalance = accountBalance.subtract(amount);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException ex) {
            //throw exception
            logger.error("Exception occured: "+ ex.getLocalizedMessage());
        }
        return false;
    }
    public boolean credit(BigDecimal amount) {
        //validate amount
        try {
            if (lock.tryLock(MoneyTransferConstants.WAIT_TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    accountBalance = accountBalance.add(amount);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException ex) {
            logger.error("Exception occured: "+ ex.getLocalizedMessage());
        }
        return true;
    }
    public Lock writeLock() {
        return lock;
    };
}
