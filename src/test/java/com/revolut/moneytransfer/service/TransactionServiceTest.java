package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.constant.MoneyTransferTestConstants;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.model.TransactionResponse;
import com.revolut.moneytransfer.model.TransactionType;
import com.revolut.moneytransfer.service.impl.AccountServiceImpl;
import com.revolut.moneytransfer.service.impl.TransactionServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.revolut.moneytransfer.constant.MoneyTransferTestConstants.TEST_NON_EXISTENT_TRANSACTION_ID;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TransactionServiceTest {

    private static TransactionService transactionService;
    private static AccountService accountService;

    @Before
    public void setUp() throws Exception {
        transactionService = TransactionServiceImpl.getInstance();
        accountService = AccountServiceImpl.getInstance();
    }

    @After
    public void tearDown() {
        accountService.clearAllAccounts();
        transactionService.clearTransactions();
    }

    @Test
    public void shouldTransferMoneyAndGetDetailsInResponse() throws SameAccountException, DuplicateAccountNumberException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

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
        assertThat(transaction.getSourceAccountNumber(), is(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER));
        assertThat(transaction.getDestinationAccountNumber(), is(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER));
        assertThat(transaction.getTransferAmount(), is(MoneyTransferTestConstants.TEST_TRANSFERAMOUNT));
    }

    @Test
    public void shouldGetTransactionDetails() throws SameAccountException, DuplicateAccountNumberException, TransactionNotFoundException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

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
        Transaction transactionDetails = transactionService.getTransactionDetails(transaction.getTransactionId());

        assertThat(transactionDetails.getSourceAccountNumber(), is(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER));
        assertThat(transactionDetails.getDestinationAccountNumber(), is(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER));
        assertThat(transactionDetails.getTransferAmount(), is(MoneyTransferTestConstants.TEST_TRANSFERAMOUNT));
    }

    @Test
    public void shouldGetTransactionDetailsByAccountNumber() throws SameAccountException, DuplicateAccountNumberException, TransactionNotFoundException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

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
        List<TransactionResponse> sourceAccountTransactions = transactionService.getTransactionDetailsByAccountNumber(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER);

        assertThat(sourceAccountTransactions.get(0).getToAccountNumber(), is(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER));
        assertThat(sourceAccountTransactions.get(0).getStatus(), is(transaction.getStatus()));
        assertThat(sourceAccountTransactions.get(0).getAmount(), is(MoneyTransferTestConstants.TEST_TRANSFERAMOUNT));
        assertThat(sourceAccountTransactions.get(0).getTransactionId(), is(transaction.getTransactionId()));
        assertThat(sourceAccountTransactions.get(0).getTransactionType(), is(TransactionType.DEBIT));
    }

    @Test(expected = SameAccountException.class)
    public void shouldThrowSameAccountExceptionForSameAccountTransfer() throws SameAccountException, DuplicateAccountNumberException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        Transaction transaction = transactionService.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);
    }

    @Test(expected = AccountNotFoundException.class)
    public void shouldThrowAccountNotFoundExceptionForNonExistentAccountTransfer() throws SameAccountException, DuplicateAccountNumberException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_DEBITLIMIT);

        Transaction transaction = transactionService.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_NON_EXISTENT_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_TRANSFERAMOUNT);
    }

    @Test(expected = DebitLimitExceededException.class)
    public void shouldThrowDebitLimitExceededException() throws SameAccountException, DuplicateAccountNumberException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

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
                MoneyTransferTestConstants.TEST_DEBITLIMIT.add(BigDecimal.TEN));

    }

    @Test(expected = InsufficientBalanceException.class)
    public void shouldThrowInsufficientBalanceException() throws SameAccountException, DuplicateAccountNumberException,
            AccountNotFoundException, DebitLimitExceededException, InsufficientBalanceException, TimedOutException {

        accountService.createAccount(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_EXCESS_DEBITLIMIT);
        accountService.createAccount(MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERFNAME,
                MoneyTransferTestConstants.TEST_ACCOUNTHOLDERLNAME,
                MoneyTransferTestConstants.TEST_EXCESS_DEBITLIMIT);

        Transaction transaction = transactionService.saveTransaction(MoneyTransferTestConstants.TEST_SOURCE_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_DESTINATION_ACCOUNT_NUMBER,
                MoneyTransferTestConstants.TEST_EXCESS_TRANSFERAMOUNT);
    }

    @Test(expected = TransactionNotFoundException.class)
    public void shouldThrowTransactionNotFoundException() throws TransactionNotFoundException {
        Transaction transactionDetails = transactionService.getTransactionDetails(TEST_NON_EXISTENT_TRANSACTION_ID);
    }

    @Test(expected = AccountNotFoundException.class)
    public void shouldThrowAccountNotFoundException() throws AccountNotFoundException {
        List<TransactionResponse> transactionDetails = transactionService.getTransactionDetailsByAccountNumber(MoneyTransferTestConstants.TEST_NON_EXISTENT_ACCOUNT_NUMBER);
    }
}