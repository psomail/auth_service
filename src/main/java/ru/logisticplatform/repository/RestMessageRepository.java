package ru.logisticplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.logisticplatform.model.RestMessage;

public interface RestMessageRepository extends JpaRepository<RestMessage, Long> {

    RestMessage findByCode(String string);
}
