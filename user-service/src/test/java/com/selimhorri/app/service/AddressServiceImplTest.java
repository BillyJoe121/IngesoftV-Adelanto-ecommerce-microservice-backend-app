package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import com.selimhorri.app.domain.Address;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.AddressNotFoundException;
import com.selimhorri.app.repository.AddressRepository;
import com.selimhorri.app.service.impl.AddressServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Unit Tests")
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address testAddress;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .phone("1234567890")
                .build();

        testAddress = Address.builder()
                .addressId(1)
                .fullAddress("123 Main Street")
                .postalCode("12345")
                .city("Test City")
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("Should return all addresses")
    void findAll_ShouldReturnAllAddresses() {
        Address address2 = Address.builder()
                .addressId(2)
                .fullAddress("456 Oak Avenue")
                .postalCode("67890")
                .city("Another City")
                .user(testUser)
                .build();

        when(addressRepository.findAll()).thenReturn(Arrays.asList(testAddress, address2));

        List<AddressDto> result = addressService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return address by ID")
    void findById_WhenAddressExists_ShouldReturnAddress() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(testAddress));

        AddressDto result = addressService.findById(1);

        assertNotNull(result);
        assertEquals("123 Main Street", result.getFullAddress());
        assertEquals("Test City", result.getCity());
        verify(addressRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when address not found by ID")
    void findById_WhenAddressNotExists_ShouldThrowException() {
        when(addressRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> {
            addressService.findById(999);
        });

        verify(addressRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Should save address successfully")
    void save_ShouldSaveAndReturnAddress() {
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        UserDto userDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .build();

        AddressDto addressDto = AddressDto.builder()
                .fullAddress("123 Main Street")
                .postalCode("12345")
                .city("Test City")
                .userDto(userDto)
                .build();

        AddressDto result = addressService.save(addressDto);

        assertNotNull(result);
        assertEquals("123 Main Street", result.getFullAddress());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should update address successfully")
    void update_ShouldUpdateAndReturnAddress() {
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        UserDto userDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .build();

        AddressDto addressDto = AddressDto.builder()
                .addressId(1)
                .fullAddress("Updated Address")
                .postalCode("54321")
                .city("Updated City")
                .userDto(userDto)
                .build();

        AddressDto result = addressService.update(addressDto);

        assertNotNull(result);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should delete address by ID")
    void deleteById_ShouldDeleteAddress() {
        doNothing().when(addressRepository).deleteById(1);

        addressService.deleteById(1);

        verify(addressRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should return empty list when no addresses exist")
    void findAll_WhenNoAddresses_ShouldReturnEmptyList() {
        when(addressRepository.findAll()).thenReturn(Arrays.asList());

        List<AddressDto> result = addressService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(addressRepository, times(1)).findAll();
    }
}
