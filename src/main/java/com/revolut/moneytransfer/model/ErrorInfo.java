package com.revolut.moneytransfer.model;

public class ErrorInfo {
    String Status;
    String message;

    public ErrorInfo(String status, String message) {
        Status = status;
        this.message = message;
    }
}
