package MyImdb.demo.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;


    private final RabbitTemplate rabbitTemplate;


    public RabbitMQProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message){
        log.info("Message sent to the rabbitmq: {}", message);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

}
