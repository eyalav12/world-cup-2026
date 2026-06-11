package com.world_cup.demo.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.gamesqueue.name}")
    private String queueName;

    @Value("${rabbitmq.matchupdatequeue.name}")
    private String matchUpdateQueueName;

    @Value("${rabbitmq.notificationsqueue.name}")
    private String notificationsUpdateQueueName;

    @Value("${rabbitmq.gamesexchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.gamesroutekey.name}")
    private String routeKey;

    @Bean
    public Queue matchGameEndQueue(){
        return new Queue(queueName);
    }

    @Bean
    public Queue chunkWorkQueue(){
        return new Queue("chunk_work_queue");
    }

    @Bean
    public Queue matchUpdateQueue(){
        return new Queue(matchUpdateQueueName);
    }

    @Bean
    public Queue notificationsUpdateQueue(){
        return new Queue(notificationsUpdateQueueName);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(matchGameEndQueue())
                .to(exchange())
                .with(routeKey);
    }

    @Bean
    public Binding notificationsUpdateBinding(){
        return BindingBuilder.bind(notificationsUpdateQueue())
                .to(exchange())
                .with(routeKey);
    }

    @Bean
    public Binding matchUpdateBinding(){
        return BindingBuilder.bind(matchUpdateQueue())
                .to(exchange())
                .with(routeKey);
    }

    @Bean
    public Binding chunkWorkBinding(){
        return BindingBuilder.bind(chunkWorkQueue())
                .to(exchange())
                .with("chunk_work_key");
    }

    @Bean
    public MessageConverter converter(){
        return new JacksonJsonMessageConverter();
    }
}
