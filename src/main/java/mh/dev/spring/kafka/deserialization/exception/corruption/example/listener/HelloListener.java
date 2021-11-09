package mh.dev.spring.kafka.deserialization.exception.corruption.example.listener;

import lombok.extern.slf4j.Slf4j;
import mh.dev.spring.kafka.deserialization.exception.corruption.example.config.KafkaConfig;
import mh.dev.spring.kafka.deserialization.exception.corruption.example.model.HelloMessage;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloListener {

    @KafkaListener(
        topics = "${hello.topic}",
        groupId = KafkaConfig.GROUP_ID,
        containerFactory = "helloListenerContainerFactory"
    )
    public void hello(HelloMessage message) {
        log.info("Message , {}", message.toString());
        throw new RuntimeException();
    }

    @DltHandler
    public void dltHello(HelloMessage message) {
        log.error("DLT message {}", message.toString());
    }
}
