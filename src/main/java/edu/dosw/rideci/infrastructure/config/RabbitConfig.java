package edu.dosw.rideci.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue geolocationTravelQueue() {
        return new Queue("geolocation.travel.created.queue", true);
    }

    @Bean
    public Queue geolocationTravelUpdatedQueue() {
        return new Queue("geolocation.travel.updated.queue", true);
    }

    @Bean
    public Queue geolocationTravelCancelledQueue() {
        return new Queue("geolocation.travel.cancelled.queue", true);
    }

    @Bean
    public TopicExchange travelExchange() {
        return new TopicExchange("travel.exchange", true, false);
    }

    @Bean
    public Binding bindingGeolocationTravelCancelled() {
        return BindingBuilder
                .bind(geolocationTravelCancelledQueue())
                .to(travelExchange())
                .with("travel.cancelled");
    }

    @Bean
    public Binding bindingGeolocationTravelUpdated() {
        return BindingBuilder
                .bind(geolocationTravelUpdatedQueue())
                .to(travelExchange())
                .with("travel.updated");
    }

    @Bean
    public Binding bindingGeolocationTravelCreated() {
        return BindingBuilder
                .bind(geolocationTravelQueue())
                .to(travelExchange())
                .with("travel.created");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
