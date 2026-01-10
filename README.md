# Kafka Spring Boot Demo Application

A professionally structured Spring Boot application demonstrating Apache Kafka producer and consumer patterns with clean, readable code and comprehensive documentation.

## 🎯 Project Overview

This project showcases:
- ✅ Spring Boot 3.2 integration with Apache Kafka
- ✅ Producer component with batch processing support
- ✅ Consumer listener with @KafkaListener annotation
- ✅ REST API endpoints for message publishing
- ✅ Clean, well-documented code with extensive comments
- ✅ Configuration management via application.yml
- ✅ Comprehensive logging and monitoring
- ✅ Error handling and reliability patterns

## 📁 Project Structure

```
kafka-container/
├── pom.xml                          # Maven configuration (Spring Boot 3.2)
├── README.md                        # This file
├── KAFKA_KNOWLEDGE_BASE.md          # Comprehensive Kafka learning guide & interview Q&A
└── src/
    ├── main/
    │   ├── java/
    │   │   └── org/kafka/demo/
    │   │       ├── KafkaUtil.java                    # Spring Boot main class
    │   │       ├── constant/
    │   │       │   └── KafkaConstants.java           # Configuration constants
    │   │       ├── producer/
    │   │       │   └── KafkaProducer.java            # Producer component
    │   │       ├── consumer/
    │   │       │   └── KafkaConsumer.java            # Consumer listener
    │   │       ├── controller/
    │   │       │   └── MessageController.java        # REST API controller
    │   │       └── dto/
    │   │           └── MessageRequest.java           # Request DTO
    │   └── resources/
    │       └── application.yml                       # Spring Boot configuration
    └── test/
        └── java/                                    # Test files
```

## 🛠 Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Maven 3.8+**
- **Apache Kafka 3.0+** (local or Docker)
- **Git** (optional)

## 🚀 Setup Instructions

### 1. Install Apache Kafka

#### Option A: Using Docker Compose (Recommended)
Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

Start: `docker-compose up -d`

#### Option B: Local Installation
```bash
# Download Kafka
wget https://archive.apache.org/dist/kafka/3.5.0/kafka_2.13-3.5.0.tgz

# Extract
tar -xzf kafka_2.13-3.5.0.tgz
cd kafka_2.13-3.5.0
```

### 2. Start Kafka (Local Setup)

```bash
# Terminal 1: Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Terminal 2: Start Kafka Broker
bin/kafka-server-start.sh config/server.properties
```

### 3. Create Test Topic

```bash
bin/kafka-topics.sh --create \
  --topic test-topic \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

### 4. Build and Run Application

```bash
# Build with Maven
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR directly
java -jar target/kafka-demo-app.jar
```

Application runs on: `http://localhost:8080`

## 📡 REST API Endpoints

### Health Check
```bash
curl -X GET http://localhost:8080/api/kafka/health

# Response:
{
  "status": "UP",
  "message": "Kafka API is running"
}
```

### Send Simple Message
```bash
curl -X POST http://localhost:8080/api/kafka/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello Kafka"}'
```

### Send Message with Key
```bash
curl -X POST http://localhost:8080/api/kafka/send-with-key \
  -H "Content-Type: application/json" \
  -d '{"key": "user-123", "message": "User logged in"}'
```

### Send to Custom Topic
```bash
curl -X POST http://localhost:8080/api/kafka/send/custom-topic \
  -H "Content-Type: application/json" \
  -d '{"message": "Custom topic message"}'
```

## ⚙️ Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092         # Broker address
    producer:
      acks: all                               # Durability: all, 1, or 0
      retries: 3                              # Retry attempts
      batch-size: 32768                       # Batch size in bytes
      linger-ms: 100                          # Wait time for batching
    consumer:
      group-id: consumerGroup10               # Consumer group
      auto-offset-reset: earliest             # Start from: earliest, latest, none
      max-poll-records: 1                     # Records per poll
```

## 📊 Monitoring

### View Application Logs
```bash
tail -f logs/kafka-app.log
```

### Check Consumer Lag
```bash
bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group consumerGroup10 \
  --describe
```

### List Topics and Partitions
```bash
bin/kafka-topics.sh \
  --list \
  --bootstrap-server localhost:9092
  
# Describe specific topic
bin/kafka-topics.sh \
  --describe \
  --topic test-topic \
  --bootstrap-server localhost:9092
```

## 💻 Code Examples

### Producer Usage
```java
@Autowired
private KafkaProducer kafkaProducer;

// Send simple message
kafkaProducer.sendMessage("my-topic", "Hello Kafka");

