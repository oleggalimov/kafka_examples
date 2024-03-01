package org.oleggalimov.examples.kafka.repository;

import org.oleggalimov.examples.kafka.entity.SimpleMessage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SimpleMessageRepository extends JpaRepository<SimpleMessage, Long> {
}
