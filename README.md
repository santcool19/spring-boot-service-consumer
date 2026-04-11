# Service Consumer Microservice

## Overview
`service-consumer` is a production-ready Java microservice built using Spring Boot that consumes transactions from multiple sources (REST endpoint and Kafka) and stores them in an embedded H2 database. It implements industry-standard patterns like Circuit Breaker, Retry, Rate Limiter, and Caching for resilience and performance.

## Technology Stack
- **Java**: 21
- **Build Tool**: Maven 3.x
- **Framework**: Spring Boot 3.3.0
- **Database**: H2 (Embedded)
- **Message Broker**: Kafka
- **Resilience Patterns**: Resilience4j
- **Testing**: JUnit 5, Mockito, Spring Boot Test

## Features

### 1. Multi-Source Transaction Consumption
- **REST Endpoint**: `http://localhost:8080/transactions/{Account_Number}/{Date}`
- **Kafka Topic**: `transaction`
  - Bootstrap Server: `127.0.0.1:9092`
  - Consumer Group: `transaction-consumer-group`

### 2. Resilience Patterns
- **Circuit Breaker**: Prevents cascading failures with configurable thresholds
- **Retry Mechanism**: Automatic retry with exponential backoff
- **Rate Limiter**: Controls traffic and prevents system overload
- **Caching**: Improves performance with Spring Cache

### 3. Standard Microservice Architecture
- **Service Layer**: Business logic with resilience annotations
- **DAO Layer**: Data access abstraction
- **Repository Layer**: JPA repository for database operations
- **Controller Layer**: RESTful API endpoints

### 4. Data Persistence
- Embedded H2 database for transaction storage
- JPA/Hibernate ORM for entity management
- Automatic schema creation and management

### 5. Comprehensive Testing
- Unit tests for Service and DAO layers
- Integration tests using embedded H2 database
- Controller tests with MockMvc
- Test coverage for all CRUD operations

## Project Structure
```
service-consumer/
├── src/
│   ├── main/
│   │   ├── java/com/namsan/consumer/
│   │   │   ├── ServiceConsumerApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CacheConfig.java
│   │   │   │   ├── KafkaConsumerConfig.java
│   │   │   │   └── RestTemplateConfig.java
│   │   │   ├── controller/
│   │   │   │   └── TransactionController.java
│   │   │   ├── service/
│   │   │   │   └── TransactionService.java
│   │   │   ├── dao/
│   │   │   │   └── TransactionDAO.java
│   │   │   ├── repository/
│   │   │   │   └── TransactionRepository.java
│   │   │   ├── model/
│   │   │   │   └── Transaction.java
│   │   │   ├── client/
│   │   │   │   └── TransactionRestClient.java
│   │   │   ├── kafka/
│   │   │   │   └── TransactionKafkaConsumer.java
│   │   │   └── dto/
│   │   │       └── TransactionResponse.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/namsan/consumer/
│       │   ├── config/
│       │   │   └── TestConfig.java
│       │   ├── service/
│       │   │   └── TransactionServiceTest.java
│       │   ├── dao/
│       │   │   └── TransactionDAOTest.java
│       │   ├── controller/
│       │   │   └── TransactionControllerTest.java
│       │   └── integration/
│       │       └── TransactionRepositoryIntegrationTest.java
│       └── resources/
│           └── application-test.properties
└── pom.xml
```

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- Kafka (for message consumption)

### Installation

1. **Clone/Download the project**
```bash
cd C:\NamSan\Assesment\service-consumer
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8081/api`

### Configuration

#### application.properties
- **Server Port**: 8081
- **Database**: H2 (in-memory)
- **Kafka Bootstrap**: 127.0.0.1:9092
- **REST Endpoint**: http://localhost:8080/transactions

#### Resilience4j Settings
- **Circuit Breaker**: 50% failure threshold, 30s wait time
- **Retry**: 3 attempts with 2s exponential backoff
- **Rate Limiter**: 100 requests per minute
- **Cache**: Simple in-memory cache

## API Endpoints

### Fetch Transactions from REST Endpoint
```
GET /api/transactions/fetch-from-rest/{accountNumber}/{date}
```
Fetches transactions from external REST endpoint and stores them.

