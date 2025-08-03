# Account Transaction API

A Spring Boot REST API for managing customer accounts and their transactions using JPA/Hibernate and H2 database.

## Features

- **Account Management**: Create and retrieve customer accounts
- **Transaction Management**: Record transactions with different operation types
- **Operation Types**: Support for CASH PURCHASE, INSTALLMENT PURCHASE, WITHDRAWAL, and PAYMENT
- **Business Rules**: Automatic amount handling (negative for purchases/withdrawals, positive for payments)
- **Transaction Management**: Proper @Transactional annotations for data consistency
- **H2 Database**: In-memory database with console access for development
- **Initial Data**: Pre-loaded operation types and sample data

## Technologies Used

- Java 17
- Spring Boot 3.2.1
- Spring Data JPA
- H2 Database
- Maven
- Jakarta Validation

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- IntelliJ IDEA (recommended)

### Running the Application

1. **Import Project in IntelliJ IDEA**:
   - Open IntelliJ IDEA
   - File → Open → Select the project folder
   - Wait for Maven to download dependencies

2. **Run the Application**:
   - Navigate to `AccountTransactionApiApplication.java`
   - Right-click and select "Run AccountTransactionApiApplication"
   - Or use the green play button in the main method

3. **Alternative - Command Line**:
   ```bash
   mvn spring-boot:run
   ```

### Database Access

The application uses H2 in-memory database. You can access the H2 console at:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`
- Swagger UI :http://localhost:8080/swagger-ui/index.html

## API Endpoints

### Account Endpoints

#### Create Account
```http
POST /accounts
Content-Type: application/json

{
    "document_number": "12345678900"
}
```

#### Get Account by ID
```http
GET /accounts/{accountId}
```

### Transaction Endpoints

#### Create Transaction
```http
POST /transactions
Content-Type: application/json

{
    "account_id": 1,
    "operation_type_id": 4,
    "amount": 123.45
}
```


## Operation Types

The system supports the following operation types:

| ID | Description          | Amount Rule |
|----|---------------------|-------------|
| 1  | CASH PURCHASE       | Negative    |
| 2  | INSTALLMENT PURCHASE| Negative    |
| 3  | WITHDRAWAL          | Negative    |
| 4  | PAYMENT            | Positive    |


## Testing with cURL

### Create an Account
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{"document_number":"99988877766"}'
```

### Create a Transaction
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{"account_id":1,"operation_type_id":4,"amount":100.00}'
```

### Get Account Information
```bash
curl http://localhost:8080/accounts/1
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/accountapi/
│   │   ├── AccountTransactionApiApplication.java
│   │   ├── controller/          # REST Controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── entity/              # JPA Entities
│   │   ├── repository/          # Data Access Layer
│   │   └── service/             # Business Logic Layer
│   └── resources/
│       ├── application.properties
│       └── data.sql            # Initial data script
└── test/
    └── java/com/example/accountapi/
        └── AccountTransactionApiApplicationTests.java
```

## Key Features Implemented

### Entity Relationships
- **Account** → **Transaction** (One-to-Many)
- **OperationType** → **Transaction** (One-to-Many)
- Proper JPA annotations with cascade operations

### Transaction Management
- `@Transactional` annotations on service methods
- Read-only transactions for query operations
- Proper rollback handling

### Business Logic
- Automatic amount sign conversion based on operation type
- Input validation using Jakarta Validation
- Proper error handling and HTTP status codes

### Data Initialization
- Automatic table creation via JPA
- Initial data loading via `data.sql`
- H2 console for database inspection

## Development Notes

- The application uses an in-memory H2 database that resets on restart
- Transaction amounts are automatically adjusted based on operation type
- All endpoints support CORS for frontend integration
- Comprehensive logging is enabled for development

## Troubleshooting

1. **Port Already in Use**: Change `server.port` in `application.properties`
2. **Database Issues**: Check H2 console at `/h2-console`
3. **Build Issues**: Run `mvn clean install` to refresh dependencies

## Next Steps for Production

- Replace H2 with a persistent database (PostgreSQL, MySQL)
- Add authentication and authorization
- Implement comprehensive error handling
- Add API documentation with Swagger/OpenAPI
- Add comprehensive unit and integration tests
- Implement database migrations with Flyway or Liquibase
