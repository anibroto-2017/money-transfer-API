package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.constant.MoneyTransferTestConstants;
import com.revolut.moneytransfer.dao.impl.AccountDaoImpl;
import com.revolut.moneytransfer.dao.impl.TransactionDaoImpl;
import com.revolut.moneytransfer.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.revolut.moneytransfer.constant.MoneyTransferTestConstants.TEST_NON_EXISTENT_TRANSACTION_ID;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TransactionDaoTest {


    private static TransactionDao transactionDao;
    private static AccountDao accountDao;

    @Before
    public void setUp() throws Exception {
        transactionDao = TransactionDaoImpl.getInstance();
        accountDao = AccountDaoImpl.getInstance();
    }

    @After
    public void tearDown(){
        accountDao.clearAllAccounts();
        transactionDao.clearTransactions();
    }

    @Test
    public void shouldTransferMoneyAndGetDetailsInResponse() throws SameAccountException, DuplicateAccountNumberException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountDao.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        Transaction transaction = transactionDao.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);
        assertThat(transaction.getSourceAccountNumber(),is(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER));
        assertThat(transaction.getDestinationAccountNumber(),is(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER));
        assertThat(transaction.getTransferAmount(),is(MoneyTransferTestConstants.TEST_TRANSFERAMOUNT));
    }

    @Test
    public void shouldGetTransactionDetails() throws SameAccountException, DuplicateAccountNumberException,TransactionNotFoundException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountDao.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        Transaction transaction = transactionDao.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);
        Transaction transactionDetails = transactionDao.getTransactionDetails(transaction.getTransactionId());

        assertThat(transactionDetails.getSourceAccountNumber(),is(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER));
        assertThat(transactionDetails.getDestinationAccountNumber(),is(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER));
        assertThat(transactionDetails.getTransferAmount(),is(MoneyTransferTestConstants.TEST_TRANSFERAMOUNT));
    }

    @Test
    public void shouldGetTransactionDetailsByAccountNumber() throws SameAccountException, DuplicateAccountNumberException,TransactionNotFoundException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountDao.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        Transaction transaction = transactionDao.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);
        List<TransactionResponse> sourceAccountTransactions = transactionDao.getTransactionDetailsByAccountNumber(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER);

        assertThat(sourceAccountTransactions.get(0).getToAccountNumber(),is(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER));
        assertThat(sourceAccountTransactions.get(0).getStatus(),is(transaction.getStatus()));
        assertThat(sourceAccountTransactions.get(0).getAmount(),is(MoneyTransferTestConstants.TEST_TRANSFERAMOUNT));
        assertThat(sourceAccountTransactions.get(0).getTransactionId(),is(transaction.getTransactionId()));
        assertThat(sourceAccountTransactions.get(0).getTransactionType(),is(TransactionType.DEBIT));
    }

    @Test(expected = SameAccountException.class)
    public void shouldThrowSameAccountExceptionForSameAccountTransfer() throws SameAccountException, DuplicateAccountNumberException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        Transaction transaction = transactionDao.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);
    }
    @Test(expected = AccountNotFoundException.class)
    public void shouldThrowAccountNotFoundExceptionForNonExistentAccountTransfer() throws SameAccountException, DuplicateAccountNumberException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        Transaction transaction = transactionDao.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_NON_EXISTENT_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);
    }
    @Test(expected = DebitLimitExceededException.class)
    public void shouldThrowDebitLimitExceededException() throws SameAccountException, DuplicateAccountNumberException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);
        accountDao.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        Transaction transaction = transactionDao.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DEBITLIMIT.add(BigDecimal.TEN));

    }
    @Test(expected = InsufficientBalanceException.class)
    public void shouldThrowInsufficientBalanceException() throws SameAccountException, DuplicateAccountNumberException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

        accountDao.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_EXCESS_DEBITLIMIT);
        accountDao.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_EXCESS_DEBITLIMIT);

        Transaction transaction = transactionDao.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_EXCESS_TRANSFERAMOUNT);
    }
    @Test(expected = TransactionNotFoundException.class)
    public void shouldThrowTransactionNotFoundException() throws TransactionNotFoundException {
        Transaction transactionDetails = transactionDao.getTransactionDetails(TEST_NON_EXISTENT_TRANSACTION_ID);
    }
    @Test(expected = AccountNotFoundException.class)
    public void shouldThrowAccountNotFoundException() throws AccountNotFoundException {
        List<TransactionResponse> transactionDetails = transactionDao.getTransactionDetailsByAccountNumber(MoneyTransferTestConstants.TEST_NON_EXISTENT_ACCOUNT_NUMBER);
    }
}