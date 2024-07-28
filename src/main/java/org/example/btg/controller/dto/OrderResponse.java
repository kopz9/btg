package org.example.btg.controller.dto;

import org.example.btg.entity.OrderEntity;

import java.math.BigDecimal;

public record OrderResponse(Long orderId, Long customerId, BigDecimal total) {

  public static OrderResponse fromEntity(OrderEntity entity){
    return new OrderResponse(entity.getOrderId(), entity.getCustomerId(), entity.getTotal());
  }


}
