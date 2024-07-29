package org.example.btg.factory;

import org.example.btg.listener.dto.OrderCreatedEvent;
import org.example.btg.listener.dto.OrderItemEvent;

import java.math.BigDecimal;
import java.util.List;

public class OrderCreatedEventFactory {

  public static OrderCreatedEvent build(){

    var itens = new OrderItemEvent("notebook", 1, BigDecimal.valueOf(50.00));
    var event = new OrderCreatedEvent(1L, 2L, List.of(itens));

    return event;
  }
}
