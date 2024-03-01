package org.oleggalimov.examples.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.oleggalimov.examples.kafka.dto.KafkaMessageDto;
import org.oleggalimov.examples.kafka.entity.SimpleMessage;
import org.oleggalimov.examples.kafka.repository.InvalidSimpleMessageRepository;
import org.oleggalimov.examples.kafka.repository.SimpleMessageRepository;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleMessageProcessor {

    private final ObjectMapper objectMapper;

    private final SimpleMessageRepository simpleMessageRepository;

    private final InvalidSimpleMessageRepository invalidSimpleMessageRepository;

    private final Validator validator;

    private final DeadLetterPublishingRecoverer deadLetterPublishingRecoverer;

    @SneakyThrows
    public void processMessage(ConsumerRecord<String, String> consumerRecord, int consumerNumber) {
        log.info("Процессим сообщение: {}", consumerRecord);
        try {
            var newMessage = objectMapper.readValue(consumerRecord.value(), KafkaMessageDto.class);
            validateMessage(newMessage);
            simpleMessageRepository.save(SimpleMessage.fromDto(newMessage, consumerNumber));
        } catch (Exception e) {
            log.error("Не удалось обработать сообщение {}", consumerRecord);
//            invalidSimpleMessageRepository.save(InvalidSimpleMessage.fromRecord(consumerRecord, e));
            handleException(consumerRecord, e);
        }
    }

    private void validateMessage(KafkaMessageDto kafkaMessageDto) {
        var violations = validator.validate(kafkaMessageDto);
        if (violations == null || violations.size() == 0) {
            log.info("Сообщение {} валидно.", kafkaMessageDto);
            return;
        }
        log.info("Получено невалидное сообщение {}", kafkaMessageDto);
        throw new RuntimeException("Сообщение не прошло валидацию!" + violations.stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.joining(", ")));
    }

    public void handleException(ConsumerRecord<String, String> consumerRecord, Exception exception) {
        deadLetterPublishingRecoverer.accept(consumerRecord, exception);
    }

}