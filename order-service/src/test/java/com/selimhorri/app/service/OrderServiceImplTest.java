package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.domain.Order;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.exception.wrapper.OrderNotFoundException;
import com.selimhorri.app.repository.OrderRepository;
import com.selimhorri.app.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setCartId(1);
        testCart.setUserId(1);

        testOrder = new Order();
        testOrder.setOrderId(1);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setOrderDesc("Test order description");
        testOrder.setOrderFee(99.99);
        testOrder.setCart(testCart);
    }

    @Test
    @DisplayName("Should return all orders")
    void findAll_ShouldReturnAllOrders() {
        Order order2 = new Order();
        order2.setOrderId(2);
        order2.setOrderDate(LocalDateTime.now());
        order2.setOrderDesc("Second order");
        order2.setOrderFee(149.99);
        order2.setCart(testCart);

        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder, order2));

        List<OrderDto> result = orderService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return order by ID")
    void findById_WhenOrderExists_ShouldReturnOrder() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));

        OrderDto result = orderService.findById(1);

        assertNotNull(result);
        assertEquals("Test order description", result.getOrderDesc());
        assertEquals(99.99, result.getOrderFee());
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when order not found by ID")
    void findById_WhenOrderNotExists_ShouldThrowException() {
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> {
            orderService.findById(999);
        });

        verify(orderRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Should save order successfully")
    void save_ShouldSaveAndReturnOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        CartDto cartDto = CartDto.builder()
                .cartId(1)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .orderDesc("Test order description")
                .orderFee(99.99)
                .cartDto(cartDto)
                .build();

        OrderDto result = orderService.save(orderDto);

        assertNotNull(result);
        assertEquals("Test order description", result.getOrderDesc());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order successfully")
    void update_ShouldUpdateAndReturnOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        CartDto cartDto = CartDto.builder()
                .cartId(1)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .orderId(1)
                .orderDesc("Updated order description")
                .orderFee(129.99)
                .cartDto(cartDto)
                .build();

        OrderDto result = orderService.update(orderDto);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order by ID successfully")
    void updateById_ShouldUpdateAndReturnOrder() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        CartDto cartDto = CartDto.builder()
                .cartId(1)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .orderDesc("Updated order description")
                .orderFee(159.99)
                .cartDto(cartDto)
                .build();

        OrderDto result = orderService.update(1, orderDto);

        assertNotNull(result);
        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should delete order by ID")
    void deleteById_ShouldDeleteOrder() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).delete(any(Order.class));

        orderService.deleteById(1);

        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).delete(any(Order.class));
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    void findAll_WhenNoOrders_ShouldReturnEmptyList() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList());

        List<OrderDto> result = orderService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle order with zero fee")
    void save_WithZeroFee_ShouldSaveOrder() {
        Order freeOrder = new Order();
        freeOrder.setOrderId(3);
        freeOrder.setOrderDate(LocalDateTime.now());
        freeOrder.setOrderDesc("Free shipping order");
        freeOrder.setOrderFee(0.0);
        freeOrder.setCart(testCart);

        when(orderRepository.save(any(Order.class))).thenReturn(freeOrder);

        CartDto cartDto = CartDto.builder()
                .cartId(1)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .orderDesc("Free shipping order")
                .orderFee(0.0)
                .cartDto(cartDto)
                .build();

        OrderDto result = orderService.save(orderDto);

        assertNotNull(result);
        assertEquals(0.0, result.getOrderFee());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should handle order with null description")
    void save_WithNullDescription_ShouldSaveOrder() {
        Order orderNullDesc = new Order();
        orderNullDesc.setOrderId(4);
        orderNullDesc.setOrderDate(LocalDateTime.now());
        orderNullDesc.setOrderDesc(null);
        orderNullDesc.setOrderFee(50.00);
        orderNullDesc.setCart(testCart);

        when(orderRepository.save(any(Order.class))).thenReturn(orderNullDesc);

        CartDto cartDto = CartDto.builder()
                .cartId(1)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .orderFee(50.00)
                .cartDto(cartDto)
                .build();

        OrderDto result = orderService.save(orderDto);

        assertNotNull(result);
        assertNull(result.getOrderDesc());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
