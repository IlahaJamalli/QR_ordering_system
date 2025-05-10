package com.qrrestaurant.backend.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderMessage(String orderMessage) {
        // 'orderQueue' is the name of the queue we'll use.
        rabbitTemplate.convertAndSend("orderQueue", orderMessage);
        System.out.println(" [x] Sent to queue: " + orderMessage);
    }
}
