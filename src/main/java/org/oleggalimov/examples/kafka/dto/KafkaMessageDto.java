package org.oleggalimov.examples.kafka.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KafkaMessageDto {
    private Long id;

    @NotBlank
    private String message;
}
