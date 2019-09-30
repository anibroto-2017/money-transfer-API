package com.revolut.moneytransfer.controller;

import com.google.gson.Gson;
import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.Util.ValidationUtil;
import com.revolut.moneytransfer.model.ErrorInfo;
import com.revolut.moneytransfer.model.Transaction;
import com.revolut.moneytransfer.model.TransactionResponse;
import com.revolut.moneytransfer.service.TransactionService;
import com.revolut.moneytransfer.service.impl.TransactionServiceImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {

    private TransactionService transactionService = TransactionServiceImpl.getInstance();
    private Gson gson = new Gson();

    @POST
    @Path("/transferMoney")
    public Response saveTransaction(@QueryParam("sourceAccountNumber") String sourceAccountNumber,
                                  @QueryParam("destinationAccountNumber") String destinationAccountNumber,
                                  @QueryParam("money") String money
    ) throws BadRequestException {
        BigDecimal amountNumeric;
        long sourceAccountNumberNumeric, destinationAccountNumberNumeric;
        try {
        ValidationUtil.validateTransactionInput(sourceAccountNumber,destinationAccountNumber,money);
            amountNumeric = BigDecimal.valueOf(Long.parseLong(money));
            sourceAccountNumberNumeric= Long.valueOf(sourceAccountNumber);
            destinationAccountNumberNumeric= Long.valueOf(destinationAccountNumber);

            Transaction transaction = transactionService.saveTransaction(sourceAccountNumberNumeric, destinationAccountNumberNumeric, amountNumeric);
            return Response.status(Response.Status.OK).entity(gson.toJson(transaction)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ErrorInfo
                    (Response.Status.BAD_REQUEST.getReasonPhrase(), e.getLocalizedMessage()))).build();
        }
    }

    @GET
    @Path("/{transactionId}")
    public Response getTransactionDetails(@PathParam("transactionId") String transactionId) throws BadRequestException {

        long transactionIdNumeric;
        Transaction transaction;
        try {
        ValidationUtil.validateTransactionId(transactionId);
            transactionIdNumeric= Long.valueOf(transactionId);
            transaction = transactionService.getTransactionDetails(transactionIdNumeric);

            return Response.status(Response.Status.OK)
                    .entity(gson.toJson(transaction)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ErrorInfo
                    (Response.Status.BAD_REQUEST.getReasonPhrase(), e.getLocalizedMessage()))).build();
        }
    }

    @GET
    @Path("/account/{accountNumber}")
    public Response getTransactionDetailsByAccountNumber(@PathParam("accountNumber") String accountNumber) throws BadRequestException {

        long accountNumberNumeric;
        List<TransactionResponse> transactionList;
        try {
        ValidationUtil.validateAccountNumber(accountNumber);
            accountNumberNumeric= Long.valueOf(accountNumber);
            transactionList = transactionService.getTransactionDetailsByAccountNumber(accountNumberNumeric);

            return Response.status(Response.Status.OK)
                    .entity(gson.toJson(transactionList)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ErrorInfo
                    (Response.Status.BAD_REQUEST.getReasonPhrase(), e.getLocalizedMessage()))).build();
        }
    }
}
