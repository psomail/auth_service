package ru.logisticplatform.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ProducerRabbitMq {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ProducerRabbitMq(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${rabbitmq.exchange}")
    private String directExchange;

    @Value("${create.user.key}")
    private String createUserKey;

    @Value("${update.user.key}")
    private String updateUserKey;

    @Value("${delete.user.key}")
    private String deleteUserKey;

    @Value("${create.user.queue}")
    private String createUserQueue;

    @Value("${update.user.queue}")
    private String updateUserQueue;

    @Value("${delete.user.queue}")
    private String deleteUserQueue;


    @Bean
    public Queue createUserQueue(){
        return new Queue(createUserQueue, false);
    }

    @Bean
    public Queue updateUserQueue(){
        return new Queue(updateUserQueue, false);
    }

    @Bean
    public Queue deleteUserQueue(){
        return new Queue(deleteUserQueue, false);
    }


    @Bean
    public TopicExchange directExchange(){
        return new TopicExchange(directExchange);
    }

    @Bean
    public Binding createUserBinding(){
        return BindingBuilder.bind(createUserQueue()).to(directExchange()).with(createUserKey);
    }

    @Bean
    public Binding updateUserBinding(){
        return BindingBuilder.bind(updateUserQueue()).to(directExchange()).with(updateUserKey);
    }

    @Bean
    public Binding deleteUserBinding(){
        return BindingBuilder.bind(deleteUserQueue()).to(directExchange()).with(deleteUserKey);
    }

    public void createUserProduce(UserMq userMq){
        log.info("IN ProducerRabbitMq createUserProduce - user: {}", userMq.getUsername());

        rabbitTemplate.convertAndSend( directExchange, createUserKey, userMq);
    }

    public void updateUserProduce(UserMq userMq){
        log.info("IN ProducerRabbitMq updateUserProduce - user: {}", userMq.getUsername());

        rabbitTemplate.convertAndSend(directExchange, updateUserKey, userMq);
    }

    public void deleteUserProduce(UserMq userMq){
        log.info("IN ProducerRabbitMq deleteUserProduce - user: {}", userMq.getUsername());

        rabbitTemplate.convertAndSend(directExchange, deleteUserKey, userMq);
    }
}
