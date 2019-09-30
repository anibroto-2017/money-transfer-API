package com.revolut.moneytransfer.constant;

import java.math.BigDecimal;

public class MoneyTransferTestConstants {

    public static final Long TEST_SOURCE_ACCOUNT_NUMBER = 112233L;
    public static final Long TEST_DESTINATION_ACCOUNT_NUMBER = 332211L;
    public static final Long TEST_NON_EXISTENT_ACCOUNT_NUMBER = 111111L;
    public static final String TEST_INVALID_ACCOUNT_NUMBER = "111ABC111";
    public static final String TEST_INVALID_AMOUNT = "ABCXYZ";
    public static final Long TEST_NON_EXISTENT_TRANSACTION_ID = 1111111111L;
    public static final String TEST_INVALID_TRANSACTION_ID = "ABCXYZ";
    public static final BigDecimal TEST_TRANSFERAMOUNT = BigDecimal.valueOf(10L);
    public static final BigDecimal TEST_EXCESS_TRANSFERAMOUNT = BigDecimal.valueOf(500L);
    public static final BigDecimal TEST_DEBITLIMIT = BigDecimal.valueOf(50L);
    public static final BigDecimal TEST_EXCESS_DEBITLIMIT = BigDecimal.valueOf(500L);
    public static final String TEST_ACCOUNTHOLDERFNAME = "Test";
    public static final String TEST_ACCOUNTHOLDERLNAME = "User";
}
