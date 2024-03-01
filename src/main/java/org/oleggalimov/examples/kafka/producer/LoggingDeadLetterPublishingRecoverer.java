package org.oleggalimov.examples.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

@Slf4j
public class LoggingDeadLetterPublishingRecoverer extends DeadLetterPublishingRecoverer {
    private static final String PUBLICATION_FAILED_MESSAGE = "Ошибка отправки сообщения в DLQ: {}";

    public LoggingDeadLetterPublishingRecoverer(KafkaTemplate<?, ?> template, String topic) {
        super(template, (consumerRecord, e) -> new TopicPartition(topic, consumerRecord.partition()));
    }

    @Override
    protected void publish(ProducerRecord<Object, Object> outRecord,
                           KafkaOperations<Object, Object> kafkaTemplate,
                           ConsumerRecord<?, ?> inRecord) {
        try {
            kafkaTemplate.send(outRecord);
            log.info("Отправлено в DLT: {}", outRecord);
        } catch (Exception e) {
            log.error(PUBLICATION_FAILED_MESSAGE, outRecord, e);
        }
    }
}
