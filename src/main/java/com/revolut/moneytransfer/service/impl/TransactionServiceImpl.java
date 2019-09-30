package com.revolut.moneytransfer.service.impl;

import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.dao.TransactionDao;
import com.revolut.moneytransfer.dao.impl.TransactionDaoImpl;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.model.TransactionResponse;
import com.revolut.moneytransfer.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {

    private static TransactionServiceImpl instance;
    private TransactionDao transactionDao = TransactionDaoImpl.getInstance();

    private TransactionServiceImpl(){}

    public static TransactionServiceImpl getInstance(){
        if(instance == null){
            synchronized (TransactionServiceImpl.class) {
                if(instance == null) {
                    instance = new TransactionServiceImpl();
                }
            }
        }
        return instance;
    }
    @Override
    public Transaction saveTransaction(long sourceAccountNumber, long destinationAccountNumber, BigDecimal money)
            throws SameAccountException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException
    {
        return transactionDao.saveTransaction(sourceAccountNumber,destinationAccountNumber,money);
    }

    @Override
    public Transaction getTransactionDetails(long transactionId) throws TransactionNotFoundException {
        return transactionDao.getTransactionDetails(transactionId);
    }

    @Override
    public List<TransactionResponse> getTransactionDetailsByAccountNumber(long accountNumber) throws AccountNotFoundException {
        return transactionDao.getTransactionDetailsByAccountNumber(accountNumber);
    }

    @Override
    public void clearTransactions() {
        transactionDao.clearTransactions();
    }
}
