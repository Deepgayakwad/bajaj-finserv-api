# BFHL REST API

A production-ready REST API that processes a mixed array of inputs and returns categorized data.

---

## Endpoint

```
POST /bfhl
```

### Request Headers
| Header | Required | Description |
|---|---|---|
| `X-Request-Id` | Optional | Correlation ID, echoed in response |
| `Content-Type` | Required | `application/json` |

### Request Body
```json
{
  "data": ["A", "1", "22", "$", "B", "7"]
}
```

### Response
```json
{
  "is_success": true,
  "request_id": "REQ-1001",
  "odd_numbers": ["1", "7"],
  "even_numbers": ["22"],
  "alphabets": ["A", "B"],
  "special_characters": ["$"],
  "sum": "30",
  "largest_number": "22",
  "smallest_number": "1",
  "alphabet_count": 2,
  "number_count": 3,
  "special_character_count": 1,
  "contains_duplicates": false,
  "unique_element_count": 6,
  "sorted_numbers": ["1", "7", "22"],
  "vowel_count": 0,
  "consonant_count": 2,
  "alphabet_frequency": { "A": 1, "B": 1 },
  "processing_time_ms": 12,
  "summary": {
    "total_elements_received": 6,
    "valid_elements_processed": 6,
    "invalid_elements_ignored": 0
  }
}
```

---

## Features

- ✅ Categorizes numbers, alphabets, special characters, alphanumeric strings
- ✅ Splits alphanumeric strings (e.g., `A1B2` → letters + numbers)
- ✅ Handles negative numbers (`-10`) and decimals (`25.5`)
- ✅ Ignores null, empty, whitespace-only values
- ✅ Removes duplicates, tracks `contains_duplicates`
- ✅ Alphabet frequency map
- ✅ Vowel / consonant count
- ✅ Longest and shortest alphabetic value
- ✅ Sorted numbers (ascending)
- ✅ Processing time in ms
- ✅ Summary object (received / processed / ignored)
- ✅ Global exception handling
- ✅ Bean Validation

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Build | Maven |
| Testing | JUnit 5 + Mockito |
| Deployment | Render (Docker) |

---

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+

```bash
# Clone
git clone https://github.com/Deepgayakwad/bajaj-finserv-api.git
cd bajaj-finserv-api

# Build & Run
mvn spring-boot:run
```

API will be available at `http://localhost:8080/bfhl`

### Run Tests
```bash
mvn test
```

---

## Docker

```bash
docker build -t bfhl-api .
docker run -p 8080:8080 bfhl-api
```

---

## Project Structure

```
src/
├── main/java/com/bajaj/bfhl/
│   ├── BfhlApplication.java
│   ├── controller/
│   │   └── BfhlController.java
│   ├── service/
│   │   ├── BfhlService.java        (Interface)
│   │   └── BfhlServiceImpl.java    (Implementation)
│   ├── dto/
│   │   ├── BfhlRequest.java
│   │   └── BfhlResponse.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── InvalidInputException.java
└── test/java/com/bajaj/bfhl/
    ├── service/
    │   └── BfhlServiceImplTest.java
    └── integration/
        └── BfhlControllerIntegrationTest.java
```
