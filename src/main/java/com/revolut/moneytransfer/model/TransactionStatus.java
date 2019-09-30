package com.revolut.moneytransfer.model;

public enum TransactionStatus {
    NEW,
    FAILED,
    SUCCESSFUL,
    INSUFFICIENT_FUND,
    TIMED_OUT
}