// Send with key (same key = same partition)
kafkaProducer.sendMessageWithKey("my-topic", "user-123", "Login event");

// Send batch
List<String> messages = Arrays.asList("msg1", "msg2", "msg3");
kafkaProducer.sendBatchMessages("my-topic", messages);
```

### Consumer Usage
The consumer is automatically listening:

```java
@KafkaListener(topics = "test-topic", groupId = "consumerGroup10")
public void consume(
    @Payload String message,
    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
    @Header(KafkaHeaders.OFFSET) long offset) {
    
    log.info("Received: {} from partition {} at offset {}", 
             message, partition, offset);
}
```

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Integration Tests with Embedded Kafka
```java
@SpringBootTest
@EmbeddedKafka(partitions = 1)
public class KafkaIntegrationTest {
    @Test
    public void testMessageProduction() {
        // Test implementation
    }
}
```

## ⚡ Performance Tuning

### Producer Optimization
```yaml
batch-size: 32768              # Larger batches
linger-ms: 100                 # Wait time for batching
compression-type: snappy       # Compression
```

### Consumer Optimization
```yaml
max-poll-records: 500          # More records per fetch
fetch-min-bytes: 1024          # Minimum wait size
fetch-max-wait-ms: 500         # Max wait time
```

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection Refused | Check if Kafka broker is running |
| Topic Not Found | Create topic with kafka-topics.sh |
| Consumer Lag Increasing | Add more consumers (up to partition count) |
| Messages Lost | Set `acks: all` and increase `replication-factor` |
| Slow Processing | Increase `max-poll-records` and `batch-size` |

## 📚 Learning Resources

### Comprehensive Kafka Guide
See [KAFKA_KNOWLEDGE_BASE.md](KAFKA_KNOWLEDGE_BASE.md) for:
- ✅ Kafka architecture and components
- ✅ Producer and consumer patterns
- ✅ Advanced topics (transactions, compaction, etc.)
- ✅ 18+ interview questions with answers
- ✅ Real-world design scenarios
- ✅ Performance optimization tips
- ✅ Complete glossary

### Quick Reference
- **Topics**: Data categories (like database tables)
- **Partitions**: Physical storage units (for parallelism)
- **Producers**: Write messages to topics
- **Consumers**: Read messages from topics
- **Consumer Groups**: Distribute load across consumers
- **Offsets**: Message position in partition

## 📦 Dependencies

```xml
- Spring Boot 3.2.0
- Spring Kafka
- Apache Kafka Clients (Latest)
- Lombok 1.18.30
- Jackson (JSON)
```

## ✨ Best Practices Implemented

✅ **Dependency Injection**: Spring components
✅ **Component Architecture**: Separation of concerns
✅ **Comprehensive Logging**: Debug and info logs
✅ **Exception Handling**: Proper error management
✅ **Documentation**: Javadoc comments throughout
✅ **RESTful Design**: Standard HTTP methods
✅ **Configuration Management**: Externalized config
✅ **Batch Processing**: Handle multiple messages
✅ **Manual Commits**: Reliable offset management

## 🎓 Next Steps

1. **Explore Code**: Review well-commented source code
2. **Read Guide**: Check [KAFKA_KNOWLEDGE_BASE.md](KAFKA_KNOWLEDGE_BASE.md)
3. **Try API**: Use curl to test endpoints
4. **Interview Prep**: Study 18+ Q&A in knowledge base
5. **Customize**: Modify for your use case
6. **Scale**: Add more producers/consumers

## 🤝 Contributing

Extend this project with:
- Advanced stream processing examples
- Complex topology demonstrations
- Performance benchmarks
- Additional test coverage
- Real-world use case implementations

---

*Made with ❤️ for Kafka learners and professionals*

**For comprehensive Kafka learning, interview prep, and architecture patterns, see [KAFKA_KNOWLEDGE_BASE.md](KAFKA_KNOWLEDGE_BASE.md)**

   docker run -d --name zookeeper -p 2181:2181 wurstmeister/zookeeper
   
   docker pull wurstmeister/kafka
   
   docker run -d --name kafka --link zookeeper:zookeeper -p 9092:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e 
   KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT -e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 wurstmeister/kafka

➠ Access the Kafka Container

   docker exec -it kafka /bin/bash
   
   List Consumer Groups:
   kafka-consumer-groups.sh --list --bootstrap-server localhost:9092
   
   Describe consumer group:
   kafka-consumer-groups.sh --describe --group <group_id> --bootstrap-server localhost:9092
   
   Run Kafka Console Consumer:
   kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic my-topic --from-beginning

   To build a jar : mvn package
   
   To upload in local maven repo : mvn install


