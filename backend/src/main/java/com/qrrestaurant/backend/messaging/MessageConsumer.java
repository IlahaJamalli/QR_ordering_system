package com.qrrestaurant.backend.messaging;

import com.qrrestaurant.backend.model.OrderMessage;
import com.qrrestaurant.backend.repository.OrderMessageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @Autowired
    private OrderMessageRepository orderMessageRepository;

    @RabbitListener(queues = "orderQueue")
    public void receiveOrderMessage(String message) {
        System.out.println(" [✔] Received from queue: " + message);

        // ✅ Save to the database
        OrderMessage orderMsg = new OrderMessage();
        orderMsg.setContent(message);
        orderMsg.setProcessed(false);  // default

        orderMessageRepository.save(orderMsg);
    }
}
