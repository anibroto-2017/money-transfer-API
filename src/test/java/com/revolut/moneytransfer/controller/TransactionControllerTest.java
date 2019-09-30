package com.revolut.moneytransfer.controller;

import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.MoneyTransferTestResource;
import com.revolut.moneytransfer.constant.MoneyTransferConstants;
import com.revolut.moneytransfer.constant.MoneyTransferTestConstants;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.service.AccountService;
import com.revolut.moneytransfer.service.impl.AccountServiceImpl;
import com.revolut.moneytransfer.service.TransactionService;
import com.revolut.moneytransfer.service.impl.TransactionServiceImpl;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.*;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class TransactionControllerTest {

    private static AccountService accountService;
    private static TransactionService transactionService;

    @ClassRule
    public static final MoneyTransferTestResource externalResource = new MoneyTransferTestResource();

    @Before
    public void setUp() throws Exception {
        accountService = AccountServiceImpl.getInstance();
        transactionService = TransactionServiceImpl.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        accountService.clearAllAccounts();
        transactionService.clearTransactions();
    }

    @Test
    public void shouldTransactTransferAmount() throws IOException, DuplicateAccountNumberException, AccountNotFoundException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferConstants.MONEY_TRANSFER_PATH
                + "?sourceAccountNumber=" + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER.toString() +
                "&destinationAccountNumber=" + MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER.toString() +
                "&money=" + MoneyTransferTestConstants.TEST_TRANSFERAMOUNT.toString());

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        assertEquals(accountService.getAccountDetails(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER).getAccountBalance(),
                BigDecimal.valueOf(MoneyTransferConstants.INITIAL_BALANCE).subtract(MoneyTransferTestConstants.TEST_TRANSFERAMOUNT));
        assertEquals(accountService.getAccountDetails(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER).getAccountBalance(),
                BigDecimal.valueOf(MoneyTransferConstants.INITIAL_BALANCE).add(MoneyTransferTestConstants.TEST_TRANSFERAMOUNT));

    }

    @Test
    public void shouldGetTransactionDetails() throws IOException, DuplicateAccountNumberException, AccountNotFoundException, SameAccountException, InsufficientBalanceException, DebitLimitExceededException, TimedOutException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        Transaction transaction = transactionService.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);

        HttpUriRequest request = new HttpGet(MoneyTransferConstants.TRANSACTION_API_PATH
                + transaction.getTransactionId());

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldgetTransactionDetailsByAccountNumber() throws IOException, DuplicateAccountNumberException, AccountNotFoundException, SameAccountException, InsufficientBalanceException, DebitLimitExceededException, TimedOutException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        Transaction transaction = transactionService.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);

        HttpUriRequest request = new HttpGet(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferConstants.GET_TRANSACTION_BY_ACCOUNT_PATH
                + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER.toString());

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldThrowBadRequestExceptionForInvalidAccountNumber() throws IOException, DuplicateAccountNumberException, AccountNotFoundException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferConstants.MONEY_TRANSFER_PATH
                + "?sourceAccountNumber=" + MoneyTransferTestConstants.TEST_INVALID_ACCOUNT_NUMBER +
                "&destinationAccountNumber=" + MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER.toString() +
                "&money=" + MoneyTransferTestConstants.TEST_TRANSFERAMOUNT.toString());

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

    }

    @Test
    public void shouldThrowBadRequestExceptionForInvalidAmount() throws IOException, DuplicateAccountNumberException, AccountNotFoundException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferConstants.MONEY_TRANSFER_PATH
                + "?sourceAccountNumber=" + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER.toString() +
                "&destinationAccountNumber=" + MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER.toString() +
                "&money=" + MoneyTransferTestConstants.TEST_INVALID_AMOUNT);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

    }

    @Test
    public void shouldReturnBadRequestForNullAccountNumber() throws IOException, DuplicateAccountNumberException, AccountNotFoundException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferConstants.MONEY_TRANSFER_PATH
                + "?sourceAccountNumber=" +
                "&destinationAccountNumber=" + MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER.toString() +
                "&money=" + MoneyTransferTestConstants.TEST_TRANSFERAMOUNT.toString());

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestForNullAmount() throws IOException, DuplicateAccountNumberException, AccountNotFoundException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferConstants.MONEY_TRANSFER_PATH
                + "?sourceAccountNumber=" + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER +
                "&destinationAccountNumber=" + MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER.toString() +
                "&money=");

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnBadRequest() throws IOException, DuplicateAccountNumberException, AccountNotFoundException, SameAccountException, InsufficientBalanceException, DebitLimitExceededException, TimedOutException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        Transaction transaction = transactionService.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);

        HttpUriRequest request = new HttpGet(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferTestConstants.TEST_INVALID_TRANSACTION_ID);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestForInvalidAccountNumber() throws IOException, DuplicateAccountNumberException, AccountNotFoundException, SameAccountException, InsufficientBalanceException, DebitLimitExceededException, TimedOutException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        Transaction transaction = transactionService.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);

        HttpUriRequest request = new HttpGet(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferConstants.GET_TRANSACTION_BY_ACCOUNT_PATH
                + MoneyTransferTestConstants.TEST_INVALID_ACCOUNT_NUMBER);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestExceptionForInsufficientBalance() throws IOException, DuplicateAccountNumberException, AccountNotFoundException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferConstants.MONEY_TRANSFER_PATH
                + "?sourceAccountNumber=" + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER.toString() +
                "&destinationAccountNumber=" + MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER.toString() +
                "&money=" + MoneyTransferTestConstants.TEST_EXCESS_TRANSFERAMOUNT.toString());

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestExceptionForZeroOrNegativeAmount() throws IOException, DuplicateAccountNumberException, AccountNotFoundException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferConstants.MONEY_TRANSFER_PATH
                + "?sourceAccountNumber=" + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER.toString() +
                "&destinationAccountNumber=" + MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER.toString() +
                "&money=" + BigDecimal.ZERO.toString());

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestExceptionForSameAccountTransfer() throws IOException, DuplicateAccountNumberException, AccountNotFoundException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.TRANSACTION_API_PATH
                + MoneyTransferConstants.MONEY_TRANSFER_PATH
                + "?sourceAccountNumber=" + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER.toString() +
                "&destinationAccountNumber=" + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER.toString() +
                "&money=" + MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

}