package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.model.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    public Transaction saveTransaction(long sourceAccountNumber, long destinationAccountNumber, BigDecimal money) throws SameAccountException,
    AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException;
    public Transaction getTransactionDetails(long transactionId) throws TransactionNotFoundException;
    public List<TransactionResponse> getTransactionDetailsByAccountNumber(long accountNumber) throws AccountNotFoundException;
    public void clearTransactions();
}
