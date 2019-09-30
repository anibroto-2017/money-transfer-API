package com.revolut.moneytransfer.controller;

import com.google.gson.Gson;
import com.revolut.moneytransfer.Exception.*;
import com.revolut.moneytransfer.Util.ValidationUtil;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.ErrorInfo;
import com.revolut.moneytransfer.service.AccountService;
import com.revolut.moneytransfer.service.impl.AccountServiceImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

    private AccountService accountService = AccountServiceImpl.getInstance();
    private Gson gson = new Gson();

    @POST
    @Path("/createAccount")
    public Response createAccount(@QueryParam("accountNumber") String accountNumber,
                           @QueryParam("accountHolderFirstName") String accountHolderFname,
                           @QueryParam("accountHolderLastName") String accountHolderLname,
                           @QueryParam("debitLimit") String debitLimit
                           ) throws BadRequestException {
        BigDecimal debitLimitNumeric;
        long accountNumberNumeric;

        try {
            ValidationUtil.validateAccountInput(accountNumber,accountHolderFname,accountHolderLname,debitLimit);
            debitLimitNumeric = BigDecimal.valueOf(Long.parseLong(debitLimit));
            accountNumberNumeric= Long.valueOf(accountNumber);

            Account account = accountService.createAccount(accountNumberNumeric, accountHolderFname, accountHolderLname, debitLimitNumeric);
            return Response.status(Response.Status.OK).entity(gson.toJson(account)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ErrorInfo
                    (Response.Status.BAD_REQUEST.getReasonPhrase(), e.getLocalizedMessage()))).build();
        }
    }

    @GET
    @Path("/{accountNumber}")
    public Response getAccount(@PathParam("accountNumber") String accountNumber) throws BadRequestException {

        long accountNumberNumeric;
        Account account;
        try {
        ValidationUtil.validateAccountNumber(accountNumber);
            accountNumberNumeric= Long.valueOf(accountNumber);
            account = accountService.getAccountDetails(accountNumberNumeric);

            return Response.status(Response.Status.OK)
                    .entity(gson.toJson(account)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ErrorInfo
                    (Response.Status.BAD_REQUEST.getReasonPhrase(), e.getLocalizedMessage()))).build();
        }
    }
}
