package org.oleggalimov.examples.kafka.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KafkaMessageDto {
    private Long id;
    private String message;
}
