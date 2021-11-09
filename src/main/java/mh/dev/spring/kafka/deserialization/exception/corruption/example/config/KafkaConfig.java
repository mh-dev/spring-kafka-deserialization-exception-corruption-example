package mh.dev.spring.kafka.deserialization.exception.corruption.example.config;

import java.util.HashMap;
import java.util.Map;
import mh.dev.spring.kafka.deserialization.exception.corruption.example.listener.HelloListener;
import mh.dev.spring.kafka.deserialization.exception.corruption.example.model.HelloMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfig {

  public static final String RETRY_TOPIC_SUFFIX = "-%s-retries";

  public static final String DLT_TOPIC_SUFFIX = "-%s-dlt";

  public static final String GROUP_ID = "hello-group";

  @Bean
  public RetryTopicConfiguration retryableTopic(
      ConcurrentKafkaListenerContainerFactory<String, HelloMessage> helloListenerContainerFactory,
      KafkaTemplate<String, HelloMessage> kafkaTemplate) {

    return RetryTopicConfigurationBuilder
        .newInstance()
        .maxAttempts(10)
        .includeTopic("hello-topic")
        .retryTopicSuffix(String.format(RETRY_TOPIC_SUFFIX, GROUP_ID))
        .doNotRetryOnDltFailure()
        .dltSuffix(String.format(DLT_TOPIC_SUFFIX, GROUP_ID))
        .dltHandlerMethod(HelloListener.class, "dltHello")
        .useSingleTopicForFixedDelays()
        .doNotAutoCreateRetryTopics()
        .listenerFactory(helloListenerContainerFactory)
        .create(kafkaTemplate);
  }


  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, HelloMessage> helloListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, HelloMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(
        getHelloConsumerConfiguration()
    ));
    return factory;
  }

  public static Map<String, Object> getHelloConsumerConfiguration() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, "org.springframework.kafka.support.serializer.JsonDeserializer");
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, HelloMessage.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

    return props;
  }


  @Bean
  public ProducerFactory<String, HelloMessage> helloProducerFactory() {
    return new DefaultKafkaProducerFactory<>(getHelloProducerConfiguration());
  }


  @Bean
  public KafkaTemplate<String, HelloMessage> helloKafkaTemplate() {
    return new KafkaTemplate<>(helloProducerFactory());
  }


  public static Map<String, Object> getHelloProducerConfiguration() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.JsonSerializer");
    props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
        ValueLoggingProducerInterceptor.class.getName());
    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
    props.put("delivery.timeout.ms", 5000);
    props.put("request.timeout.ms", 5000);
    props.put("transaction.timeout.ms", 5000);
    props.put("max.block.ms", 5000);
    props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "1000000");
    return props;
  }

}
