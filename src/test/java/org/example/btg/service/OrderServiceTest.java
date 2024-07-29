package org.example.btg.service;

import org.example.btg.entity.OrderEntity;
import org.example.btg.factory.OrderCreatedEventFactory;
import org.example.btg.factory.OrderEntityFactory;
import org.example.btg.repository.OrderRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  OrderRepository orderRepository;

  @Mock
  MongoTemplate mongoTemplate;

  @InjectMocks
  OrderService orderService;

  @Captor
  ArgumentCaptor<OrderEntity> orderEntityCaptor;

  @Nested
  class Save {

    @Test
    void shouldCallRepositorySave () {

      var event = OrderCreatedEventFactory.build();

      orderService.save(event);

      verify(orderRepository , times(1)).save(any());

    }

    @Test
    void shouldMapEventToEntityWithSuccess () {
      var event = OrderCreatedEventFactory.build();

      orderService.save(event);

      verify(orderRepository , times(1)).save(orderEntityCaptor.capture());

      var entity = orderEntityCaptor.getValue();

      assertEquals(event.codigoPedido(), entity.getOrderId());
      assertEquals(event.codigoCliente(), entity.getCustomerId());
      assertNotNull(entity.getTotal());
      assertEquals(event.itens().getFirst().produto(), entity.getItems().getFirst().getProduct());
      assertEquals(event.itens().getFirst().preco(), entity.getItems().getFirst().getPrice());
      assertEquals(event.itens().getFirst().quantidade(), entity.getItems().getFirst().getQuantity());
    }
  }

  @Test
  void shouldCalculateOrderTotalWithSuccess () {
    var event = OrderCreatedEventFactory.buildWithTwoItems();
    var totalItem1 = event.itens().getFirst().preco().multiply(BigDecimal.valueOf(event.itens().getFirst().quantidade()));
    var totalItem2 = event.itens().getLast().preco().multiply(BigDecimal.valueOf(event.itens().getLast().quantidade()));
    var orderTotal = totalItem1.add(totalItem2);

    orderService.save(event);

    verify(orderRepository , times(1)).save(orderEntityCaptor.capture());

    var entity = orderEntityCaptor.getValue();

    assertNotNull(entity.getTotal());
    assertEquals(orderTotal, entity.getTotal());
  }

  @Nested
  class findAllByCustomerId{

    @Test
    void shouldCallRepository(){
      var customerId = 1L;
      var pageRequest = PageRequest.of(0, 10);
      doReturn(OrderEntityFactory.buildWithPage()).when(orderRepository).findAllByCustomerId(eq(customerId), eq(pageRequest));


      var response = orderService.findAllByCustomerId(customerId, pageRequest);

      verify(orderRepository, times(1)).findAllByCustomerId(eq(customerId), eq(pageRequest));

    }

    @Test
    void shouldMapResponse(){
      var customerId = 1L;
      var pageRequest = PageRequest.of(0, 10);
      var page = OrderEntityFactory.buildWithPage();
      doReturn(page).when(orderRepository).findAllByCustomerId(anyLong(), any());


      var response = orderService.findAllByCustomerId(customerId, pageRequest);

      assertEquals(page.getTotalPages(), response.getTotalPages());
      assertEquals(page.getTotalElements(), response.getTotalElements());
      assertEquals(page.getSize(), response.getSize());
      assertEquals(page.getNumber(), response.getNumber());

      assertEquals(page.getContent().getFirst().getOrderId(), response.getContent().getFirst().orderId());
      assertEquals(page.getContent().getFirst().getCustomerId(), response.getContent().getFirst().customerId());
      assertEquals(page.getContent().getFirst().getTotal(), response.getContent().getFirst().total());

    }

  }

}

