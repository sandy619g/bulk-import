# Bulk User Importer

-  **Backend**: Java 21 + Kotlin (Spring Boot), pub/sub via Kotlin Coroutine Channels
-  **Frontend**: React with Vite (Node 22+)
-  **Build Tool**: Maven

---

## Prerequisites

### Backend
- Java 21
- Maven 3.9+
- Kotlin 2.0+

### Frontend
- Node.js 22+
- npm (v10+)

---

## Build Instructions

### Backend

```bash

cd doc-importer
mvn clean install
```

### Frontend

```bash

cd doc-uploader
npm install
npm run build
```

## Run Instructions

### Backend
```bash

cd doc-importer
mvn spring-boot:run
```

### Frontend
```bash

cd doc-uploader
npm run dev
```

## API Endpoints

| Method | URL            | Description                      |
|--------|----------------|----------------------------------|
| POST   | `/api/upload`  | Upload a CSV file                |
| GET    | `/api/status`  | Get processing status by file ID |

---

### Usage

---

#### POST `/api/upload`

- **Description**: Upload a CSV file for processing.
- **Content-Type**: `multipart/form-data`
- **Body**: Attach the CSV file with the key name `file`.

```bash

curl -X POST http://localhost:8080/api/upload \
  -F 'file=@data.csv'
```

#### GET `/api/status`

- **Description**: Check the processing status of a previously uploaded file.
- **Query Parameter**: id (string) – the file ID returned from /api/upload.

```bash

curl "http://localhost:8080/api/status?id=123"
```

## Design Notes

### Pub/Sub with Kotlin Coroutine Channels

We use Kotlin's `Channel` API to implement a lightweight in-memory **publish/subscribe** pattern:

- **Publisher**: When a CSV file is uploaded, its processing task is pushed into a coroutine `Channel`.
- **Subscriber**: A background coroutine receives messages from the channel and processes them asynchronously.

#### Benefits

- Simple and efficient asynchronous processing.
- Native Kotlin solution — no external brokers required.

#### Trade-offs

- In-memory `Channel` is **not durable** — data is lost if the service crashes.
- **Not distributed** — best for simple or demo-level apps.
- Easily replaceable with a production-ready system (e.g., **Kafka**, **RabbitMQ**) if needed.

---

### Example CSV Format (Expected)

```csv
id,firstName,lastName,email
1,Tony,Stark,ironman@gmail.com
2,Bruce,Banner,hulk@gmail.com
3,Steve,Roggers,capAmerica@gmail.com
