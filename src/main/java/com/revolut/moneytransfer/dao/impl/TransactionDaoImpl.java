package com.revolut.moneytransfer.dao.impl;

import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.constant.MoneyTransferConstants;
import com.revolut.moneytransfer.dao.AccountDao;
import com.revolut.moneytransfer.dao.TransactionDao;
import com.revolut.moneytransfer.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class TransactionDaoImpl implements TransactionDao {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDaoImpl.class);

    private static TransactionDaoImpl instance;

    private TransactionDaoImpl() {
    }

    public static TransactionDaoImpl getInstance() {
        if (instance == null) {
            synchronized (TransactionDaoImpl.class) {
                if (instance == null) {
                    instance = new TransactionDaoImpl();
                }
            }
        }
        return instance;
    }

    private final ConcurrentHashMap<Long, Transaction> transactionList = new ConcurrentHashMap<>();
    private final AccountDao accountDao = AccountDaoImpl.getInstance();

    @Override
    public Transaction saveTransaction(long sourceAccountNumber, long destinationAccountNumber, BigDecimal money)
            throws SameAccountException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {
        if (accountDao.checkIfAccountNumberExists(sourceAccountNumber)) {
            if (accountDao.checkIfAccountNumberExists(destinationAccountNumber)) {

                if(sourceAccountNumber == destinationAccountNumber) {
                    throw new SameAccountException();
                }

                Account debitAccount = accountDao.getAccountDetails(sourceAccountNumber);
                Account creditAccount = accountDao.getAccountDetails(destinationAccountNumber);

                if (debitAccount.getDebitLimit().compareTo(money) >= 0) {
                    Transaction newTransaction = new Transaction(sourceAccountNumber, destinationAccountNumber, money);
                    if (transferMoney(debitAccount, creditAccount, money, newTransaction)) {
                        transactionList.put(newTransaction.getTransactionId(), newTransaction);
                        return newTransaction;
                    } else {
                        throw new TimedOutException();
                    }
                } else {
                    throw new DebitLimitExceededException(sourceAccountNumber);
                }
            } else throw new AccountNotFoundException(destinationAccountNumber);
        } else {
            throw new AccountNotFoundException(sourceAccountNumber);
        }
    }

    @Override
    public Transaction getTransactionDetails(long transactionId) throws TransactionNotFoundException {
        if (transactionList.get(transactionId) != null) {
            return transactionList.get(transactionId);
        }
        else {
            throw new TransactionNotFoundException(transactionId);
        }
    }

    @Override
    public List<TransactionResponse> getTransactionDetailsByAccountNumber(long accountNumber) throws AccountNotFoundException
    {

        if (accountDao.checkIfAccountNumberExists(accountNumber)) {
            List<TransactionResponse> transactions = new ArrayList<>();

            for (Transaction transaction : transactionList.values()) {
                TransactionResponse transactionResponse = new TransactionResponse();
                if (accountNumber == transaction.getSourceAccountNumber()) {
                    transactionResponse.setToAccountNumber(transaction.getDestinationAccountNumber());
                    transactionResponse.setAmount(transaction.getTransferAmount());
                    transactionResponse.setStatus(transaction.getStatus());
                    transactionResponse.setTransactionId(transaction.getTransactionId());
                    transactionResponse.setTransactionType(TransactionType.DEBIT);
                }
                if (accountNumber == transaction.getDestinationAccountNumber()) {
                    transactionResponse.setToAccountNumber(transaction.getSourceAccountNumber());
                    transactionResponse.setAmount(transaction.getTransferAmount());
                    transactionResponse.setStatus(transaction.getStatus());
                    transactionResponse.setTransactionId(transaction.getTransactionId());
                    transactionResponse.setTransactionType(TransactionType.CREDIT);
                }
                transactions.add(transactionResponse);
            }
            return transactions;
        }
        else throw new AccountNotFoundException(accountNumber);
    }

    @Override
    public void clearTransactions() {
        transactionList.clear();
    }

    private boolean transferMoney(Account sourceAccount, Account destinationAccount, BigDecimal money, Transaction transaction)
            throws InsufficientBalanceException, TimedOutException {
        final Lock debitLock = sourceAccount.writeLock();
        try {
            if (debitLock.tryLock(MoneyTransferConstants.WAIT_TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    final Lock creditLock = destinationAccount.writeLock();
                    if (creditLock.tryLock(MoneyTransferConstants.WAIT_TIMEOUT, TimeUnit.MILLISECONDS)) {
                        try {
                            if (sourceAccount.debit(money)) {
                                if (destinationAccount.credit(money)) {
                                    transaction.setStatus(TransactionStatus.SUCCESSFUL);
                                    logger.debug("Transaction completed from " + sourceAccount.getAccountNumber() + " to " + destinationAccount.getAccountNumber());
                                    return true;
                                }
                            }
                            transaction.setStatus(TransactionStatus.INSUFFICIENT_FUND);
                            transactionList.put(transaction.getTransactionId(), transaction);
                            throw new InsufficientBalanceException();
                        } finally {
                            creditLock.unlock();
                        }
                    } else {
                        transaction.setStatus(TransactionStatus.TIMED_OUT);
                        transactionList.put(transaction.getTransactionId(), transaction);
                        throw new TimedOutException();
                    }
                } finally {
                    debitLock.unlock();
                }
            } else {
                transaction.setStatus(TransactionStatus.TIMED_OUT);
                transactionList.put(transaction.getTransactionId(), transaction);
                throw new TimedOutException();
            }
        } catch (InterruptedException ex) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionList.put(transaction.getTransactionId(), transaction);
            logger.error("Exception occurred " + ex.getLocalizedMessage());
            throw new TimedOutException();
        }
    }
}
