package com.revolut.moneytransfer.controller;

import com.revolut.moneytransfer.Exception.AccountNotFoundException;
import com.revolut.moneytransfer.Exception.DuplicateAccountNumberException;
import com.revolut.moneytransfer.MoneyTransferTestResource;
import com.revolut.moneytransfer.constant.MoneyTransferConstants;
import com.revolut.moneytransfer.constant.MoneyTransferTestConstants;
import com.revolut.moneytransfer.service.AccountService;
import com.revolut.moneytransfer.service.impl.AccountServiceImpl;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class AccountControllerTest {
    private static AccountService accountService;

    @ClassRule
    public static final MoneyTransferTestResource externalResource = new MoneyTransferTestResource();

    @Before
    public void setUp() throws Exception {
        accountService = AccountServiceImpl.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        accountService.clearAllAccounts();
    }

    @Test
    public void shouldCreateAccountForValidData() throws IOException {

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.ACCOUNT_API_PATH
                + MoneyTransferConstants.CREATE_ACCOUNT_PATH
                + "?accountNumber=" + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER.toString() +
                "&accountHolderFirstName=" + MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME +
                "&accountHolderLastName=" + MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME +
                "&debitLimit=" + MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldGetAccountDetailsForValidData() throws IOException, DuplicateAccountNumberException, AccountNotFoundException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpUriRequest request = new HttpGet(MoneyTransferConstants.ACCOUNT_API_PATH
                + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER.toString());

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestForInValidData() throws IOException {

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.ACCOUNT_API_PATH
                + MoneyTransferConstants.CREATE_ACCOUNT_PATH
                + "?accountNumber=" + MoneyTransferTestConstants.TEST_INVALID_ACCOUNT_NUMBER.toString() +
                "&accountHolderFirstName=" + MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME +
                "&accountHolderLastName=" + MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME +
                "&debitLimit=" + MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestForNullorEmptyAccountNumber() throws IOException {

        HttpUriRequest request = new HttpPost(MoneyTransferConstants.ACCOUNT_API_PATH
                + MoneyTransferConstants.CREATE_ACCOUNT_PATH
                + "?accountNumber=" + "" +
                "&accountHolderFirstName=" + MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME +
                "&accountHolderLastName=" + MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME +
                "&debitLimit=" + MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestForExistingAccountNumber() throws IOException {

        HttpUriRequest request1 = new HttpPost(MoneyTransferConstants.ACCOUNT_API_PATH
                + MoneyTransferConstants.CREATE_ACCOUNT_PATH
                + "?accountNumber=" + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER +
                "&accountHolderFirstName=" + MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME +
                "&accountHolderLastName=" + MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME +
                "&debitLimit=" + MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpResponse response = HttpClientBuilder.create().build().execute(request1);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        HttpUriRequest request2 = new HttpPost(MoneyTransferConstants.ACCOUNT_API_PATH
                + MoneyTransferConstants.CREATE_ACCOUNT_PATH
                + "?accountNumber=" + MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER +
                "&accountHolderFirstName=" + MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME +
                "&accountHolderLastName=" + MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME +
                "&debitLimit=" + MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpResponse response2 = HttpClientBuilder.create().build().execute(request2);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response2.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestForInValidAccountNumber() throws IOException, DuplicateAccountNumberException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        HttpUriRequest request = new HttpGet(MoneyTransferConstants.ACCOUNT_API_PATH
                + MoneyTransferTestConstants.TEST_INVALID_ACCOUNT_NUMBER.toString());

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }
}
