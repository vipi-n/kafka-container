# Quick Start Guide

Get the Kafka Spring Boot application up and running in 5 minutes!

## 🚀 5-Minute Setup

### Step 1: Start Kafka (Choose One)

**Option A: Docker (Easiest)**
```bash
docker run -d --name zookeeper -p 2181:2181 \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  confluentinc/cp-zookeeper:7.5.0

docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:7.5.0
```

**Option B: Local (if already installed)**
```bash
# Terminal 1
bin/zookeeper-server-start.sh config/zookeeper.properties

# Terminal 2
bin/kafka-server-start.sh config/server.properties
```

### Step 2: Create Topic
```bash
docker exec kafka kafka-topics --create \
  --topic test-topic \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

### Step 3: Start Application
```bash
mvn spring-boot:run
```

### Step 4: Send a Test Message
```bash
curl -X POST http://localhost:8080/api/kafka/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello Kafka"}'
```

## 📊 Check Logs

```bash
# View consumer receiving message
tail -f logs/kafka-app.log | grep "Received message"
```

You should see something like:
```
2026-01-11 10:30:45 - Received message from topic: test-topic
Message: Hello Kafka
Partition: 0
Offset: 10
```

## ✅ Success Indicators

- ✅ Application started on http://localhost:8080
- ✅ REST API responding to requests
- ✅ Consumer receiving messages in logs
- ✅ No connection errors

## 🔧 Common Commands

### Check Topic Status
```bash
docker exec kafka kafka-topics --describe \
  --topic test-topic \
  --bootstrap-server localhost:9092
```

### Monitor Consumer Lag
```bash
docker exec kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --group consumerGroup10 \
  --describe
```

### Send Messages via CLI
```bash
docker exec -it kafka kafka-console-producer \
  --broker-list localhost:9092 \
  --topic test-topic
```

### Consume Messages via CLI
```bash
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic test-topic \
  --from-beginning
```

## 📚 Next Steps

1. Read comprehensive guide: [KAFKA_KNOWLEDGE_BASE.md](KAFKA_KNOWLEDGE_BASE.md)
2. Explore REST API endpoints in [README.md](README.md)
3. Review source code for learning
4. Try different configurations in `application.yml`

## 🆘 Troubleshooting

**Connection Refused?**
- Check Kafka is running: `docker ps`
- Check port 9092 is open: `netstat -an | grep 9092`

**Topic Not Found?**
- Create the topic (see Step 2 above)

**No Logs Appearing?**
- Check logs file: `tail -f logs/kafka-app.log`
- Verify consumer group exists

## 🎯 API Testing Examples

### Test Health
```bash
curl http://localhost:8080/api/kafka/health
```

### Send to Different Topic
```bash
curl -X POST http://localhost:8080/api/kafka/send/orders \
  -H "Content-Type: application/json" \
  -d '{"message": "Order #123"}'
```

### Send with Message Key
```bash
curl -X POST http://localhost:8080/api/kafka/send-with-key \
  -H "Content-Type: application/json" \
  -d '{"key": "customer-456", "message": "Placed new order"}'
```

---

**You're all set! Happy Kafka learning! 🚀**
