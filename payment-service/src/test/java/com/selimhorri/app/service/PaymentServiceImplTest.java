package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.exception.wrapper.PaymentNotFoundException;
import com.selimhorri.app.repository.PaymentRepository;
import com.selimhorri.app.service.impl.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment testPayment;
    private OrderDto testOrderDto;

    @BeforeEach
    void setUp() {
        testPayment = new Payment();
        testPayment.setPaymentId(1);
        testPayment.setOrderId(1);
        testPayment.setIsPayed(false);
        testPayment.setPaymentStatus(PaymentStatus.NOT_STARTED);

        testOrderDto = OrderDto.builder()
                .orderId(1)
                .orderDesc("Test order")
                .orderFee(99.99)
                .build();
    }

    @Test
    @DisplayName("Should save payment successfully")
    void save_ShouldSaveAndReturnPayment() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        PaymentDto paymentDto = PaymentDto.builder()
                .orderDto(testOrderDto)
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build();

        PaymentDto result = paymentService.save(paymentDto);

        assertNotNull(result);
        assertFalse(result.getIsPayed());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should update payment successfully")
    void update_ShouldUpdateAndReturnPayment() {
        Payment updatedPayment = new Payment();
        updatedPayment.setPaymentId(1);
        updatedPayment.setOrderId(1);
        updatedPayment.setIsPayed(true);
        updatedPayment.setPaymentStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);

        PaymentDto paymentDto = PaymentDto.builder()
                .paymentId(1)
                .orderDto(testOrderDto)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        PaymentDto result = paymentService.update(paymentDto);

        assertNotNull(result);
        assertTrue(result.getIsPayed());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should delete payment by ID")
    void deleteById_ShouldDeletePayment() {
        doNothing().when(paymentRepository).deleteById(1);

        paymentService.deleteById(1);

        verify(paymentRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should handle payment status NOT_STARTED")
    void save_WithNotStartedStatus_ShouldSavePayment() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        PaymentDto paymentDto = PaymentDto.builder()
                .orderDto(testOrderDto)
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build();

        PaymentDto result = paymentService.save(paymentDto);

        assertNotNull(result);
        assertEquals(PaymentStatus.NOT_STARTED, result.getPaymentStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should handle payment status IN_PROGRESS")
    void update_WithInProgressStatus_ShouldUpdatePayment() {
        Payment inProgressPayment = new Payment();
        inProgressPayment.setPaymentId(1);
        inProgressPayment.setOrderId(1);
        inProgressPayment.setIsPayed(false);
        inProgressPayment.setPaymentStatus(PaymentStatus.IN_PROGRESS);

        when(paymentRepository.save(any(Payment.class))).thenReturn(inProgressPayment);

        PaymentDto paymentDto = PaymentDto.builder()
                .paymentId(1)
                .orderDto(testOrderDto)
                .isPayed(false)
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .build();

        PaymentDto result = paymentService.update(paymentDto);

        assertNotNull(result);
        assertEquals(PaymentStatus.IN_PROGRESS, result.getPaymentStatus());
        assertFalse(result.getIsPayed());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should handle payment status COMPLETED")
    void update_WithCompletedStatus_ShouldUpdatePayment() {
        Payment completedPayment = new Payment();
        completedPayment.setPaymentId(1);
        completedPayment.setOrderId(1);
        completedPayment.setIsPayed(true);
        completedPayment.setPaymentStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.save(any(Payment.class))).thenReturn(completedPayment);

        PaymentDto paymentDto = PaymentDto.builder()
                .paymentId(1)
                .orderDto(testOrderDto)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        PaymentDto result = paymentService.update(paymentDto);

        assertNotNull(result);
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        assertTrue(result.getIsPayed());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should handle multiple payments for different orders")
    void save_MultiplePayments_ShouldSaveAll() {
        Payment payment1 = new Payment();
        payment1.setPaymentId(1);
        payment1.setOrderId(1);
        payment1.setIsPayed(false);
        payment1.setPaymentStatus(PaymentStatus.NOT_STARTED);

        Payment payment2 = new Payment();
        payment2.setPaymentId(2);
        payment2.setOrderId(2);
        payment2.setIsPayed(false);
        payment2.setPaymentStatus(PaymentStatus.NOT_STARTED);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment1)
                .thenReturn(payment2);

        PaymentDto paymentDto1 = PaymentDto.builder()
                .orderDto(OrderDto.builder().orderId(1).build())
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build();

        PaymentDto paymentDto2 = PaymentDto.builder()
                .orderDto(OrderDto.builder().orderId(2).build())
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build();

        PaymentDto result1 = paymentService.save(paymentDto1);
        PaymentDto result2 = paymentService.save(paymentDto2);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should transition payment from NOT_STARTED to COMPLETED")
    void update_TransitionToCompleted_ShouldUpdatePayment() {
        Payment completedPayment = new Payment();
        completedPayment.setPaymentId(1);
        completedPayment.setOrderId(1);
        completedPayment.setIsPayed(true);
        completedPayment.setPaymentStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.save(any(Payment.class))).thenReturn(completedPayment);

        PaymentDto paymentDto = PaymentDto.builder()
                .paymentId(1)
                .orderDto(testOrderDto)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        PaymentDto result = paymentService.update(paymentDto);

        assertNotNull(result);
        assertTrue(result.getIsPayed());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}
