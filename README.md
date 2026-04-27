# Dataset Kafka Producer (Spring Boot)

A minimal, layered Spring Boot Kafka producer that emits synthetic JSON records at startup. Supports a dry-run mode via conditional bean wiring (no broker needed) and configurable message count.

## Project Layout
```
src/main/java/com/hack/dataset/
  DatasetProducerApplication.java        # Spring Boot entrypoint
  config/ProducerProperties.java         # Binds producer.* settings
  core/DatasetProducerClient.java        # Abstraction for producing messages
  kafka/DatasetKafkaProducer.java        # Real KafkaTemplate-backed producer (active when dry-run=false)
  kafka/DryRunDatasetProducer.java       # Logs messages instead of sending (active when dry-run=true)
  runner/StartupProducerRunner.java      # Produces messages on application startup
  util/DataRecordGenerator.java          # Builds JSON payloads
```
Tests:
```
src/test/java/com/hack/dataset/runner/StartupProducerRunnerTest.java
src/test/java/com/hack/dataset/binding/DataGenerationAndBindingTest.java
```

## Architecture Overview
- Conditional beans (`@ConditionalOnProperty`) switch between real Kafka producer and dry-run implementation using `producer.dry-run` property.
- `StartupProducerRunner` injects `DatasetProducerClient` (interface) so it is agnostic of transport.
- Configuration is externalized in `application.yml` with environment variable overrides.

## Configuration (`application.yml`)
```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      properties:
        acks: all
        enable.idempotence: true

producer:
  topic: ${PRODUCER_TOPIC:dataset-topic}
  count: ${PRODUCER_COUNT:10}
  sleep-ms: ${PRODUCER_SLEEP_MS:0}
  dry-run: ${PRODUCER_DRY_RUN:false}

logging:
  level:
    root: info
    com.hack.dataset: info
```
Environment overrides (macOS/Linux examples):
```bash
export KAFKA_BOOTSTRAP=localhost:9092
export PRODUCER_TOPIC=my-topic
export PRODUCER_COUNT=25
export PRODUCER_SLEEP_MS=100
export PRODUCER_DRY_RUN=true   # activates DryRunDatasetProducer
```
Command-line argument overrides also work (Spring Boot will map them):
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--producer.topic=quick-topic --producer.count=5"
```

## Building & Running
```bash
# Build
mvn clean package

# Run with defaults (count=10, topic=dataset-topic)
mvn spring-boot:run

# Run dry-run mode (no Kafka broker needed)
export PRODUCER_DRY_RUN=true
mvn spring-boot:run

# Run jar directly
java -jar target/dataset-producer-1.0-SNAPSHOT.jar --producer.topic=jobs --producer.count=3
```

## Tests
Run all tests:
```bash
mvn test
```
Test coverage:
- `StartupProducerRunnerTest` – Ensures the runner invokes KafkaTemplate send the configured number of times.
- `DataGenerationAndBindingTest` – Validates JSON structure and property binding (executes in dry-run mode).

## Dry-Run vs Real Send
- Real producer: `DatasetKafkaProducer` active when `producer.dry-run=false` (default).
- Dry-run producer: `DryRunDatasetProducer` active when `producer.dry-run=true`. It logs each message: `[DRY-RUN] key=... value=...`.

## Cleaning Legacy Stubs
The old package `com.hack.dataset.producer` contained legacy classes now replaced. Safe to delete any residual stub files if still present (these may already be stripped to comments):
```
src/main/java/com/hack/dataset/producer/DatasetProducerApplication.java  # obsolete
```
`config.properties` is deprecated (all config now in `application.yml`). Remove if still present.

## Extending
- Add REST endpoint (e.g. `/produce?count=N`) for on-demand message generation.
- Add Micrometer metrics to track produced messages (`Counter` tagged by topic).
- Introduce structured logging with JSON layout.
- Swap serializers (Avro/Protobuf) by adding schema registry and adjusting `value-serializer`.

## Troubleshooting
| Symptom | Cause | Fix |
|---------|-------|-----|
| UNKNOWN_TOPIC_OR_PARTITION warnings | Topic not created | Create with kafka-topics.sh or use auto-create (if enabled) |
| Messages not appearing | `producer.dry-run=true` | Set `producer.dry-run=false` or unset env var |
| Slow startup | Large `PRODUCER_COUNT` + sleep | Lower count or remove `PRODUCER_SLEEP_MS` |
| Cannot connect to broker | Wrong `KAFKA_BOOTSTRAP` | Verify address, port, firewall |

## Quick Topic Creation (Local Kafka)
```bash
kafka-topics.sh --create \
  --topic dataset-topic \
  --bootstrap-server localhost:9092 \
  --partitions 3 --replication-factor 1
```

## License
Internal / sample project.
