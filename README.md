## To run using kafka container 
➠ install docker and docker desktop

➠ Pull ZooKeeper and Kafka Images and run

   docker pull wurstmeister/zookeeper
   
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


