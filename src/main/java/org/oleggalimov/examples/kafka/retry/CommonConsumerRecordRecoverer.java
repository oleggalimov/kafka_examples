package org.oleggalimov.examples.kafka.retry;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommonConsumerRecordRecoverer implements ConsumerRecordRecoverer {

    @Override
    public void accept(ConsumerRecord<?, ?> consumerRecord, Exception exception) {
        log.error("Не удалось обработать запись после нескольких попыток", exception);
    }
}