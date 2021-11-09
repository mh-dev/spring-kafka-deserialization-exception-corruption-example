package mh.dev.spring.kafka.deserialization.exception.corruption.example.config;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.KafkaHeaders;

@Slf4j
public class ValueLoggingProducerInterceptor implements ProducerInterceptor {

  @Override
  public ProducerRecord onSend(ProducerRecord record) {
    log.info("Message value type: {}", record.value().getClass());
    if (record.value() instanceof byte[]) {
      log.info("byte value length {}", ((byte[]) record.value()).length);
    }
    if (record.value() instanceof String) {
      log.info("string value {}", record.value());
    }
    return record;
  }

  @Override
  public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
  }

  @Override
  public void close() {
  }

  @Override
  public void configure(Map<String, ?> configs) {
  }

}
