package org.example.btg.controller.dto;

import org.example.btg.factory.OrderEntityFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseTest {
  @Nested
  class FromEntity{

    @Test
    void shouldMapCorrectly(){

     var input = OrderEntityFactory.build();

     var output = OrderResponse.fromEntity(input);

     assertEquals(input.getOrderId(), output.orderId());
     assertEquals(input.getCustomerId(), output.customerId());
     assertEquals(input.getTotal(), output.total());

    }
  }
}