# **Money Transfer API**

The application has 5 endpoints.

###### Account:

**Create Account** : 
Create a new account with the following 4 parameters

Path: /account/createAccount

Parameters:	

                    account number
			account holder first name
			account holder last name
			debit limit

Sample Request: http://localhost:8080/account/createAccount?accountNumber=112233&accountHolderFirstName=Sample&accountHolderLastName=User1&debitLimit=100

Sample Response: {
                    ` "accountNumber": 112233,
                     "accountHolderFirstName": "Sample",
                     "accountHolderLastName": "User1",
                     "accountBalance": 100,
                     "debitLimit": 100,
                     "INITIAL_BALANCE": 100`
                 }

**Get Account Details**: Get the account details of an existing account

Path: account/{account number}

Parameters: account number

Sample Request: http://localhost:8080/account/112233

Sample Response: `{
                     "accountNumber": 112233,
                     "accountHolderFirstName": "Sample",
                     "accountHolderLastName": "User1",
                     "accountBalance": 100,
                     "debitLimit": 100,
                     "INITIAL_BALANCE": 100
                 }`

**Transaction**

**Transfer Amount** :
Transfer a positive amount from a source account to a destination account

Path: /transaction/transferMoney

Parameters: `sourceAccountNumber
			destinationAccountNumber
			Amount
`

Sample Request: http://localhost:8080/transaction/transferMoney?sourceAccountNumber=112233&destinationAccountNumber=445566&money=25

Sample Response: `{
                     "sourceAccountNumber": 112233,
                     "destinationAccountNumber": 445566,
                     "transferAmount": 25,
                     "transactionId": 1569865631294,
                     "status": "SUCCESSFULL"
                 }`

**Get Transaction Details**: 

Get the transaction id of an existing transaction

Path:/transaction/{transaction Id}

Parameters: transaction id

Sample Request: http://localhost:8080/transaction/1569865631294

Sample Response: `{
                     "sourceAccountNumber": 112233,
                     "destinationAccountNumber": 445566,
                     "transferAmount": 25,
                     "transactionId": 1569865631294,
                     "status": "SUCCESSFUL"
                 }`


**Get All transaction Details for an account number**: 

Get the list of transactions made from or to an account

Path:/transaction/account/{account number}

Parameters: account number

Sample Request: http://localhost:8080/transaction/account/445566

Sample Response: `[
                     {
                         "toAccountNumber": 112233,
                         "transactionType": "CREDIT",
                         "amount": 25,
                         "transactionId": 1569865631294,
                         "status": "SUCCESSFULL"
                     }
                 ]
`
Technologies used:
For simplicity, not any heavy framework has been used.
1. Java 11
2. Jersey / JAX-RS
3. Jackson
4. Junit
5. Jetty Server

Assumptions:
For simplicity of the application, following assumptions have been made

1. There is no currency conversion, all the amounts are in an unified currency.
2. Account number is a 6 digit unique number which is provided as input.
3. Transaction id is simply the timestamp of the transaction.
4. No transaction can be done within the same account number, it will show Bad request
5. Transfer of zero or negative amount is not allowed, it will return Bad request
6. Each account has an initial balance of 100, so that it is easier to start the transfer. Otherwise, we would get insufficient balance exception.
7. There is a debit limit for each account. Transferring amount more than the debit limit is not permitted in a single transaction.

Scope of Improvement:
1. Add currency conversion
2. Add the logic for random and unique account number and transaction id generation
3. Making test cases more independent.

The Server is running on port 8080 currently.
