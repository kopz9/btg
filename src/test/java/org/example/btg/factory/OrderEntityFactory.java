package org.example.btg.factory;

import org.example.btg.entity.OrderEntity;
import org.example.btg.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;

public class OrderEntityFactory {

  public static OrderEntity build(){

    var items = new OrderItem("notebook", 1, BigDecimal.valueOf(50.00));

    var entity = new OrderEntity();
    entity.setOrderId(1L);
    entity.setCustomerId(2L);
    entity.setTotal(BigDecimal.valueOf(50.00));
    entity.setItems(List.of(items));

    return entity;
  }

  public static Page<OrderEntity> buildWithPage(){
    return new PageImpl<>(List.of(build()));
  }

}
