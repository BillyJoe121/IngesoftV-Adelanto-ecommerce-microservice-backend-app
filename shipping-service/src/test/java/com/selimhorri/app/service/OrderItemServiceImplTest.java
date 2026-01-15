package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.domain.OrderItem;
import com.selimhorri.app.domain.id.OrderItemId;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.OrderItemDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.OrderItemNotFoundException;
import com.selimhorri.app.repository.OrderItemRepository;
import com.selimhorri.app.service.impl.OrderItemServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemService Unit Tests")
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private OrderItem testOrderItem;
    private OrderItemId testOrderItemId;
    private ProductDto testProductDto;
    private OrderDto testOrderDto;

    @BeforeEach
    void setUp() {
        testOrderItemId = new OrderItemId(1, 1);

        testOrderItem = new OrderItem();
        testOrderItem.setProductId(1);
        testOrderItem.setOrderId(1);
        testOrderItem.setOrderedQuantity(5);

        testProductDto = ProductDto.builder()
                .productId(1)
                .productTitle("Test Product")
                .priceUnit(29.99)
                .quantity(100)
                .build();

        testOrderDto = OrderDto.builder()
                .orderId(1)
                .orderDesc("Test Order")
                .orderFee(149.95)
                .build();
    }

    @Test
    @DisplayName("Should save order item successfully")
    void save_ShouldSaveAndReturnOrderItem() {
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItemDto orderItemDto = OrderItemDto.builder()
                .productDto(testProductDto)
                .orderDto(testOrderDto)
                .orderedQuantity(5)
                .build();

        OrderItemDto result = orderItemService.save(orderItemDto);

        assertNotNull(result);
        assertEquals(5, result.getOrderedQuantity());
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should update order item successfully")
    void update_ShouldUpdateAndReturnOrderItem() {
        OrderItem updatedOrderItem = new OrderItem();
        updatedOrderItem.setProductId(1);
        updatedOrderItem.setOrderId(1);
        updatedOrderItem.setOrderedQuantity(10);

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(updatedOrderItem);

        OrderItemDto orderItemDto = OrderItemDto.builder()
                .productDto(testProductDto)
                .orderDto(testOrderDto)
                .orderedQuantity(10)
                .build();

        OrderItemDto result = orderItemService.update(orderItemDto);

        assertNotNull(result);
        assertEquals(10, result.getOrderedQuantity());
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should delete order item by ID")
    void deleteById_ShouldDeleteOrderItem() {
        doNothing().when(orderItemRepository).deleteById(any(OrderItemId.class));

        orderItemService.deleteById(testOrderItemId);

        verify(orderItemRepository, times(1)).deleteById(any(OrderItemId.class));
    }

    @Test
    @DisplayName("Should handle order item with quantity 1")
    void save_WithQuantityOne_ShouldSaveOrderItem() {
        OrderItem singleItemOrder = new OrderItem();
        singleItemOrder.setProductId(2);
        singleItemOrder.setOrderId(1);
        singleItemOrder.setOrderedQuantity(1);

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(singleItemOrder);

        OrderItemDto orderItemDto = OrderItemDto.builder()
                .productDto(ProductDto.builder().productId(2).build())
                .orderDto(testOrderDto)
                .orderedQuantity(1)
                .build();

        OrderItemDto result = orderItemService.save(orderItemDto);

        assertNotNull(result);
        assertEquals(1, result.getOrderedQuantity());
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should handle order item with high quantity")
    void save_WithHighQuantity_ShouldSaveOrderItem() {
        OrderItem bulkOrder = new OrderItem();
        bulkOrder.setProductId(1);
        bulkOrder.setOrderId(1);
        bulkOrder.setOrderedQuantity(1000);

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(bulkOrder);

        OrderItemDto orderItemDto = OrderItemDto.builder()
                .productDto(testProductDto)
                .orderDto(testOrderDto)
                .orderedQuantity(1000)
                .build();

        OrderItemDto result = orderItemService.save(orderItemDto);

        assertNotNull(result);
        assertEquals(1000, result.getOrderedQuantity());
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should handle multiple order items for same order")
    void save_MultipleItemsSameOrder_ShouldSaveAll() {
        OrderItem item1 = new OrderItem();
        item1.setProductId(1);
        item1.setOrderId(1);
        item1.setOrderedQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setProductId(2);
        item2.setOrderId(1);
        item2.setOrderedQuantity(3);

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(item1)
                .thenReturn(item2);

        OrderItemDto orderItemDto1 = OrderItemDto.builder()
                .productDto(ProductDto.builder().productId(1).build())
                .orderDto(testOrderDto)
                .orderedQuantity(2)
                .build();

        OrderItemDto orderItemDto2 = OrderItemDto.builder()
                .productDto(ProductDto.builder().productId(2).build())
                .orderDto(testOrderDto)
                .orderedQuantity(3)
                .build();

        OrderItemDto result1 = orderItemService.save(orderItemDto1);
        OrderItemDto result2 = orderItemService.save(orderItemDto2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(2, result1.getOrderedQuantity());
        assertEquals(3, result2.getOrderedQuantity());
        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should handle order item update with different quantity")
    void update_WithDifferentQuantity_ShouldUpdateOrderItem() {
        OrderItem originalItem = new OrderItem();
        originalItem.setProductId(1);
        originalItem.setOrderId(1);
        originalItem.setOrderedQuantity(5);

        OrderItem updatedItem = new OrderItem();
        updatedItem.setProductId(1);
        updatedItem.setOrderId(1);
        updatedItem.setOrderedQuantity(8);

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(updatedItem);

        OrderItemDto orderItemDto = OrderItemDto.builder()
                .productDto(testProductDto)
                .orderDto(testOrderDto)
                .orderedQuantity(8)
                .build();

        OrderItemDto result = orderItemService.update(orderItemDto);

        assertNotNull(result);
        assertEquals(8, result.getOrderedQuantity());
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should delete order item for specific product and order combination")
    void deleteById_SpecificCombination_ShouldDelete() {
        OrderItemId specificId = new OrderItemId(5, 10);
        doNothing().when(orderItemRepository).deleteById(any(OrderItemId.class));

        orderItemService.deleteById(specificId);

        verify(orderItemRepository, times(1)).deleteById(any(OrderItemId.class));
    }
}
