package mh.dev.spring.kafka.deserialization.exception.corruption.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class SpringKafkaDeserializationExceptionCorruptionExample {

    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaDeserializationExceptionCorruptionExample.class, args);
    }

}
