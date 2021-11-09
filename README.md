# Spring Kafka Deserialization Exception Corruption exmaple
This is a minimal code setup to reproduce the message corruption happening after a deserialization exception.
It forces the DeserializationException by providing a not valid enum value for `HelloMessage.type`.

## How to verify
The project contains a simple `HelloListenerTest` that writes a message that is not deserializable. The test
itself is only a vehicle to send the message and does not do any verification. 

The issue in this particular case is illustrated in `ValueLoggingProducerInterceptor` via the following code
```java
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
```



An example from a run is below. The essential part is that the byte value length continues to grow. 
```
2021-11-09 08:45:55.267  INFO 56370 --- [    Test worker] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class java.lang.String
2021-11-09 08:45:55.267  INFO 56370 --- [ad | producer-1] org.apache.kafka.clients.Metadata        : [Producer clientId=producer-1] Cluster ID: 5fiTgOc0R8iMVlNzhL1q1g
2021-11-09 08:45:55.267  INFO 56370 --- [    Test worker] .e.c.e.c.ValueLoggingProducerInterceptor : string value {"type":"value"}
2021-11-09 08:45:55.344  INFO 56370 --- [ntainer#0-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class [B
2021-11-09 08:45:55.344  INFO 56370 --- [ntainer#0-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : byte value length 22
2021-11-09 08:45:55.366  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class [B
2021-11-09 08:45:55.366  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : byte value length 34
2021-11-09 08:45:55.381  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class [B
2021-11-09 08:45:55.381  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : byte value length 50
2021-11-09 08:45:55.392  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class [B
2021-11-09 08:45:55.392  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : byte value length 70
2021-11-09 08:45:55.401  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class [B
2021-11-09 08:45:55.401  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : byte value length 98
2021-11-09 08:45:55.409  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class [B
2021-11-09 08:45:55.410  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : byte value length 134
2021-11-09 08:45:55.418  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class [B
2021-11-09 08:45:55.418  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : byte value length 182
2021-11-09 08:45:55.428  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class [B
2021-11-09 08:45:55.429  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : byte value length 246
2021-11-09 08:45:55.438  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class [B
2021-11-09 08:45:55.438  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : byte value length 330
2021-11-09 08:45:55.448  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : Message value type: class [B
2021-11-09 08:45:55.448  INFO 56370 --- [p-retries-0-C-1] .e.c.e.c.ValueLoggingProducerInterceptor : byte value length 442
```