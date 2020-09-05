package ru.logisticplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.logisticplatform.model.RestMessage;
import ru.logisticplatform.repository.RestMessageRepository;


@Service
@Slf4j
public class RestMessageServiceImpl implements RestMessageService {

    private final RestMessageRepository restMessageRepository;

    @Autowired
    public RestMessageServiceImpl(RestMessageRepository restMessageRepository){
        this.restMessageRepository = restMessageRepository;
    }


    @Override
    public RestMessage findById(Long id) {

        ru.logisticplatform.model.RestMessage restMessage = this.restMessageRepository.findById(id).orElse(null);

        if (restMessage == null){
            log.warn("IN RestMessageServiceImpl findById - no restMessage found by id: {}", id);
            return null;
        }

        return restMessage;
    }

    @Override
    public RestMessage findByCode(String code) {
        RestMessage restMessage = this.restMessageRepository.findByCode(code);

        if (restMessage == null){
            log.warn("IN RestMessageServiceImpl findByCode - no restMessage found by code: {}", code);
            return null;
        }

        return restMessage;
    }
}
