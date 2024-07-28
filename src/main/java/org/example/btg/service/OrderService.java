package org.example.btg.service;

import org.bson.Document;
import org.example.btg.controller.dto.OrderResponse;
import org.example.btg.entity.OrderEntity;
import org.example.btg.entity.OrderItem;
import org.example.btg.listener.dto.OrderCreatedEvent;
import org.example.btg.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {
  private final OrderRepository orderRepository;
  private final MongoTemplate mongoTemplate;

  public OrderService (OrderRepository orderRepository , MongoTemplate mongoTemplate) {
    this.orderRepository = orderRepository;
    this.mongoTemplate = mongoTemplate;
  }

  private static List<OrderItem> getOrderItems (OrderCreatedEvent event) {
    return event.itens().stream()
      .map(i -> new OrderItem(i.produto() , i.quantidade() , i.preco()))
      .toList();

  }

  private BigDecimal getTotal (OrderCreatedEvent event) {
    return event
      .itens()
      .stream()
      .map(i -> i.preco().multiply(BigDecimal.valueOf(i.quantidade())))
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);
  }

  public BigDecimal findTotalOnOrdersByCustomerId (Long customerId) {
    var aggregations = newAggregation(match(Criteria.where("customerId").is(customerId)),
      group().sum("total").as("total")
    );

    var response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);

    return new BigDecimal(response.getUniqueMappedResult().get("total").toString());
  }

  public Page<OrderResponse> findAllByCustomerId (Long customerId , PageRequest pageRequest) {
    var orders = orderRepository.findAllByCustomerId(customerId , pageRequest);
    return orders.map(OrderResponse::fromEntity);
  }

  public void save (OrderCreatedEvent event) {
    var entity = new OrderEntity();
    entity.setOrderId(event.codigoPedido());
    entity.setCustomerId(event.codigoCliente());
    entity.setItems(getOrderItems(event));
    entity.setTotal(getTotal(event));

    orderRepository.save(entity);
  }
}
