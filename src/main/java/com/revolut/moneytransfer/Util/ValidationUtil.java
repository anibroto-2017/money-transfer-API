package com.revolut.moneytransfer.Util;

import com.revolut.moneytransfer.constant.MoneyTransferConstants;

import javax.ws.rs.BadRequestException;

public final class ValidationUtil {

    public static void validateAccountNumber(String accountNumber) {
        long parsedAccountNumber;
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new BadRequestException(MoneyTransferConstants.NULL_ACCOUNT_NUMBER);
        } else if (accountNumber.length() != 6) {
            throw new BadRequestException(MoneyTransferConstants.INVALID_ACCOUNT_NUMBER + accountNumber);
        } else {
            try {
                parsedAccountNumber = Long.parseLong(accountNumber);
            } catch (Exception e) {
                throw new BadRequestException(MoneyTransferConstants.INVALID_ACCOUNT_NUMBER + accountNumber);
            }
        }
        if (parsedAccountNumber < 0) {
            throw new BadRequestException(MoneyTransferConstants.INVALID_ACCOUNT_NUMBER + accountNumber);
        }
    }

    public static void validateAccountHolderName(String accountHolderFirstName, String accountHolderLastName) {
        long parsedAccountNumber;
        if (accountHolderFirstName == null || accountHolderFirstName.isEmpty()) {
            throw new BadRequestException(MoneyTransferConstants.NULL_ACCOUNT_HOLDER);
        }
        if (accountHolderLastName == null || accountHolderLastName.isEmpty()) {
            throw new BadRequestException(MoneyTransferConstants.NULL_ACCOUNT_HOLDER);
        }
    }

    public static void validateDebitLimit(String debitLimit) {
        long parsedDebitLimit;
        if (debitLimit == null || debitLimit.isEmpty()) {
            throw new BadRequestException(MoneyTransferConstants.NULL_DEBIT_LIMIT);
        } else {
            try {
                parsedDebitLimit = Long.parseLong(debitLimit);
            } catch (Exception e) {
                throw new BadRequestException(MoneyTransferConstants.INVALID_DEBIT_LIMIT + debitLimit);
            }
        }
        if (parsedDebitLimit < 0) {
            throw new BadRequestException(MoneyTransferConstants.INVALID_DEBIT_LIMIT + debitLimit);
        }
    }

    public static void validateTransferAmount(String transferAmount) {
        long parsedTransferAmount;
        if (transferAmount == null || transferAmount.isEmpty()) {
            throw new BadRequestException(MoneyTransferConstants.ZERO_NEGATIVE_TRANSFER_AMOUNT);
        } else {
            try {
                parsedTransferAmount = Long.parseLong(transferAmount);
            } catch (Exception e) {
                throw new BadRequestException(MoneyTransferConstants.INVALID_TRANSFER_AMOUNT);
            }
        }
        if (parsedTransferAmount <= 0) {
            throw new BadRequestException(MoneyTransferConstants.ZERO_NEGATIVE_TRANSFER_AMOUNT);
        }
    }

    public static void validateTransactionId(String transactionId) {
        long parsedTransactionId;
        if (transactionId == null || transactionId.isEmpty()) {
            throw new BadRequestException(MoneyTransferConstants.NULL_TRANSACTION_ID);
        } else {
            try {
                parsedTransactionId = Long.parseLong(transactionId);
            } catch (Exception e) {
                throw new BadRequestException(MoneyTransferConstants.INVALID_TRANSACTION_ID + transactionId);
            }
        }
        if (parsedTransactionId < 0) {
            throw new BadRequestException(MoneyTransferConstants.INVALID_TRANSACTION_ID + transactionId);
        }
    }

    public static void validateAccountInput(String accountNumber, String accountHolderFirstName,
                                            String accountHolderLastName, String debitLimit) {
        validateAccountNumber(accountNumber);
        validateAccountHolderName(accountHolderFirstName, accountHolderLastName);
        validateDebitLimit(debitLimit);
    }

    public static void validateTransactionInput(String sourceAccountNumber, String destinationAccountNumber,
                                                String amount) {
        validateAccountNumber(sourceAccountNumber);
        validateAccountNumber(destinationAccountNumber);
        validateTransferAmount(amount);
    }


}
