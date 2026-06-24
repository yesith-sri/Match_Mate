# 💙 MatchMate

### A Compatibility-Based Dating Platform for Meaningful Connections

MatchMate is a community-driven dating platform designed to create meaningful real-world connections through compatibility-based matching and structured blind dating events.

Unlike traditional dating applications that rely on swipe-based interactions, MatchMate focuses on understanding users through their interests, personality traits, relationship goals, and preferences. The platform analyzes user compatibility and helps organize personalized blind dating events in a safe and moderated environment.

---

## ✨ Features

### 👤 User Management
- User registration and authentication
- Secure JWT-based login system
- User profile management
- Personalized compatibility questionnaire

### 🧠 Compatibility Matching Engine
- Personality-based matching
- Interest similarity analysis
- Relationship goal alignment
- Preference-based compatibility scoring
- Automated match generation before events

### 🎟️ Event Management
- Browse upcoming dating events
- Event registration and ticket management
- Event capacity handling
- Event details and updates

### 🛠️ Admin Dashboard
- Create and manage events
- Monitor participant registrations
- Trigger compatibility matching
- View generated match pairs
- Manage platform operations

### 🔒 Privacy & Security
- Private user profiles
- Role-based access control
- Encrypted password storage
- Secure API communication

---

# 🛠️ Technology Stack

## Frontend
- React.js
- TypeScript
- Vite
- HTML5 / CSS3

## Backend
- Java
- Spring Boot
- Spring Security
- JWT Authentication
- RESTful APIs

## Database
- PostgreSQL

## Development Tools
- Git & GitHub
- Docker
- Postman
- IntelliJ IDEA
- Visual Studio Code

---

# 🧮 Compatibility Matching Algorithm

MatchMate uses a weighted compatibility scoring algorithm to evaluate potential matches.

The algorithm considers:

- Gender preference compatibility
- Age preference matching
- Shared interests
- Partner preference fulfillment
- Personality compatibility
- Relationship goal alignment

Each participant receives a compatibility score between potential matches, and the system selects the highest-scoring pairs for each event.

---

# 🚀 Getting Started

## Prerequisites

Make sure you have installed:

- Java 17+
- Node.js 18+
- PostgreSQL
- Docker (optional)

---

## Backend Setup

Clone the repository:

```bash
git clone https://github.com/your-username/matchmate.git
```

---

# 📡 REST API Documentation

> **Version:** 1.0-SNAPSHOT &nbsp;|&nbsp; **Framework:** Spring Boot 4.0.0 &nbsp;|&nbsp; **Java:** 21 &nbsp;|&nbsp; **Auth:** JWT (JJWT 0.11.5)
>
> **Base URL:** `/api/v1`

---

## 1. Overview

The Basic Resource Package is a Spring Boot REST API for a resource booking and event management platform. It exposes endpoints for user authentication, event and booking management, and payment handling. The API is secured with JWT tokens and uses PostgreSQL as the primary database with Liquibase for schema versioning.

### 1.1 Technology Stack

| Technology | Version | Description |
|---|---|---|
| Spring Boot | 4.0.0 | Core framework, dependency injection, auto-configuration |
| Spring Security | 6.x | Authentication and authorization with JWT |
| Spring Data JPA | 3.x | ORM layer over PostgreSQL |
| Liquibase | Latest | Database schema migrations (`db/changelog/changes/`) |
| JJWT | 0.11.5 | JWT generation, parsing, and validation |
| Lombok | 1.18.42 | Boilerplate reduction (`@Data`, `@Builder`, `@Slf4j`, etc.) |
| PostgreSQL | Runtime | Primary relational database |
| Actuator | Included | Health checks and monitoring endpoints |

### 1.2 Package Structure

The project follows a domain-driven layered architecture under `com.edu.basic`:

| Package / File | Type | Description |
|---|---|---|
| `booking/` | Package | Booking controllers, services, and DTOs |
| `config/` | Package | Spring Security, CORS, and app configuration beans |
| `entity/` | Package | JPA entity classes mapped to DB tables |
| `event/` | Package | Event management controllers and services |
| `events/` | Package | Domain events (application event publishing) |
| `exception/` | Package | Global exception handler and custom exception classes |
| `payment/` | Package | Payment processing controllers and services |
| `repositary/` | Package | Spring Data JPA repositories *(note: typo in package name)* |
| `Security/` | Package | JWT filter, token provider, and UserDetailsService |
| `service/` | Package | Shared/common service layer |
| `user/` | Package | User registration, profile, and authentication |
| `Main.java` | Class | Spring Boot entry point (`@SpringBootApplication`) |

