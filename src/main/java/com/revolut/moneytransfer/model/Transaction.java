package com.revolut.moneytransfer.model;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {
    long sourceAccountNumber;
    long destinationAccountNumber;
    BigDecimal transferAmount;
    long transactionId;
    TransactionStatus status;

    public Transaction(long sourceAccountNumber, long destinationAccountNumber, BigDecimal transferAmount) {
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.transferAmount = transferAmount;
        this.transactionId = createTransactionID(sourceAccountNumber, destinationAccountNumber);
        this.status = TransactionStatus.NEW;
    }

    public long getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public long getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public synchronized TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    private long createTransactionID(long sourceAccountNumber, long destinationAccountNumber) {
        return Long.parseLong(String.valueOf(new Date().getTime()));
    }

}
