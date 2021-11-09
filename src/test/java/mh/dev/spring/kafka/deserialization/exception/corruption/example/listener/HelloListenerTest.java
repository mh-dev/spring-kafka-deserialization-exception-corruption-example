package mh.dev.spring.kafka.deserialization.exception.corruption.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

@Slf4j
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class HelloListenerTest {

  @Value("${hello.topic}")
  private String helloTopic;

  @Autowired
  private KafkaTemplate kafkaTemplate;

  @Test
  public void test() throws InterruptedException {
    log.info("Send message");
    kafkaTemplate.send(helloTopic, "{\"type\":\"value\"}");
    // Pause for a moment to allow a bit of time for retry processing
    Thread.sleep(2000);
  }
}