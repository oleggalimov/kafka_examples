package org.oleggalimov.examples.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.oleggalimov.examples.kafka.service.SimpleMessageProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.oleggalimov.examples.kafka.constant.KafkaConstant.DEFAULT_KAFKA_CONTAINER_FACTORY;

@Slf4j
@Component
@RequiredArgsConstructor
public class DltListener {
    private static final int CONSUMER_NUMBER = 2;
    private static final String TEST_DLT_NAME = "test.topic.dlt";
    private static final String TEST_GROUP_ID = "test.topic.dlt.group.1";
    private static final Duration PROCESSING_DELAY = Duration.ofMinutes(2);

    private final SimpleMessageProcessor testService;

    @KafkaListener(
            id = "invalid_test_1",
            topics = TEST_DLT_NAME,
            containerFactory = DEFAULT_KAFKA_CONTAINER_FACTORY,
            groupId = TEST_GROUP_ID

    )
    public void readMessage(List<ConsumerRecord<String, String>> invalidRecords, Acknowledgment acknowledgment) {
        log.info("Получено сообщений: {}", invalidRecords.size());
        Instant lastAttemptTime = invalidRecords.stream()
                .min(Comparator.comparingLong(ConsumerRecord::timestamp))
                .map((consumerRecord) -> Instant.ofEpochMilli(consumerRecord.timestamp()))
                .orElseThrow(() -> new RuntimeException("В очереди нет сообщений"));
        Instant currentTime = Instant.now();
        if (currentTime.minus(PROCESSING_DELAY).isBefore(lastAttemptTime)) {
            ZoneId zoneId = ZoneId.of("Europe/Moscow");
            log.info("Обработка не производится: сообщения обрабатывались в {}; следующая обработка не ранее {}",
                    lastAttemptTime.atZone(zoneId), currentTime.plus(PROCESSING_DELAY).atZone(zoneId));
            acknowledgment.nack(0, PROCESSING_DELAY);
        } else {
            invalidRecords.stream()
                    .map(consumerRecord -> {
                        try {
                            testService.processMessage(consumerRecord, CONSUMER_NUMBER);
                            return null;
                        } catch (Exception exception) {
                            log.info("Повторная обработка не удалась");
                            return consumerRecord;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(consumerRecord -> {
                        if(getProcessingAttempts(consumerRecord) < 3) {
                            log.error("Превышен лимит попыток");
                            return false;
                        } else {
                            return true;
                        }
                    })
                    .toList();
            acknowledgment.acknowledge();
        }
    }

    public static <T> int getProcessingAttempts(ConsumerRecord<String, T> consumerRecord) {
        Header processingAttemptsHeader = consumerRecord.headers().lastHeader("attempts_to_process");
        return processingAttemptsHeader == null ? 0 : Integer.parseInt(bytesToString(processingAttemptsHeader.value()));
    }

    public static String bytesToString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
