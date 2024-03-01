package org.oleggalimov.examples.kafka.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

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

    @Bean
    public DefaultErrorHandler defaultErrorHandler(ConsumerRecordRecoverer recoverer, FixedBackOff backoff) {
        var handler = new DefaultErrorHandler(recoverer, backoff);
//        handler.addRetryableExceptions(SocketTimeoutException.class);
//        handler.addNotRetryableExceptions(JsonEOFException.class);
        handler.setLogLevel(KafkaException.Level.TRACE); //не работает!
        return handler;
    }

    @Bean(name = DEFAULT_KAFKA_CONTAINER_FACTORY)
    public ConcurrentKafkaListenerContainerFactory getDefaultFactory(KafkaProperties kafkaProperties,
                                                                     DefaultErrorHandler defaultErrorHandler) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(getDefaultConsumerFactory(kafkaProperties));
        factory.setBatchListener(true);
        factory.setAutoStartup(true);
        factory.setConcurrency(1);
        factory.setCommonErrorHandler(defaultErrorHandler);
        //Чтобы добавить объект Ack в листенеры нужна эта пропертя
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
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
}
