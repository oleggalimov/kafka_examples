package org.oleggalimov.examples.kafka.repository;

import org.oleggalimov.examples.kafka.entity.InvalidSimpleMessage;
import org.oleggalimov.examples.kafka.entity.SimpleMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface InvalidSimpleMessageRepository extends JpaRepository<InvalidSimpleMessage, UUID> {
}
