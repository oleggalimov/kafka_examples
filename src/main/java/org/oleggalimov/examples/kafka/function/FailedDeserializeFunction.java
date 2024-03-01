package org.oleggalimov.examples.kafka.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.serializer.FailedDeserializationInfo;

import java.util.function.Function;

/*
    Если сообщение не удалось распарсить - выполняет действия ДО ошибки.
    Важно - нНЕ РАБОТАЕТ, если в контейнере настроен хендлер
 */
@Slf4j
public class FailedDeserializeFunction<T> implements Function<FailedDeserializationInfo, T> {

    @Override
    public T apply(FailedDeserializationInfo failedDeserializationInfo) {
        log.error("Не удалось десериализовать сообщение. {}", failedDeserializationInfo);
        //нужно или кинуть exception сразу, или предусмотреть его обработку дальше
        return null;
    }
}
