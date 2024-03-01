package org.oleggalimov.examples.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.oleggalimov.examples.kafka.dto.KafkaMessageDto;
import org.oleggalimov.examples.kafka.entity.SimpleMessage;
import org.oleggalimov.examples.kafka.repository.SimpleMessageRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleMessageProcessor {

    private final ObjectMapper objectMapper;

    private final SimpleMessageRepository simpleMessageRepository;

    @SneakyThrows
    public void processMessage(ConsumerRecord<String, String> consumerRecord, int consumerNumber) {
        log.info("Процессим сообщение: {}", consumerRecord);
        try {
            var newMessage = objectMapper.readValue(consumerRecord.value(), KafkaMessageDto.class);
            simpleMessageRepository.save(SimpleMessage.fromDto(newMessage, consumerNumber));
        } catch (Exception e) {
            log.error("Не удалось обработать сообщение {}", consumerRecord, e);
            throw e;
        }
    }

}