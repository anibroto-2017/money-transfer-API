package com.revolut.moneytransfer.constant;

public class MoneyTransferConstants {
    public static final Integer SERVER_PORT = 8080;
    public final static String ACCOUNT_API_PATH = "http://localhost:8080/account/";
    public final static String TRANSACTION_API_PATH = "http://localhost:8080/transaction/";
    public final static String MONEY_TRANSFER_PATH = "transferMoney";
    public final static String GET_TRANSACTION_BY_ACCOUNT_PATH = "account/";
    public final static String CREATE_ACCOUNT_PATH = "createAccount";

    public static final String DUPLICATE_ACCOUNT_NUMBER = "Account Number already exists: ";
    public static final String INSUFFICIENT_BALANCE = "Cannot process transaction due to Insufficient balance in account ";
    public static final String ACCOUNT_NOT_FOUND = "No such account exists with account number: ";
    public static final String TRANSACTION_NOT_FOUND = "No such transaction exists with transaction id: ";
    public static final String DEBIT_LIMIT_EXCEEDED = "Debit Limit exceeded for account number: ";
    public static final String INVALID_TRANSFER_AMOUNT = "Transfer amount is invalid";
    public static final String INVALID_ACCOUNT_NUMBER = "Invalid Account Number(Must be of 6 digits): ";
    public static final String INVALID_TRANSACTION_ID = "Invalid Transaction Id: ";
    public static final String NULL_ACCOUNT_NUMBER = "Account Number Cannot be null";
    public static final String NULL_TRANSACTION_ID = "Transaction Id Cannot be null";
    public static final String NULL_ACCOUNT_HOLDER = "Account Holder name Cannot be null";
    public static final String NULL_DEBIT_LIMIT = "Debit limit Cannot be null";
    public static final String INVALID_DEBIT_LIMIT = "Debit limit is not valid";
    public static final String ZERO_NEGATIVE_TRANSFER_AMOUNT = "Zero or negative amount cannot be transferred ";
    public static final String SAME_ACCOUNT_NUMBER = "Credit and Debit account cannot be same ";
    public static final String TRANSACTION_TIMED_OUT = "Transaction has been timed out, please try again after sometime ";
    public static final long WAIT_TIMEOUT = 100L;

    public static final long INITIAL_BALANCE = 100L;
}
