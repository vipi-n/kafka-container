package org.kafka.demo;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.kafka.demo.constant.KafkaConstants;
import org.kafka.demo.consumer.KafkaConsumer;
import org.kafka.demo.producer.KafkaProducer;

import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) {

        //runConsumer();
        runProducer();

    }
    public static void runConsumer(){

        Consumer<Long, String> consumer = KafkaConsumer.consumer();
        int msgToFetch = 0;

        while (true){
             ConsumerRecords<Long, String> consumerRecord = consumer.poll(1000);
             if (consumerRecord.count() == 0){
                 msgToFetch++;
                 if (msgToFetch > KafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT){
                     break;
                 }else {
                     continue;
                 }
             }
             consumerRecord.forEach(record -> {
                 System.out.println("Record Key " + record.key());
                 System.out.println("Record value " + record.value());
                 System.out.println("Record partition " + record.partition());
                 System.out.println("Record offset " + record.offset());
             });
             consumer.commitAsync();
        }
        consumer.close();

    }

    public static void runProducer(){

        Producer<Long, String> producer = KafkaProducer.producer();

        for (int i = 0; i < KafkaConstants.MESSAGE_COUNT ; i++) {

            final ProducerRecord<Long, String> record =
                    new ProducerRecord<>(KafkaConstants.TOPIC_NAME, "This is record " +i);

            try {
                RecordMetadata metadata = producer.send(record).get();

                System.out.println("Record sent with key " + i + " to partition " + metadata.partition()
                        + " with offset " + metadata.offset());

            } catch (ExecutionException | InterruptedException e) {
                System.out.println("Error in sending record");
                System.out.println(e);
            }
        }
    }




}