package ru.logisticplatform.mq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.logisticplatform.dto.utils.ObjectMapperUtils;
import ru.logisticplatform.model.user.User;
import ru.logisticplatform.service.user.RoleService;
import ru.logisticplatform.service.user.UserService;

@Component
@Slf4j
public class Consumer {

    private final UserService userService;
    private final ProducerRabbitMq producerRabbitMq;

    @Autowired
    public Consumer(UserService userService
                    ,ProducerRabbitMq producerRabbitMq) {
        this.userService = userService;
        this.producerRabbitMq = producerRabbitMq;
    }
//    @RabbitListener(queues = "${rabbitmq.queue}")
//    public void consume(String msg){
//        log.info("!!!!!!!!!! consume: " + msg);
//    }

    @RabbitListener(queues = "${unknown.user.queue}")
    public void unknownUser(String userName){

        User user = userService.findByUsername(userName);

        if(user == null){
            throw new UsernameNotFoundException("User with username: " + userName + " not found");
        }

        UserMq userMq = ObjectMapperUtils.map(user, UserMq.class);
        producerRabbitMq.createUserProduce(userMq);

        log.info("IN Consumer unknownUser() - object user: {} sent to RabbitMq", user.getUsername());
    }


}
