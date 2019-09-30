package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.constant.MoneyTransferConstants;
import com.revolut.moneytransfer.constant.MoneyTransferTestConstants;
import com.revolut.moneytransfer.dao.impl.AccountDaoImpl;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.AccountStatus;
import com.revolut.moneytransfer.model.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class AccountDaoTest {

    private static AccountDao accountDao;

    @Before
    public void setUp() throws Exception {
        accountDao = AccountDaoImpl.getInstance();
    }

    @After
    public void tearDown(){
        accountDao.clearAllAccounts();
    }

    @Test
    public void shouldCreateAccountAndGetDetailsInResponse() throws DuplicateAccountNumberException {

        Account account = accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        assertThat(account.getAccountNumber(),is(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER));
        assertThat(account.getAccountHolderFirstName(),is(MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME));
        assertThat(account.getAccountHolderLastName(),is(MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME));
        assertThat(account.getDebitLimit(),is(MoneyTransferTestConstants.TEST_DEBITLIMIT));
        assertThat(account.getAccountBalance(),is(BigDecimal.valueOf(MoneyTransferConstants.INITIAL_BALANCE)));
    }

    @Test
    public void shouldGetAccountDetailsInResponse() throws DuplicateAccountNumberException,AccountNotFoundException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        Account accountDetails = accountDao.getAccountDetails(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER);

        assertThat(accountDetails.getAccountNumber(),is(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER));
        assertThat(accountDetails.getAccountHolderFirstName(),is(MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME));
        assertThat(accountDetails.getAccountHolderLastName(),is(MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME));
        assertThat(accountDetails.getDebitLimit(),is(MoneyTransferTestConstants.TEST_DEBITLIMIT));
        assertThat(accountDetails.getAccountBalance(),is(BigDecimal.valueOf(MoneyTransferConstants.INITIAL_BALANCE)));
    }

    @Test
    public void shouldCheckAccountNumberExists() throws DuplicateAccountNumberException,AccountNotFoundException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        assertThat(accountDao.checkIfAccountNumberExists(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER),is(true));
        assertThat(accountDao.checkIfAccountNumberExists(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER),is(false));
    }

    @Test(expected = DuplicateAccountNumberException.class)
    public void shouldThrowDuplicateAccountNumberException() throws DuplicateAccountNumberException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

    }
    @Test(expected = AccountNotFoundException.class)
    public void shouldThrowAccountNotFoundException() throws DuplicateAccountNumberException,AccountNotFoundException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountDao.getAccountDetails(MoneyTransferTestConstants.TEST_NON_EXISTENT_ACCOUNT_NUMBER);
    }
    @Test
    public void shouldClearAccountList() throws DuplicateAccountNumberException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        assertThat(accountDao.checkIfAccountNumberExists(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER),is(true));

        accountDao.clearAllAccounts();
        assertThat(accountDao.checkIfAccountNumberExists(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER),is(false));
    }


}