### 1.3 Database Schema

Liquibase manages the schema through four ordered changesets in `db/changelog/changes/`:

| File | Type | Description |
|---|---|---|
| `01-create-user-table.xml` | Migration | Users table — credentials, roles, profile fields |
| `02-create-event-tables.xml` | Migration | Events table — title, description, capacity, dates |
| `03-create-bookings-table.xml` | Migration | Bookings table — user-event link, status, timestamps |
| `04-create-payments-table.xml` | Migration | Payments table — amount, method, booking FK, status |

---

## 2. Configuration

### 2.1 Running with Docker

```bash
# Start database
docker compose up -d

# Run application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2.2 Configuration Files

- `application.yml` — production/default configuration
- `application-dev.yml` — local development overrides (points to `localhost:5432`)

Activate dev profile:

```bash
SPRING_PROFILES_ACTIVE=dev   # env variable
# or
spring.profiles.active=dev   # application.yml property
```

### 2.3 Key Configuration Properties

| Property | Type | Description |
|---|---|---|
| `spring.datasource.url` | String | JDBC URL — `jdbc:postgresql://host:5432/dbname` |
| `spring.datasource.username` | String | PostgreSQL username |
| `spring.datasource.password` | String | PostgreSQL password |
| `spring.liquibase.change-log` | String | `classpath:db/changelog/db.changelog-master.xml` |
| `jwt.secret` | String | Base64-encoded HMAC secret (min 256-bit recommended) |
| `jwt.expiration` | Long | Token TTL in milliseconds (e.g., `86400000` = 24 h) |
| `spring.jpa.hibernate.ddl-auto` | String | Set to `none` — Liquibase owns schema management |
| `server.port` | Integer | Default `8080`; override per environment |

---

## 3. User Management API

> **Base URL:** `/v1`

Most user-related APIs require JWT authentication:
```
Authorization: Bearer <JWT_TOKEN>
```

### 3.1 Authentication Endpoints

#### `POST /v1/auth/register`
Creates a new user account and returns an authentication token along with user details.

| Field | Type | Description |
|---|---|---|
| `email` | String | User email address |
| `password` | String | User password |
| `firstName` | String | User first name |
| `lastName` | String | User last name |
| `age` | Integer | User age |
| `gender` | String | User gender |
| `phoneNumber` | String | Contact number |
| `city` | String | User city |
| `country` | String | User country |

#### `POST /v1/auth/login`
Authenticates an existing user and generates a JWT authentication token.

| Field | Type | Description |
|---|---|---|
| `email` | String | Registered user email |
| `password` | String | User password |

#### `GET /v1/auth/check-email/{email}`
Checks whether an email address is already registered.

### 3.2 User Profile Endpoints

#### `GET /v1/users/{userId}`
Retrieves user profile information using user ID.

#### `PUT /v1/users/{userId}`
Updates existing user profile information.

| Field | Type | Description |
|---|---|---|
| `firstName` | String | Updated first name |
| `lastName` | String | Updated last name |
| `age` | Integer | Updated age |
| `gender` | String | Updated gender |
| `phoneNumber` | String | Updated phone |
| `bio` | String | User description |
| `city` | String | Current city |
| `country` | String | Current country |
| `profileImageUrl` | String | Profile image URL |

#### `DELETE /v1/users/{userId}`
Deletes a user account permanently.

### 3.3 Database Entity — `users`

| Column | Type | Description |
|---|---|---|
| `user_id` | Long | Primary Key |
| `email` | VARCHAR(100) | Unique user email |
| `password` | VARCHAR | Encrypted password |
| `first_name` | VARCHAR(50) | First name |
| `last_name` | VARCHAR(50) | Last name |
| `age` | Integer | User age |
| `gender` | VARCHAR(20) | User gender |
| `phone_number` | VARCHAR(20) | Contact number |
| `bio` | TEXT | User biography |
| `role` | VARCHAR(20) | User role |
| `is_active` | Boolean | Account status |
| `is_email_verified` | Boolean | Email verification status |
| `city` | VARCHAR(100) | User city |
| `country` | VARCHAR(100) | User country |
| `profile_image_url` | VARCHAR(255) | Profile image URL |

