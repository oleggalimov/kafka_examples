package org.oleggalimov.examples.kafka.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.oleggalimov.examples.kafka.producer.LoggingDeadLetterPublishingRecoverer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

import java.util.HashMap;
import java.util.Map;

import static org.oleggalimov.examples.kafka.constant.KafkaConstant.DEFAULT_KAFKA_CONTAINER_FACTORY;

@Configuration
public class KafkaApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);

        return mapper;
    }

//    @Bean
//    public DefaultErrorHandler defaultErrorHandler(ConsumerRecordRecoverer recoverer, FixedBackOff backoff) {
//        var handler = new DefaultErrorHandler(recoverer, backoff);
////        handler.addRetryableExceptions(SocketTimeoutException.class);
////        handler.addNotRetryableExceptions(JsonEOFException.class);
//        handler.setLogLevel(KafkaException.Level.TRACE); //не работает!
//        return handler;
//    }

    @Bean(name = DEFAULT_KAFKA_CONTAINER_FACTORY)
    public ConcurrentKafkaListenerContainerFactory getDefaultFactory(KafkaProperties kafkaProperties) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(getDefaultConsumerFactory(kafkaProperties));
        factory.setBatchListener(true);
        factory.setAutoStartup(true);
        factory.setConcurrency(1);
        //Чтобы добавить объект Ack в листенеры нужна эта пропертя
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    private ConsumerFactory<String, String> getDefaultConsumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(buildParams(kafkaProperties));
    }

    private Map<String, Object> buildParams(KafkaProperties kafkaProperties) {
        var result = new HashMap<String, Object>();
        result.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        result.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        result.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        result.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        result.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        result.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
//        result.put(ErrorHandlingDeserializer.VALUE_FUNCTION, ru.tinkoff.prm.prma.consumer.FailedDeserializeFunction.class);

        return result;
    }


    @Bean
    public DeadLetterPublishingRecoverer getDeadLetterPublishingRecoverer(KafkaProperties kafkaProperties) {
        return new LoggingDeadLetterPublishingRecoverer(buildDefaultProducer(kafkaProperties), "test.topic.dlt");
    }

    public KafkaTemplate<String, String> buildDefaultProducer(KafkaProperties kafkaProperties) {
        var producerFactory = new DefaultKafkaProducerFactory<String, String>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.RETRIES_CONFIG, "3",
                ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, "5000"
        ));
        return new KafkaTemplate<>(producerFactory);
    }
}