**Example:**
```
GET /api/transactions/fetch-from-rest/ACC001/2024-04-11
```

### Get All Transactions
```
GET /api/transactions
```

### Get Transaction by ID
```
GET /api/transactions/{id}
```

### Get Transactions by Account Number
```
GET /api/transactions/account/{accountNumber}
```

### Get Transactions by Account and Date
```
GET /api/transactions/account/{accountNumber}/date/{date}
```

### Get Transactions by Source
```
GET /api/transactions/source/{source}
```
Source can be "REST" or "KAFKA"

### Get Statistics
```
GET /api/transactions/stats
```

## Kafka Integration

### Consumer Configuration
- **Topic**: `transaction`
- **Group ID**: `transaction-consumer-group`
- **Bootstrap Servers**: `127.0.0.1:9092`

### Message Format
```json
{
  "account_number": "ACC001",
  "transaction_amount": 1000.00,
  "transaction_type": "DEBIT",
  "transaction_date": "2024-04-11",
  "description": "Sample Transaction",
  "status": "PROCESSED"
}
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Suite
```bash
# Unit tests
mvn test -Dtest=TransactionServiceTest

# Integration tests
mvn test -Dtest=TransactionRepositoryIntegrationTest

# Controller tests
mvn test -Dtest=TransactionControllerTest
```

### Test Coverage
The project includes comprehensive test coverage:
- **TransactionServiceTest**: 12+ test cases for service layer
- **TransactionDAOTest**: 15+ test cases for data access layer
- **TransactionRepositoryIntegrationTest**: 9+ integration test cases
- **TransactionControllerTest**: 8+ controller test cases

## Resilience Patterns Implementation

### 1. Circuit Breaker
```java
@CircuitBreaker(name = "transactionServiceCircuitBreaker", 
                fallbackMethod = "saveTransactionFallback")
public Transaction saveTransaction(Transaction transaction) {
    // Implementation
}
```
Prevents cascading failures by stopping requests when service is down.

### 2. Retry Pattern
```java
@Retry(name = "transactionServiceRetry")
public List<Transaction> getTransactionsByAccount(String accountNumber) {
    // Implementation
}
```
Automatically retries failed operations with exponential backoff.

### 3. Rate Limiter
```java
@RateLimiter(name = "transactionServiceRateLimiter")
public List<Transaction> fetchTransactions(String accountNumber, String date) {
    // Implementation
}
```
Controls request rate to prevent system overload.

### 4. Caching
```java
@Cacheable(value = "transaction", key = "#id")
public Optional<Transaction> getTransaction(Long id) {
    // Implementation
}
```
Improves performance by caching frequently accessed data.

## Database Schema

### Transaction Table
```sql
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL,
    transaction_amount DECIMAL(19,2) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    transaction_date VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'PROCESSED',
    source VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## Performance Optimization

1. **Caching Strategy**
   - Individual transaction caching by ID
   - Account-based transaction caching
   - Source-based transaction caching
   - Status-based transaction caching

2. **Database Indexing**
   - Primary key on ID
   - Foreign key relationships optimized

3. **Connection Pooling**
   - HikariCP for connection management

4. **Batch Processing**
   - Support for batch transaction saves

## Error Handling

- Comprehensive exception handling in all layers
- Fallback methods for circuit breaker
- Graceful degradation on service failures
- Detailed error messages in API responses

## Monitoring and Logging

- Structured logging with SLF4J
- Log levels: DEBUG for detailed info, INFO for operations, ERROR for failures
- Resilience4j health indicators for circuit breaker status

## Future Enhancements

1. Add REST API documentation (Swagger/OpenAPI)
2. Implement audit logging
3. Add transaction encryption for sensitive data
4. Implement message queue for asynchronous processing
5. Add metrics collection with Micrometer
6. Implement distributed tracing
7. Add GraphQL endpoint support

## Troubleshooting

### Issue: Kafka Connection Failed
**Solution**: Ensure Kafka is running on `127.0.0.1:9092`

### Issue: H2 Database Not Initializing
**Solution**: Check application.properties for correct database URL

### Issue: Circuit Breaker Activated
**Solution**: Check service health and logs, wait for configured timeout period

## License
This project is part of the NamSan Assessment.

## Contact
For support or questions, please contact the development team.