### 3.4 Architecture Components

| Layer | Class(es) | Responsibility |
|---|---|---|
| Controller | `AuthController`, `UserController` | HTTP requests, validation, responses |
| Service | `AuthServiceImpl`, `UserServiceImpl` | Business logic, authentication processing |
| Repository | `UserRepository` | Database communication |
| Mapper | `UserMapper` | Entity ↔ DTO conversion |

---

## 4. Event Management API

> **Base URL:** `{{host}}/v1/events`

### 4.1 Common Response Format

All event-related endpoints return a standardized response wrapper:

```json
{
  "success": true,
  "message": "Operation description",
  "data": {},
  "statusCode": 200
}
```

| Field | Type | Description |
|---|---|---|
| `success` | Boolean | Indicates if the operation was successful |
| `message` | String | Contextual feedback for the user or developer |
| `data` | Object | The primary payload (DTO or List) |
| `statusCode` | Integer | Standard HTTP status code |

### 4.2 DTOs

#### EventRequestDTO
Used exclusively for creating or updating event resources.

| Field | Type | Required | Description |
|---|---|---|---|
| `eventName` | String | Yes | Min 3, Max 100 characters |
| `description` | String | Yes | Min 10, Max 500 characters |
| `eventType` | Enum | Yes | `SPEED_DATING`, `GROUP_DATING`, etc. |
| `eventDate` | LocalDateTime | Yes | Must be a future date/time |
| `eventEndDate` | LocalDateTime | Yes | Must be after the start date |
| `location` | String | Yes | Venue address or name |
| `latitude` | Double | Yes | Between -90 and 90 |
| `longitude` | Double | Yes | Between -180 and 180 |
| `ticketPrice` | BigDecimal | Yes | Must be greater than 0 |
| `totalSeats` | Integer | Yes | Between 1 and 10,000 |
| `imageUrl` | String | No | URL to the event's promotional image |
| `specialInstructions` | String | No | Max 1000 characters |

#### EventResponseDTO
Standard structure returned for all event retrieval requests.

| Field | Type | Description |
|---|---|---|
| `eventId` | Long | Unique identifier |
| `eventStatus` | EventStatus | `UPCOMING`, `ONGOING`, `COMPLETED`, `CANCELLED` |
| `availableSeats` | Integer | Calculated as `totalSeats - confirmed participants` |
| `bookingPercentage` | Integer | Dynamic progress indicator |
| `confirmedMaleCount` | Integer | Current male participant count |
| `confirmedFemaleCount` | Integer | Current female participant count |
| `createdByUsername` | String | Email of the event organizer |

### 4.3 Endpoints

#### Event Creation & Management

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/create` | Bearer JWT | Creates a new event; assigns authenticated user as creator |
| `PUT` | `/{eventId}` | Bearer JWT | Modify event details. Returns `403 Forbidden` if not the creator |
| `DELETE` | `/{eventId}` | Bearer JWT | Permanently removes the event. Restricted to the creator |

#### Discovery & Search

| Method | Endpoint | Params | Description |
|---|---|---|---|
| `GET` | `/nearby` | `latitude`, `longitude`, `radius` (default 50 km) | Geospatial search within a radius |
| `GET` | `/search` | `keyword` (String) | Partial matches on event names |
| `GET` | `/status/{status}` | — | Filter by status: `UPCOMING`, `ONGOING`, etc. |
| `GET` | `/type/{type}` | — | Filter by type: `DINNER_NIGHT`, `ADVENTURE_DATE`, etc. |

#### Admin Controls

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `PUT` | `/{eventId}/limits` | `ROLE_ADMIN` | Update `maleLimit` and `femaleLimit`. Cannot be set below existing confirmed bookings |

### 4.4 Business & Validation Rules

1. **Temporal Integrity** — An event's end date must always follow its start date.
2. **Ownership** — Only the `createdByUserId` can modify or delete a specific event.
3. **Default State** — Every new event is initialized with `UPCOMING` status.
4. **Automatic Scaling** — Adjusting gender limits recalculates `totalSeats` and `availableSeats` automatically.
5. **Data Integrity** — Gender-based limits cannot be decreased below the number of already-registered users.

### 4.5 Database Schema — `events`

| Column | Type | Constraints |
|---|---|---|
| `event_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| `event_name` | VARCHAR(100) | NOT NULL |
| `event_status` | ENUM | `UPCOMING`, `ONGOING`, `COMPLETED`, `CANCELLED` |
| `ticket_price` | DECIMAL(19,2) | NOT NULL, CHECK > 0 |
| `male_limit` | INTEGER | Default 0 |
| `female_limit` | INTEGER | Default 0 |
| `created_by` | BIGINT | FOREIGN KEY → `users.id` |

