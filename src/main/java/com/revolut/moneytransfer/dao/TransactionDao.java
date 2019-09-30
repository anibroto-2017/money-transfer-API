package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.model.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionDao {

    public Transaction saveTransaction(long sourceAccountNumber, long destinationAccountNumber, BigDecimal money)
            throws SameAccountException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException;

    public Transaction getTransactionDetails(long transactionId) throws TransactionNotFoundException;

    public List<TransactionResponse> getTransactionDetailsByAccountNumber(long accountNumber) throws AccountNotFoundException;

    public void clearTransactions();

}
