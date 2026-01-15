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

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.CartNotFoundException;
import com.selimhorri.app.repository.CartRepository;
import com.selimhorri.app.service.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart testCart;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setCartId(1);
        testCart.setUserId(1);

        testUserDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .build();
    }

    @Test
    @DisplayName("Should save cart successfully")
    void save_ShouldSaveAndReturnCart() {
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartDto cartDto = CartDto.builder()
                .userDto(testUserDto)
                .build();

        CartDto result = cartService.save(cartDto);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should update cart successfully")
    void update_ShouldUpdateAndReturnCart() {
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartDto cartDto = CartDto.builder()
                .cartId(1)
                .userDto(testUserDto)
                .build();

        CartDto result = cartService.update(cartDto);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should delete cart by ID")
    void deleteById_ShouldDeleteCart() {
        doNothing().when(cartRepository).deleteById(1);

        cartService.deleteById(1);

        verify(cartRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should handle multiple carts for same user")
    void save_MultipleCarts_ShouldSaveAll() {
        Cart cart2 = new Cart();
        cart2.setCartId(2);
        cart2.setUserId(1);

        when(cartRepository.save(any(Cart.class)))
                .thenReturn(testCart)
                .thenReturn(cart2);

        CartDto cartDto1 = CartDto.builder()
                .userDto(testUserDto)
                .build();

        CartDto cartDto2 = CartDto.builder()
                .userDto(testUserDto)
                .build();

        CartDto result1 = cartService.save(cartDto1);
        CartDto result2 = cartService.save(cartDto2);

        assertNotNull(result1);
        assertNotNull(result2);
        verify(cartRepository, times(2)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should handle cart deletion for non-existent ID")
    void deleteById_NonExistentCart_ShouldNotThrow() {
        doNothing().when(cartRepository).deleteById(999);

        assertDoesNotThrow(() -> cartService.deleteById(999));

        verify(cartRepository, times(1)).deleteById(999);
    }

    @Test
    @DisplayName("Should save cart with minimum data")
    void save_WithMinimumData_ShouldSaveCart() {
        Cart minimalCart = new Cart();
        minimalCart.setCartId(5);
        minimalCart.setUserId(2);

        when(cartRepository.save(any(Cart.class))).thenReturn(minimalCart);

        UserDto minimalUser = UserDto.builder()
                .userId(2)
                .build();

        CartDto cartDto = CartDto.builder()
                .userDto(minimalUser)
                .build();

        CartDto result = cartService.save(cartDto);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}
