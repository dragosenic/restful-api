## Simple Java RESTful API

Simple Java RESTful API for money transfers between accounts.
Standalone executable program with jetty server.

To clone source code

```sh
git clone https://github.com/dragosenic/restful-api.git
```

To build a project

```sh
mvn clean package
```

To run (available at http://localhost:8080)

```sh
mvn exec:java -Dexec.mainClass="com.dragosenic.Main"
```

Now the following API is ready to use:

Url to GET all account holders<br/>
http://localhost:8080/account-holder

Url to GET account holder by id<br/>
http://localhost:8080/account-holder?id=123456789

Url to GET all accounts<br/>
http://localhost:8080/account

Url to GET account by id<br/>
http://localhost:8080/account?accountNumber=1234567890

Url to POST to tranfer money between two accounts or to deposit money to one account<br/>
http://localhost:8080/money-transfer

Here is the format of json POST to transfer money between two accounts:
```sh
{
    "accountFrom": "1234567891",
    "accountTo": "1234567890",
    "amount": "10.00",
    "description": "descr.";
}
```

Here is the format of json POST to deposit money to one account:

```sh
{
    "accountTo": "1234567891",
    "amount": "111.11",
    "description": "descr.";
}
```