---

## 5. Payment Management API

> **Base URL:** `/v1/payments`

The Payment Management module handles online payment processing for event bookings using the **PayHere** payment gateway.

**Capabilities:**
- Initiating payments
- Generating PayHere payment hash
- Receiving PayHere payment callbacks
- Verifying payment authenticity
- Updating payment status
- Automatically confirming bookings after successful payments
- Retrieving payment details by booking ID

### 5.1 Payment Flow

```
User
 │
 ├─▶ Create Booking  ──▶  Booking Status = PENDING
 │
 ├─▶ Initiate Payment API
 │
 ├─▶ Generate PayHere Hash
 │
 ├─▶ PayHere Payment Gateway
 │
 ├─▶ Payment Callback
 │
 ├─▶ Verify MD5 Signature
 │
 ├─▶ Update Payment Status
 │
 └─▶ Confirm Booking
```

### 5.2 Payment Status Flow

```
PENDING ──▶ COMPLETED ──▶ Booking CONFIRMED
PENDING ──▶ FAILED
```

### 5.3 Authentication

| Endpoint | Auth Required | Reason |
|---|---|---|
| `POST /v1/payments/initiate` | ✅ Yes (Bearer JWT) | Only booking owner can initiate |
| `POST /v1/payments/callback` | ❌ No | PayHere servers call this directly |

```
Authorization: Bearer <JWT_TOKEN>
```

### 5.4 DTOs

#### PaymentRequest
Used to initiate a payment.

```json
{
  "bookingId": 10,
  "amount": 2500.00
}
```

| Field | Type | Required | Rule |
|---|---|---|---|
| `bookingId` | Long | Yes | Cannot be null |
| `amount` | Double | Yes | Cannot be null |

#### PaymentHashResponse
Returned when initiating payment — contains required info for opening the PayHere payment popup.

```json
{
  "orderId": "MM-AB12CD34",
  "amount": 2500.00,
  "currency": "LKR",
  "hash": "A8F23BC90...",
  "merchantId": "123456"
}
```

| Field | Type | Description |
|---|---|---|
| `orderId` | String | Unique payment order ID |
| `amount` | Double | Payment amount |
| `currency` | String | Payment currency |
| `hash` | String | PayHere generated security hash |
| `merchantId` | String | PayHere merchant ID |

#### PaymentResponse
Used to return payment information.

```json
{
  "paymentId": 5,
  "bookingId": 10,
  "orderId": "MM-AB12CD34",
  "amount": 2500.00,
  "currency": "LKR",
  "status": "COMPLETED",
  "createdAt": "2026-06-24T10:30:00"
}
```

| Field | Type | Description |
|---|---|---|
| `paymentId` | Long | Payment record ID |
| `bookingId` | Long | Related booking ID |
| `orderId` | String | Payment order reference |
| `amount` | Double | Paid amount |
| `currency` | String | Payment currency |
| `status` | PaymentStatus | Current payment status |
| `createdAt` | LocalDateTime | Payment creation time |

### 5.5 Endpoints

#### `POST /v1/payments/initiate`
Creates a payment record and generates a PayHere hash. **Only the booking owner can initiate payment.**

**Request Body:**
```json
{
  "bookingId": 10,
  "amount": 3000.00
}
```

