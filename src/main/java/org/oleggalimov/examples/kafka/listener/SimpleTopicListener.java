package org.oleggalimov.examples.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.oleggalimov.examples.kafka.service.SimpleMessageProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.oleggalimov.examples.kafka.constant.KafkaConstant.DEFAULT_KAFKA_CONTAINER_FACTORY;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleTopicListener {
    private static final int CONSUMER_NUMBER = 1;
    private static final String TEST_TOPIC_NAME = "test.topic";
    private static final String TEST_GROUP_ID = "test.topic.group.1";

    private final SimpleMessageProcessor testService;

    @KafkaListener(
            id = "test_1",
            topics = TEST_TOPIC_NAME,
            containerFactory = DEFAULT_KAFKA_CONTAINER_FACTORY,
            groupId = TEST_GROUP_ID
    )
    public void readMessage(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) {
        records.forEach(consumerRecord -> testService.processMessage(consumerRecord, CONSUMER_NUMBER));
        acknowledgment.acknowledge();
    }
}
