# ShoppingVerse - E-Commerce Backend Application

## Overview

ShoppingVerse is a role-based E-Commerce Backend application developed using Spring Boot, Spring Security, JWT Authentication, Hibernate/JPA, and MySQL.

The application supports Customer, Seller, and Admin roles with secure authentication and authorization. Customers can browse products, manage carts, and place orders. Sellers can manage products, while Admins can perform administrative operations.

---

## Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA (Hibernate)

### Database

* MySQL

### Security

* JWT Authentication
* BCrypt Password Encoding
* Role-Based Authorization

### Documentation & Tools

* Swagger OpenAPI
* Maven
* Git & GitHub

### Additional Features

* Java Mail Sender
* Validation
* Global Exception Handling
* Logging
* Transaction Management

---

## Features

### Authentication & Authorization

* Customer Registration
* Seller Registration
* Login API
* JWT Token Generation
* JWT Token Validation
* Role-Based Access Control

### Product Management

* Add Product
* Update Product
* Delete Product (Soft Delete)
* Get Deleted Products
* Search Products
* Category Search
* Price Range Search

### Cart Management

* Add Item to Cart
* View Cart
* Cart Validation

### Order Management

* Place Order
* Checkout Cart
* Order History
* Email Notification after Order Placement

### Additional Features

* Pagination
* Sorting
* Filtering
* Validation
* Global Exception Handling
* Transaction Management

---

## Roles

### ADMIN

* View Deleted Products

### SELLER

* Add Products
* Update Products
* Delete Products

### CUSTOMER

* Add Items to Cart
* Checkout Cart
* Place Orders

---

## Security Features

* Spring Security Integration
* JWT Authentication
* BCrypt Password Hashing
* Custom JWT Authentication Filter
* Role-Based Authorization using @PreAuthorize

---

## API Documentation

Swagger UI:

http://localhost:8080/swagger-ui/index.html

---

## Project Architecture

Controller Layer

↓

Service Layer

↓

Repository Layer

↓

MySQL Database

---

## Database Entities

* User
* Customer
* Seller
* Product
* Cart
* Item
* OrderEntity
* Card

---

## How To Run

1. Clone the repository

2. Configure MySQL database

3. Create application.properties using application-example.properties

4. Update database credentials, mail credentials, and JWT secret

5. Run the application

```bash
mvn spring-boot:run
```

6. Open Swagger

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Future Improvements

* Email Verification
* Password Reset
* Seller Product Ownership Validation
* Refresh Tokens
* Docker Deployment

---

## Author

Ravi Madhu
