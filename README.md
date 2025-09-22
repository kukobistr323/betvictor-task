# Word Counter Task – Java Software Engineer

This project is an implementation of a coding task for a Java Software Engineer position.  
It consists of two Spring Boot services that communicate via Kafka:

- **processing-service**  
  Fetches random text from [hipsum.co](https://hipsum.co), analyzes it, computes statistics, and publishes results to Kafka.

- **repository-service**  
  Consumes the processed results from Kafka, persists them in PostgreSQL, and exposes the latest 10 results over a REST endpoint.

---

## Architecture Overview

```
Client -> processing-service -> Kafka -> repository-service -> PostgreSQL
```

- **processing-service**
    - `/betvictor/text?p=N` endpoint
    - Calls hipsum.co `N` times (in parallel), computes:
        - most frequent word
        - average paragraph size
        - average paragraph processing time
        - total processing time
    - Returns JSON response and publishes the same result to Kafka (topic: `words.processed`).

- **repository-service**
    - Consumes messages from Kafka (`words.processed`)
    - Persists them into PostgreSQL (with Liquibase-managed schema)
    - Exposes `/betvictor/history` to return the last 10 records in JSON (snake_case).

---

## Prerequisites

- **Java 21**
- **Maven 3.9+**
- **Docker & Docker Compose** (for Kafka and Postgres)
- **IntelliJ IDEA** (recommended)

---

## Running the Project

### 1. Start infrastructure (Kafka, Zookeeper, PostgreSQL)
From the project root:

```bash
docker compose up -d
```

This starts:
- Zookeeper (port `2181`)
- Kafka broker (external port `9093`)
- PostgreSQL (port `5432`, db: `bv_history`, user: `postgres`, password: `postgres`)

### 2. Run processing-service
From the project root:

```bash
./mvnw -pl processing-service spring-boot:run -Dspring-boot.run.profiles=local
```

It will start on port `8080`.

### 3. Run repository-service
From the project root:

```bash
./mvnw -pl repository-service spring-boot:run -Dspring-boot.run.profiles=local
```

It will start on port `8081`.

---

## Usage

### Request text processing

```bash
curl "http://localhost:8080/betvictor/text?p=3"
```

Example response:

```json
{
  "freq_word": "hipster",
  "avg_paragraph_size": 12.3,
  "avg_paragraph_processing_time": 5.4,
  "total_processing_time": 45.2
}
```

The same payload is published to Kafka (`words.processed`).

### Get processing history

```bash
curl "http://localhost:8081/betvictor/history"
```

Example response (last 10 records):

```json
[
  {
    "freq_word": "hipster",
    "avg_paragraph_size": 12.3,
    "avg_paragraph_processing_time": 5.4,
    "total_processing_time": 45.2,
    "created_at": "2025-08-31T21:30:12.123+02:00"
  }
]
```

---

## Configuration

- **processing-service**
    - Hipsum client configuration in `application-local.yml`
    - Kafka topic configurable via `app.kafka.topic` (default: `words.processed`)

- **repository-service**
    - Uses Spring Data JPA with PostgreSQL
    - Database schema managed by Liquibase
    - Reads from Kafka topic `words.processed`

---

## Developer Notes

- Profiles:
    - `local` → uses Docker Kafka/Postgres
- To clean infra:
  ```bash
  docker compose down -v
  ```
- Logs are configured with `DEBUG` level for application packages when using `local` profile.
- Tie-breaking rule: when multiple words have the same frequency, the **lexicographically smallest** word is chosen (deterministic).

---

## Future Improvements

- Use **Avro/Protobuf** for Kafka payloads (currently JSON).
- Add **integration tests** with Testcontainers for Kafka/Postgres.
- Add CI pipeline to build & test automatically.

---
