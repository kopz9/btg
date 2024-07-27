package org.example.btg.listener;

import org.example.btg.listener.dto.OrderCreatedEvent;
import org.example.btg.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static org.example.btg.config.RabbitMqConfig.ORDER_CREATED_QUEUE;

@Component
public class OrderCreatedListener {

  private final Logger logger = LoggerFactory.getLogger(OrderCreatedListener.class);

  private final OrderService orderService;

  public OrderCreatedListener (OrderService orderService) {
    this.orderService = orderService;
  }

  @RabbitListener(queues = ORDER_CREATED_QUEUE)
  public void listen(Message<OrderCreatedEvent> message){
    logger.info("Message consumed: {}", message);

    orderService.save(message.getPayload());

  }

}
