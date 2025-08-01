# Eagle Bank Service

This application is my submission for the Barclays bank 'Take home coding test'. It is an API for a fictional bank called
Eagle bank.

## Running the application

### Prerequisites

You will need the following installed to run the application:

- Java 21+
- Maven 3.6.3+

### To execute the application:

Navigate to the root directory and run:

```a
mvn clean install
mvn spring-boot:run
```

The application will run at http://localhost:8080

## Database:

The database used is an in-memory H2 database. No additional setup is required for this.

The H2 console can be accessed at:

http://localhost:8080/h2-console

The following details will be needed to log in to the H2 console:

```
JDBC URL: jdbc:h2:mem:eagledb
UserName: sa
Password: <empty>
```

## Usage:

JWT authentication has been implemented using Spring Security.

To use the application, you will first need to create a user which is a public endpoint. 

After creating a user, you will need to authenticate yourself using the login endpoint. This will return a JWT token.
For all other requests, this JWT token must be included in the header under the following property:

```Authorization: Bearer <token>```

## Swagger:

Swagger / OpenAPI documentation is available here: http://localhost:8080/swagger-ui/index.html

## Implemented endpoints:

The following endpoints have been implemented:

- Create a user
    - POST http://localhost:8080/v1/users
- Update a user
  - PATCH http://localhost:8080/v1/users/{userId}
- Delete a user
  - DELETE http://localhost:8080/v1/users/{userId}



- Login (Retrieve a JWT token)
  - POST http://localhost:8080/v1/auth/login


- Create an account
  - POST http://localhost:8080/v1/accounts
- Get an account
  - GET http://localhost:8080/v1/accounts/{accountId}


- Create a transaction
  - POST http://localhost:8080/v1/accounts/{accountId}/transactions
- List all transactions for an account
  - GET http://localhost:8080/v1/accounts/{accountId}/transactions