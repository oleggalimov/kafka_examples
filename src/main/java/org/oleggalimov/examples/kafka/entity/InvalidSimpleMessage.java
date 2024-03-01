package org.oleggalimov.examples.kafka.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invalid_simple_messages")
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class InvalidSimpleMessage {

    @Id
    private UUID id;

    private String topic;

    private LocalDateTime rowInsertTime;

    private String payload;

    private String cause;

    @PrePersist
    private void setRequiredFields() {
        id = UUID.randomUUID();
        rowInsertTime = LocalDateTime.now();
    }

    public static InvalidSimpleMessage fromRecord(
            ConsumerRecord<String, String> consumerRecord,
            Exception exception) {
        return new InvalidSimpleMessage()
                .setTopic(consumerRecord.topic())
                .setPayload(consumerRecord.value())
                .setCause(exception.toString());
    }

}
