package ru.logisticplatform.service;

public interface RestMessageService {

    ru.logisticplatform.model.RestMessage findById(Long id);

    ru.logisticplatform.model.RestMessage findByCode(String code);
}
