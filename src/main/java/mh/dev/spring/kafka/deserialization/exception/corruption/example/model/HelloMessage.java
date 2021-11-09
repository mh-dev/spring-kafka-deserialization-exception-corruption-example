package mh.dev.spring.kafka.deserialization.exception.corruption.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloMessage {

    private String text;
    private HelloType type;

}
