# Loan Management System Backend

## Overview
This is the backend API for the **Loan Management System**. Built with **Spring Boot** and **MySQL**, it supports features like user registration, loan application, EMI management, transaction tracking, password reset, and notifications via email/SMS.

## ✨ Features
-  User Registration & Authentication (JWT-based)
-  Role-Based Access Control (Admin, User)
-  Loan Application & Approval Workflow
-  EMI Payment Management with Transaction Tracking
-  Password Reset via Email and OTP
-  Notifications via Email and SMS (Twilio)
-  Token Blacklisting for Logout Security
-  Rate Limiting for API Requests
-  Swagger API Documentation
-  Unit & Integration Tests Included

---

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- MySQL 8.x
- Maven
- Twilio API (SMS)
- Java Mail API (Email)
- Swagger (API Docs)

---

##  Setup & Installation

### 1. Clone the repository:
```bash 
git clone https://github.com/Demophyta/loan-management.git
cd loan-management
```

### 2. Setup and Installation
 
Clone the repository:
```bash
git clone https://github.com/Demophyta/loan-management.git
cd loan-management
```
Configure MySQL database:

Create a database named loan_management (or your preferred name).
```bash

Update the src/main/resources/application.properties file with your MySQL credentials and database URL:
spring.datasource.url=jdbc:mysql://localhost:3306/loan_management
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update
```

Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```
Access Swagger API docs at:
```bash
http://localhost:8081/swagger-ui/index.html
```
API Endpoints
/api/auth - User registration and login

/api/loans - Loan application and management

/api/emis - EMI payment processing

/api/transactions - View payment transactions

/api/password-reset - Password reset via email or OTP

/api/notifications - Notifications handling

/api/admin - Admin-specific operations

Refer to Swagger UI for full request/response details.

Running Tests
```bash
mvn test
Project Structure
```
```bash
src/
 ├─ main/
 │   ├─ java/com/demo/loan/management/
 │   │    ├─ controller/          # REST controllers
 │   │    ├─ service/             # Business logic services
 │   │    ├─ model/               # Entity classes
 │   │    ├─ repository/          # JPA repositories
 │   │    ├─ dto/                 # Data Transfer Objects
 │   │    ├─ security/            # Security config and JWT utils
 │   │    └─ exception/           # Custom exceptions and handlers
 │   └─ resources/
 │        └─ application.properties
 └─ test/                        # Unit and integration tests
```
## Notes
Make sure to configure your Twilio credentials in application.properties to enable SMS notifications.

For email functionality, configure the Java Mail API settings similarly.

## Contributing
Feel free to fork and submit pull requests. Please follow coding conventions and write tests for new features.

## License
MIT License

