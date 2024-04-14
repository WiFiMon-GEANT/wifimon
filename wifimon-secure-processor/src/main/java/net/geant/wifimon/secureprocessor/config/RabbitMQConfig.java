package net.geant.wifimon.secureprocessor.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.probesQueue.name}")
    private String probesQueue;

    @Value("${rabbitmq.probesRoutingKey.name}")
    private String probesRoutingKey;

    @Value("${rabbitmq.twampQueue.name}")
    private String twampQueue;

    @Value("${rabbitmq.twampRoutingKey.name}")
    private String twampRoutingKey;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${cloudAMQP_url}")
    private String cloudAMQP_url;


    @Bean
    public Queue probesQueue() {
        return new Queue(probesQueue);
    }

    @Bean
    public Queue twampQueue() {
        return new Queue(twampQueue);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding probesBinding() {
        return BindingBuilder.bind(probesQueue())
                .to(exchange())
                .with(probesRoutingKey);
    }

    @Bean
    public Binding twampBinding() {
        return BindingBuilder.bind(twampQueue())
                .to(exchange())
                .with(twampRoutingKey);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUri(cloudAMQP_url);
        return connectionFactory;
    }

}