**Success Response `200 OK`:**
```json
{
  "orderId": "MM-A12345BC",
  "amount": 3000.00,
  "currency": "LKR",
  "hash": "7B91ACD...",
  "merchantId": "123456"
}
```

**Business Rules (validated before hash generation):**

1. **Booking Exists** — Booking ID must exist in the system.
2. **Ownership Validation** — The logged-in user must own the booking. *(User A cannot pay for User B's booking.)*
3. **Booking Status Validation** — Payment only allowed when `Booking Status = PENDING`.
4. **Duplicate Payment Prevention** — If already completed, returns: `"Payment already completed for this booking"`.

**Error Responses:**

| Scenario | Status | Message |
|---|---|---|
| Booking not found | `404 NOT FOUND` | `Booking not found` |
| Not the booking owner | `401 UNAUTHORIZED` | `Unauthorized` |
| Booking not in PENDING state | `400 BAD REQUEST` | `Booking is not in PENDING state` |

---

#### `POST /v1/payments/callback`
Receives payment confirmation from the PayHere server. Verifies the callback signature before updating payment status. **No authentication required.**

**Request Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `merchant_id` | String | PayHere merchant ID |
| `order_id` | String | Generated order ID |
| `payment_id` | String | PayHere payment ID |
| `payhere_amount` | String | Payment amount |
| `payhere_currency` | String | Currency |
| `status_code` | String | Payment result |
| `md5sig` | String | Verification signature |

**Example Callback:**
```
merchant_id=123456
order_id=MM-AB12CD34
payment_id=987654
payhere_amount=3000.00
payhere_currency=LKR
status_code=2
md5sig=AABBCC123
```

**PayHere Status Codes:**

| Status Code | Meaning | System Action |
|---|---|---|
| `2` | Payment successful | Complete payment + confirm booking |
| `-1` | Cancelled | Mark failed |
| `-2` | Failed | Mark failed |
| `-3` | Chargeback | Mark failed |

**MD5 Signature Verification:**
```
MD5(
  merchant_id +
  order_id +
  amount +
  currency +
  status_code +
  MD5(secret)
)
```
Generated signature must match `md5sig`. If validation fails → `"Invalid MD5 signature"`.

**On Success:**
- Payment Status → `COMPLETED`
- Booking Status → `CONFIRMED`

---

#### `GET /v1/payments/booking/{bookingId}`
Retrieves payment information for a booking (e.g., `/v1/payments/booking/10`).

**Response `200 OK`:**
```json
{
  "paymentId": 5,
  "bookingId": 10,
  "orderId": "MM-AB12CD34",
  "amount": 2500,
  "currency": "LKR",
  "status": "COMPLETED"
}
```

### 5.6 Database Schema — `payments`

| Column | Type | Description |
|---|---|---|
| `id` | Long | Primary key |
| `booking_id` | Long | Related booking (FK) |
| `amount` | Double | Payment amount |
| `currency` | String | Currency code |
| `order_id` | String | Unique payment reference |
| `payhere_payment_id` | String | PayHere transaction ID |
| `hash` | String | Security hash |
| `status` | Enum | Payment status |
| `created_at` | DateTime | Creation timestamp |

> **Entity Relationship:** One Booking → One Payment (`@OneToOne`)

### 5.7 Repository Methods

| Method | Used For |
|---|---|
| `findByOrderId(String orderId)` | Callback processing |
| `findByBooking_Id(Long bookingId)` | Retrieving payment details |

### 5.8 Security Features

**Hash Generation**
Before sending a payment request, a merchant data + secret key → MD5 Hash is generated to prevent payment manipulation.

**Callback Verification**
Every PayHere callback is verified using MD5 signature to prevent:
- Fake payment requests
- Unauthorized status updates
- Payment tampering

### 5.9 Complete Payment Flow

```
 1. User creates booking
         │
 2. Booking Status = PENDING
         │
 3. User calls POST /v1/payments/initiate
         │
 4. Backend creates Payment record
         │
 5. Backend generates PayHere hash
         │
 6. User completes payment on PayHere
         │
 7. PayHere sends callback to POST /v1/payments/callback
         │
 8. Backend validates MD5 signature
         │
 9. Payment Status = COMPLETED
         │
10. Booking Status = CONFIRMED
```