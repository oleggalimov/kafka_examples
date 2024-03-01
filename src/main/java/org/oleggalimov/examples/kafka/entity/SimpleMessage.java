package org.oleggalimov.examples.kafka.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.oleggalimov.examples.kafka.dto.KafkaMessageDto;

import java.time.LocalDateTime;

@Entity
@Table(name = "simple_messages")
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SimpleMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "simpleMessageIdSeqGen")
    @SequenceGenerator(name = "simpleMessageIdSeqGen", sequenceName = "simple_messages_id_seq", allocationSize = 1)
    private Long id;

    private Long messageId;

    private String message;

    private Integer consumer;

    private LocalDateTime rowInsertTime;

    @PrePersist
    private void setRowInsertTime() {
        rowInsertTime = LocalDateTime.now();
    }

    public static SimpleMessage fromDto(KafkaMessageDto dto, int consumerNumber) {
        return new SimpleMessage()
                .setMessageId(dto.getId())
                .setMessage(dto.getMessage())
                .setConsumer(consumerNumber);
    }

}
