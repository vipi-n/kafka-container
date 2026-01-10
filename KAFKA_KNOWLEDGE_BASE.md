# Kafka Learning Guide - Comprehensive Knowledge Base

## Table of Contents
1. [Introduction to Kafka](#introduction-to-kafka)
2. [Kafka Architecture](#kafka-architecture)
3. [Core Concepts](#core-concepts)
4. [Producer Details](#producer-details)
5. [Consumer Details](#consumer-details)
6. [Advanced Topics](#advanced-topics)
7. [Interview Questions](#interview-questions)
8. [Spring Boot Integration](#spring-boot-integration)

---

## Introduction to Kafka

### What is Apache Kafka?

Apache Kafka is a **distributed event streaming platform** capable of handling trillions of events a day. It was originally developed at LinkedIn and is now maintained by the Apache Software Foundation.

### Why Use Kafka?

1. **High Throughput**: Handles millions of messages per second
2. **Low Latency**: Real-time message processing
3. **Durability**: Messages are persisted on disk
4. **Scalability**: Easy to scale horizontally
5. **Fault Tolerance**: Replication ensures data availability
6. **Stream Processing**: Supports real-time stream analytics
7. **Integration**: Works with various technologies (databases, data warehouses, etc.)

### Key Characteristics

- **Event Streaming Platform**: Captures data in real-time from event sources
- **Distributed System**: Runs as a cluster of servers
- **Pub-Sub Model**: Follows the publish-subscribe messaging pattern
- **Persistent Storage**: Stores data on disk, not just in memory
- **Ordered**: Maintains message ordering within a partition

### Use Cases

- Real-time analytics
- Log aggregation
- Stream processing
- Website activity tracking
- IoT sensor data collection
- E-commerce platform events
- Financial transaction monitoring
- Microservice communication

---

## Kafka Architecture

### Basic Components

```
┌─────────────────────────────────────────────────────────────┐
│                    KAFKA CLUSTER                             │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │   Broker 1   │  │   Broker 2   │  │   Broker 3   │       │
│  │              │  │              │  │              │       │
│  │ Partitions   │  │ Partitions   │  │ Partitions   │       │
│  │ 0, 1, 2      │  │ 0, 1, 2      │  │ 0, 1, 2      │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
│         ▲                  ▲                  ▲               │
│         │                  │                  │               │
│  ┌──────┴──────────────────┴──────────────────┴──────┐       │
│  │                ZooKeeper / KRaft                    │       │
│  │          (Cluster Coordination)                     │       │
│  └────────────────────────────────────────────────────┘       │
│                                                               │
└─────────────────────────────────────────────────────────────┘

     ▲ Producers                    ▼ Consumers
```

### Broker

A **Broker** is a Kafka server that stores data and serves clients. Key responsibilities:

- Store messages in partitions
- Replicate data to other brokers
- Serve fetch requests from consumers
- Coordinate with ZooKeeper/KRaft

**Broker Properties**:
- `broker.id`: Unique identifier for each broker
- `listeners`: Network interface to listen on
- `advertised.listeners`: Address clients use to connect
- `log.dirs`: Directory where partition data is stored

### Topic

A **Topic** is a category or feed name to which records are published.

**Characteristics**:
- Consists of one or more partitions
- Distributed across multiple brokers
- Can be created, deleted, and modified dynamically
- Is identified by a unique name

**Topic Example**:
```
Topic: "user-events"
├── Partition 0: [msg1, msg2, msg3]
├── Partition 1: [msg4, msg5, msg6]
└── Partition 2: [msg7, msg8, msg9]
```

### Partition

A **Partition** is the unit of parallelism in Kafka.

**Key Points**:
- Each partition is an ordered, immutable sequence of messages
- Has a unique offset for each message
- Can be replicated across brokers
- Messages within a partition maintain order
- Enables parallel consumption

**Partition Structure**:
```
Partition 0:
┌─────┬─────┬─────┬─────┬─────┐
│msg0 │msg1 │msg2 │msg3 │msg4 │ ← Messages in order
│ off0│ off1│ off2│ off3│ off4│ ← Offsets (0, 1, 2, 3, 4)
└─────┴─────┴─────┴─────┴─────┘
```

### Replication

**Replication Factor**: Number of copies of each partition across brokers.

```
Topic with Replication Factor = 3:

Partition 0:
  Leader (Broker 1)     - Handles all reads/writes
  Replica (Broker 2)    - Standby copy
  Replica (Broker 3)    - Standby copy

Partition 1:
  Leader (Broker 2)
  Replica (Broker 3)
  Replica (Broker 1)
```

**Benefits**:
- Fault tolerance: If leader fails, a replica becomes new leader
- High availability: Ensure no data loss
- Load balancing across brokers

### Consumer Group

A **Consumer Group** is a group of consumers that work together to consume messages from topics.

**Key Concepts**:
- Multiple consumers in a group share the workload
- Each partition is assigned to only one consumer per group
- Different groups can consume same topic independently
- Offset is tracked per group

**Example**:
```
Topic: "orders" (3 partitions)

Consumer Group 1:
  Consumer 1 → Partition 0
  Consumer 2 → Partition 1
  Consumer 3 → Partition 2

Consumer Group 2:
  Consumer 1 → Partitions 0, 1, 2 (one consumer handles all)
```

### Log Retention

Kafka stores messages on disk for a configurable retention period:

- **Time-based**: `log.retention.hours: 168` (7 days)
- **Size-based**: `log.retention.bytes: 1073741824` (1GB)
- **Offset-based**: Compact logs by key

---

## Core Concepts

### Message/Record

A **Message** or **Record** is a key-value pair with metadata:

```
Record:
{
  key: "user:123",
  value: {
    "name": "John Doe",
    "action": "login",
    "timestamp": "2024-01-11T10:30:00Z"
  },
  timestamp: 1705000200000,
  partition: 1,
  offset: 1042
}
```

### Offset

An **Offset** is a sequential ID given to messages within a partition.

- Starts from 0
- Unique within a partition
- Immutable (doesn't change)
- Used by consumers to track read position

```
Partition 0:
Offset → 0, 1, 2, 3, 4, 5, 6, 7, 8, 9
Message → [m1, m2, m3, m4, m5, m6, m7, m8, m9, m10]
```

### Serialization & Deserialization

**Serialization**: Converting Java objects to bytes for Kafka
**Deserialization**: Converting bytes back to Java objects

**Common Serializers**:
- `StringSerializer`: String ↔ bytes
- `IntegerSerializer`: Integer ↔ bytes
- `LongSerializer`: Long ↔ bytes
- `JsonSerde`: JSON objects ↔ bytes (custom)
- `AvroSerde`: Avro format (schema evolution)
- `ProtobufSerde`: Protocol Buffers

### Acks Configuration (Producer)

Controls when a message is considered "sent":

1. **acks=0** (Fire-and-forget)
   - Producer doesn't wait for acknowledgment
   - Fastest but least reliable
   - May lose messages

2. **acks=1** (Leader acknowledgment)
   - Leader broker acknowledges receipt
   - Message is written to leader's log
   - Replicas may not have the message yet
   - Risk of message loss if leader crashes before replication

3. **acks=all** (In-Sync Replica acknowledgment)
   - All in-sync replicas must acknowledge
   - Slowest but most reliable
   - No message loss (durability guaranteed)

```
Comparison:
┌─────────────────┬──────────────┬─────────────────┬──────────────┐
│     Acks        │  Speed       │    Durability   │  Use Case    │
├─────────────────┼──────────────┼─────────────────┼──────────────┤
│ acks=0          │ Very Fast    │ Low             │ Logs         │
│ acks=1          │ Medium       │ Medium          │ Events       │
│ acks=all        │ Slow         │ Very High       │ Critical     │
└─────────────────┴──────────────┴─────────────────┴──────────────┘
```

### Idempotent Producer

Ensures messages are delivered exactly once:

```java
// Enable idempotency
properties.put("enable.idempotence", "true");
// Also sets acks=all and retries=Integer.MAX_VALUE automatically
```

**How it works**:
- Assigns unique sequence number to each message
- Broker deduplicates messages with same sequence number
- Prevents duplicate messages on retries

---

## Producer Details

### What is a Producer?

A **Producer** is a client application that publishes (writes) messages to Kafka topics.

### Producer Configuration

```properties
bootstrap.servers=localhost:9092
key.serializer=org.apache.kafka.common.serialization.StringSerializer
value.serializer=org.apache.kafka.common.serialization.StringSerializer

# Performance tuning
batch.size=16384                # Bytes to batch messages
linger.ms=10                    # Wait time for batching
compression.type=snappy         # Compression (snappy, lz4, gzip)

# Reliability
acks=all                        # All replicas must acknowledge
retries=3                       # Retry failed sends
max.in.flight.requests.per.connection=5

# Idempotency
enable.idempotence=true         # Exactly-once semantics
```

### Producer Message Flow

```
1. Create ProducerRecord
        ↓
2. Serializer (convert to bytes)
        ↓
3. Partitioner (determine partition)
        ↓
4. Batch messages if possible
        ↓
5. Send to broker
        ↓
6. Broker appends to partition log
        ↓
7. Replicate to other brokers
        ↓
8. Send acknowledgment back to producer
        ↓
9. Producer callback (success/failure)
```

### Partitioning Strategy

**How partitions are determined**:

1. **If key is provided**:
   - Hash the key
   - `partition = hash(key) % number_of_partitions`
   - Same key → same partition → ordered delivery

2. **If key is null**:
   - Round-robin across partitions (default)
   - Good distribution but no ordering guarantees

3. **Custom Partitioner**:
   ```java
   public class CustomPartitioner implements Partitioner {
       public int partition(String topic, Object key, byte[] keyBytes,
                           Object value, byte[] valueBytes,
                           Cluster cluster) {
           // Custom logic
           return partitionNumber;
       }
   }
   ```

### Producer Example (Spring Boot)

```java
@Component
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
    
    public void sendWithKey(String topic, String key, String value) {
        kafkaTemplate.send(topic, key, value);
    }
}
```

---

## Consumer Details

### What is a Consumer?

A **Consumer** is a client application that reads (subscribes to) messages from Kafka topics.

### Consumer Configuration

```properties
bootstrap.servers=localhost:9092
group.id=my-consumer-group
key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

# Consumption behavior
auto.offset.reset=earliest         # Action on startup: earliest or latest
enable.auto.commit=true            # Auto-commit offset
auto.commit.interval.ms=1000       # Commit interval
max.poll.records=500               # Max records per poll
session.timeout.ms=30000           # Heartbeat timeout

# Fetch settings
fetch.min.bytes=1                  # Minimum bytes to wait for
fetch.max.wait.ms=500              # Max wait time for fetch
```

### Offset Management

**How consumers track position**:

```
Topic: "events"
Partition 0:
┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┐
│ 0 │ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │ 8 │ 9 │ ← Offsets
└───┴───┴───┴───┴───┴───┴───┴───┴───┴───┘
                        ▲
                        └─ Consumer position
                           (has read up to offset 5)
```

**Offset Management Types**:

1. **Automatic Commit**:
   ```
   enable.auto.commit=true
   auto.commit.interval.ms=1000
   
   // Offset automatically committed every 1 second
   // Simple but risk of message loss if app crashes
   ```

2. **Manual Commit**:
   ```
   enable.auto.commit=false
   
   // Consumer explicitly commits offset after processing
   consumer.commitSync();      // Synchronous
   consumer.commitAsync();     // Asynchronous
   
   // Better control and reliability
   ```

3. **Seek to Specific Offset**:
   ```java
   TopicPartition partition = new TopicPartition("topic", 0);
   consumer.seek(partition, 10);  // Start from offset 10
   ```

### Consumer Lag

**Consumer Lag** = Latest offset in partition - Consumer offset

```
Latest offset in partition: 1000
Consumer offset:            950
Consumer Lag:               50 (behind by 50 messages)
```

**Importance**:
- Indicates how far behind consumer is
- Used for monitoring and alerting
- If lag is high, consumer needs scaling

### Rebalancing

**Rebalancing** occurs when:
- New consumer joins the group
- Consumer leaves the group
- Consumer is considered dead (timeout)
- Topic assignment changes

**Rebalancing Process**:
```
1. Consumer detects rebalance
2. All consumers stop processing
3. Group Coordinator assigns partitions
4. Consumers resume processing
     ↓
   Pause in message processing
```

**Types**:
- **Eager Rebalance**: Stop all, rebalance, resume (downtime)
- **Cooperative Rebalance**: Incremental reassignment (reduced downtime)

### Consumer Example (Spring Boot)

```java
@Component
public class KafkaConsumer {
    
    @KafkaListener(topics = "my-topic", groupId = "my-group")
    public void consume(String message,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                       @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received: {} from partition {} with offset {}", 
                 message, partition, offset);
    }
}
```

---

## Advanced Topics

### Exactly-Once Semantics

**Three possible delivery guarantees**:

1. **At-Most-Once**: 
   - Message sent once, may not be delivered
   - acks=0
   - Risk of data loss
   - Fast

2. **At-Least-Once**: 
   - Message delivered at least once
   - May have duplicates
   - acks=1 or acks=all
   - Better reliability

3. **Exactly-Once** (Idempotent Producer):
   - Message delivered exactly once
   - No duplicates, no loss
   - Requires:
     - Idempotent producer (enable.idempotence=true)
     - Transactional writes
     - Unique producer ID
   - Slowest but most reliable

### Transactions

**Kafka Transactions** ensure atomic writes across partitions:

```java
// Producer with transactions
KafkaProducer producer = new KafkaProducer<>(props);
producer.initTransactions();

try {
    producer.beginTransaction();
    producer.send(record1);
    producer.send(record2);
    producer.commitTransaction();
} catch (Exception e) {
    producer.abortTransaction();
}
```

**Use Cases**:
- Multi-partition writes
- Exactly-once processing
- Atomic updates across topics

### Compacted Topics

**Log Compaction**: Retains only the latest value for each key.

```
Before Compaction:
key → [v1, v2, v3, v2, v4, v1, v3]

After Compaction:
key → [v4]  (only latest value retained)
```

**Configuration**:
```properties
cleanup.policy=compact
min.compaction.lag.ms=0          # How long to wait before compacting
delete.retention.ms=86400000     # How long to keep deleted keys
```

**Use Cases**:
- Configuration topics
- State stores
- User profiles

### Stream Processing Patterns

#### Kafka Streams

High-level API for stream processing:

```java
StreamsBuilder builder = new StreamsBuilder();

builder.stream("input-topic")
    .filter((key, value) -> value.length() > 0)
    .mapValues(value -> value.toUpperCase())
    .to("output-topic");

KafkaStreams streams = new KafkaStreams(
    builder.build(),
    streamsProperties
);
streams.start();
```

#### Common Operations

```
1. Map: Transform each record
2. Filter: Keep only matching records
3. Flatmap: One input → multiple outputs
4. Join: Combine streams by key
5. Aggregate: Group and summarize
6. Window: Time-based grouping
```

### Multi-Tenancy

Supporting multiple independent clients on single cluster:

```
Topic Naming Convention:
tenant-{tenantId}-{functionName}

Example:
├── tenant-acme-orders
├── tenant-acme-payments
├── tenant-globex-orders
└── tenant-globex-payments

Consumer Groups:
└── {tenantId}-consumer-group
```

---

## Interview Questions

### Fundamental Questions

**Q1: What is Apache Kafka and why is it used?**

A: Apache Kafka is a distributed event streaming platform that:
- Handles high-throughput, real-time data streaming
- Persists messages on disk for durability
- Ensures fault tolerance through replication
- Provides scalability through partitioning
- Supports pub-sub and event streaming patterns

Common use cases: log aggregation, real-time analytics, microservices communication, IoT data.

---

**Q2: Explain the difference between a Topic and a Partition.**

A: 
- **Topic**: Logical category for messages (like a database table)
  - Can span multiple brokers
  - Has a name and configuration
  - Contains one or more partitions

- **Partition**: Physical storage unit (like a shard)
  - Ordered, immutable sequence
  - Lives on a broker
  - Unit of parallelism
  - Each message has unique offset within partition

---

**Q3: What is a Consumer Group?**

A: A group of consumers that work together:
- Each partition is consumed by only one consumer in group
- Multiple groups can independently consume same topic
- Enables load balancing and fault tolerance
- Automatically rebalances when consumers join/leave
- Tracks offsets per group

Example: 3 consumers in group consuming 3-partition topic → each consumer gets 1 partition.

---

**Q4: Explain Producer Acks Configuration.**

A:
- **acks=0**: Fire-and-forget, no acknowledgment needed
  - Fastest, least reliable
  - Use for non-critical logs

- **acks=1**: Leader acknowledges after writing
  - Medium speed and reliability
  - Risk if leader crashes before replication

- **acks=all**: All in-sync replicas acknowledge
  - Slowest, most reliable
  - No data loss guarantee
  - Use for critical data

---

**Q5: How does Kafka ensure data durability?**

A: Through multiple mechanisms:
1. **Replication**: Multiple copies on different brokers
2. **Persistence**: Messages written to disk (log-structured storage)
3. **In-Sync Replicas (ISR)**: Min replicas in sync before acknowledging
4. **Replication Factor**: Configurable copies of each partition
5. **Min ISR**: Minimum replicas required for acknowledgment
6. **Log Segments**: Immutable segments for data protection

---

**Q6: What happens during a Consumer Group Rebalance?**

A: Process:
1. Coordinator detects change (consumer join/leave/timeout)
2. Revoke generation signal sent to consumers
3. Consumers stop consuming and commit offset
4. Coordinator assigns new partition distribution
5. Consumers resume consuming from new assignments

Downtime considerations:
- Processing pauses during rebalance
- Can be optimized with cooperative rebalancing
- Minimize with consumer session configuration

---

**Q7: Explain Consumer Lag and its importance.**

A: 
**Definition**: Lag = Latest offset - Consumer offset

**Importance**:
- Indicates consumer behind schedule
- High lag means messages pile up
- Used for performance monitoring
- Helps determine if scaling needed

**Monitoring**:
```
Lag < 100 → Consumer healthy
Lag = 1000 → Consumer struggling
Lag = 10000+ → Critical, needs scaling
```

---

**Q8: What are Idempotent Producers?**

A: Producers that send messages exactly once:
- Enable: `enable.idempotence=true`
- Automatically sets:
  - acks=all
  - retries=Integer.MAX_VALUE
  - max.in.flight.requests.per.connection=5

**How it works**:
- Assigns sequence number to each message
- Broker detects and deduplicates duplicates
- Prevents duplicates on retries

**Trade-off**: Slight performance overhead for guarantee.

---

**Q9: Difference between Traditional Message Queues and Kafka.**

A:

| Aspect | Traditional MQ | Kafka |
|--------|---|---|
| **Model** | Pull (mostly) | Pub-Sub & Pull |
| **Retention** | Deletes after consumption | Persists indefinitely |
| **Scalability** | Vertical | Horizontal |
| **Throughput** | Lower (millions/sec) | Higher (billions/sec) |
| **Multiple Consumers** | Limited | Full support |
| **Log Structure** | Not sequential | Append-only log |
| **Use Case** | Transaction queues | Event streaming |

---

**Q10: What is Log Compaction?**

A: Mechanism to remove historical values, keeping only latest:

```
Before:
key-1 → [v1, v2, v3, v2, v4, v1, v3]
key-2 → [a, b, c, d, e]

After Compaction:
key-1 → [v4]
key-2 → [e]
```

**Use Cases**:
- Configuration state topics
- Changelog topics
- User profile updates

**Configuration**:
```
cleanup.policy=compact
log.cleaner.enable=true
```

---

### Architecture & Design Questions

**Q11: Design a real-time analytics system using Kafka.**

A: Architecture:

```
Data Sources
    ↓
[Kafka Producer] → Topic: raw-events
    ↓
[Kafka Streams/Spark] → Processing Layer
    ↓
[Kafka Producer] → Topic: processed-events
    ↓
[Consumer] → Analytics DB / Data Warehouse
```

Components:
- Event sources stream to raw-events topic
- Stream processors aggregate, filter, enrich
- Results written to analytics topics
- Consumers push to databases for querying

---

**Q12: How would you handle exactly-once message processing?**

A: Multi-layer approach:

1. **Producer Level**: Idempotent producer
2. **Message Level**: Unique message ID
3. **Consumer Level**: Manual offset commit after processing
4. **Processing Level**: Idempotent operations
5. **Database Level**: Unique constraint on message ID

```java
@Transactional
public void processMessage(String message) {
    if (!messageProcessed(message.getId())) {
        // Process message
        save(message);
        recordProcessed(message.getId());
        consumer.commitSync();
    }
}
```

---

**Q13: Design a distributed logging system using Kafka.**

A: Architecture:

```
Application Servers
    ↓ (logs)
[Kafka Producer] → Topics by log level
    ├── logs-error
    ├── logs-warn
    ├── logs-info
    ↓
[Consumers]
    ├─→ Elasticsearch (indexing)
    ├─→ S3 (archival)
    └─→ Splunk (analysis)
```

Benefits:
- Decoupled log sources from processors
- Easy to add new processors
- Reliable delivery
- Scalable to millions of logs/sec

---

### Performance & Optimization Questions

**Q14: How would you optimize Kafka Producer Performance?**

A: Techniques:

1. **Batching**:
   ```
   batch.size=32768         # Larger batch
   linger.ms=100            # Wait for batch fill
   ```

2. **Compression**:
   ```
   compression.type=snappy  # CPU vs bandwidth trade-off
   ```

3. **Async Sends**:
   ```java
   producer.send(record, callback);  // Non-blocking
   ```

4. **Connection Pooling**:
   ```
   connections.max.idle.ms=540000
   ```

5. **Tuning**:
   ```
   buffer.memory=67108864           # Producer buffer
   max.in.flight.requests.per.connection=5
   ```

---

**Q15: How would you optimize Kafka Consumer Performance?**

A: Techniques:

1. **Parallel Processing**:
   - Multiple consumer instances
   - Process each partition in thread pool

2. **Batch Processing**:
   ```
   max.poll.records=500     # Fetch more at once
   ```

3. **Offset Management**:
   ```
   enable.auto.commit=false # Manual commit after batch
   ```

4. **Connection Settings**:
   ```
   fetch.min.bytes=1024     # Minimum to wait for
   fetch.max.wait.ms=500    # Max wait time
   ```

5. **Consumer Scaling**:
   - Add more consumers (up to partition count)
   - Each consumer gets dedicated partition

---

### Common Issues & Troubleshooting

**Q16: What causes high consumer lag?**

A: Common causes:

1. **Insufficient consumers**: Not enough parallelism
   - Solution: Add more consumers (up to partition count)

2. **Slow processing**: Application is slow
   - Solution: Optimize processing logic, use batch operations

3. **GC pauses**: Garbage collection stalls
   - Solution: Tune JVM heap size, use G1GC

4. **Network issues**: Slow broker communication
   - Solution: Check network, increase fetch size

5. **Rebalancing**: Frequent rebalance overhead
   - Solution: Tune session.timeout.ms, heartbeat.interval.ms

---

**Q17: Message loss - Causes and Prevention?**

A:

**Causes**:
- Producer acks=0 (fire-and-forget)
- Insufficient replication
- Broker crash before replication

**Prevention**:
```
Producer:
  acks=all
  enable.idempotence=true
  retries=Integer.MAX_VALUE

Broker:
  min.insync.replicas=2
  replication.factor=3
```

---

**Q18: How to handle Kafka Broker failure?**

A: Kafka handles automatically through:

1. **Replication**: Data exists on multiple brokers
2. **Leader Election**: Broker failure triggers new leader election
3. **In-Sync Replicas**: New leader chosen from ISR
4. **Metadata Update**: Clients updated with new broker topology

Recovery:
- Brokers rejoin cluster automatically
- Data resynchronized
- No data loss (with proper config)

---

## Spring Boot Integration

### Spring Boot Kafka Features

1. **Auto-Configuration**: `spring-kafka` starter auto-configures everything
2. **KafkaTemplate**: Convenient template for sending messages
3. **@KafkaListener**: Declarative consumer listener
4. **Error Handling**: Built-in error handling mechanisms
5. **Testing**: `@EmbeddedKafka` for integration testing

### Configuration in application.yml

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: StringSerializer
      value-serializer: StringSerializer
      acks: all
    consumer:
      group-id: my-group
      key-deserializer: StringDeserializer
      value-deserializer: StringDeserializer
      auto-offset-reset: earliest
    listener:
      ack-mode: manual
```

### Common Patterns

**Producer Pattern**:
```java
@Service
public class EventPublisher {
    @Autowired
    private KafkaTemplate<String, String> template;
    
    public void publish(String topic, String event) {
        template.send(topic, event);
    }
}
```

**Consumer Pattern**:
```java
@Service
public class EventListener {
    @KafkaListener(topics = "my-topic", groupId = "group1")
    public void consume(String event) {
        // Process event
    }
}
```

**Error Handling**:
```java
@Service
public class EventListener {
    @KafkaListener(topics = "my-topic", groupId = "group1")
    public void consume(String event) {
        // Process
    }
    
    @KafkaListenerExceptionHandler
    public void handleError(ListenerExecutionFailedException exc) {
        // Handle error
    }
}
```

---

## Summary & Key Takeaways

### Core Concepts to Remember
1. **Topic** = logical category, **Partition** = physical storage
2. **Producer** writes, **Consumer** reads
3. **Replication** ensures durability, **Offset** tracks position
4. **Consumer Group** enables parallel processing
5. **Rebalance** happens when group composition changes

### For Interviews
- Understand trade-offs (latency vs durability)
- Know your Kafka architecture deeply
- Be ready to design systems
- Understand performance tuning
- Know common pitfalls

### Best Practices
- Use **acks=all** for critical data
- Monitor **consumer lag**
- Plan **capacity** based on throughput
- Implement **error handling** properly
- Use **transactions** for multi-partition writes
- Test with **realistic data volumes**

### Resources for Further Learning
- Official Apache Kafka Documentation
- "Kafka: The Definitive Guide" book
- Confluent Developer Documentation
- Spring Kafka Documentation

---

## Glossary

| Term | Definition |
|------|-----------|
| **ACK** | Acknowledgment from broker |
| **BROKER** | Kafka server storing data |
| **CLUSTER** | Multiple brokers working together |
| **CONSUMER** | Application reading messages |
| **COORDINATOR** | Broker managing group balance |
| **ISR** | In-Sync Replicas |
| **LAG** | Difference between latest and consumer offset |
| **OFFSET** | Sequential message ID in partition |
| **PARTITION** | Ordered message unit |
| **PRODUCER** | Application writing messages |
| **REBALANCE** | Reassigning partitions to consumers |
| **REPLICA** | Copy of partition data |
| **TOPIC** | Category for messages |
| **ZOOKEEPER** | Cluster coordination (older versions) |
| **KRAFT** | Kafka Raft for coordination (newer versions) |

---

*Last Updated: January 2026*
*This guide covers Kafka fundamentals, architecture, and common interview questions for professionals at all levels.*
