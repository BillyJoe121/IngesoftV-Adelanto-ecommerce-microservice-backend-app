package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.domain.Favourite;
import com.selimhorri.app.domain.id.FavouriteId;
import com.selimhorri.app.dto.FavouriteDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.FavouriteNotFoundException;
import com.selimhorri.app.repository.FavouriteRepository;
import com.selimhorri.app.service.impl.FavouriteServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavouriteService Unit Tests")
class FavouriteServiceImplTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FavouriteServiceImpl favouriteService;

    private Favourite testFavourite;
    private FavouriteId testFavouriteId;
    private UserDto testUserDto;
    private ProductDto testProductDto;
    private LocalDateTime testLikeDate;

    @BeforeEach
    void setUp() {
        testLikeDate = LocalDateTime.now();
        testFavouriteId = new FavouriteId(1, 1, testLikeDate);

        testFavourite = new Favourite();
        testFavourite.setUserId(1);
        testFavourite.setProductId(1);
        testFavourite.setLikeDate(testLikeDate);

        testUserDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .build();

        testProductDto = ProductDto.builder()
                .productId(1)
                .productTitle("Test Product")
                .priceUnit(99.99)
                .quantity(50)
                .build();
    }

    @Test
    @DisplayName("Should save favourite successfully")
    void save_ShouldSaveAndReturnFavourite() {
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(testFavourite);

        FavouriteDto favouriteDto = FavouriteDto.builder()
                .userId(1)
                .productId(1)
                .likeDate(testLikeDate)
                .userDto(testUserDto)
                .productDto(testProductDto)
                .build();

        FavouriteDto result = favouriteService.save(favouriteDto);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getProductId());
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    @DisplayName("Should update favourite successfully")
    void update_ShouldUpdateAndReturnFavourite() {
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(testFavourite);

        FavouriteDto favouriteDto = FavouriteDto.builder()
                .userId(1)
                .productId(1)
                .likeDate(testLikeDate)
                .userDto(testUserDto)
                .productDto(testProductDto)
                .build();

        FavouriteDto result = favouriteService.update(favouriteDto);

        assertNotNull(result);
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    @DisplayName("Should delete favourite by ID")
    void deleteById_ShouldDeleteFavourite() {
        doNothing().when(favouriteRepository).deleteById(any(FavouriteId.class));

        favouriteService.deleteById(testFavouriteId);

        verify(favouriteRepository, times(1)).deleteById(any(FavouriteId.class));
    }

    @Test
    @DisplayName("Should handle user adding multiple favourites")
    void save_MultipleProductsSameUser_ShouldSaveAll() {
        Favourite favourite1 = new Favourite();
        favourite1.setUserId(1);
        favourite1.setProductId(1);
        favourite1.setLikeDate(testLikeDate);

        Favourite favourite2 = new Favourite();
        favourite2.setUserId(1);
        favourite2.setProductId(2);
        favourite2.setLikeDate(testLikeDate.plusMinutes(5));

        when(favouriteRepository.save(any(Favourite.class)))
                .thenReturn(favourite1)
                .thenReturn(favourite2);

        FavouriteDto favouriteDto1 = FavouriteDto.builder()
                .userId(1)
                .productId(1)
                .likeDate(testLikeDate)
                .build();

        FavouriteDto favouriteDto2 = FavouriteDto.builder()
                .userId(1)
                .productId(2)
                .likeDate(testLikeDate.plusMinutes(5))
                .build();

        FavouriteDto result1 = favouriteService.save(favouriteDto1);
        FavouriteDto result2 = favouriteService.save(favouriteDto2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(1, result1.getProductId());
        assertEquals(2, result2.getProductId());
        verify(favouriteRepository, times(2)).save(any(Favourite.class));
    }

    @Test
    @DisplayName("Should handle multiple users favouriting same product")
    void save_SameProductMultipleUsers_ShouldSaveAll() {
        Favourite favouriteUser1 = new Favourite();
        favouriteUser1.setUserId(1);
        favouriteUser1.setProductId(1);
        favouriteUser1.setLikeDate(testLikeDate);

        Favourite favouriteUser2 = new Favourite();
        favouriteUser2.setUserId(2);
        favouriteUser2.setProductId(1);
        favouriteUser2.setLikeDate(testLikeDate.plusMinutes(10));

        when(favouriteRepository.save(any(Favourite.class)))
                .thenReturn(favouriteUser1)
                .thenReturn(favouriteUser2);

        FavouriteDto favouriteDto1 = FavouriteDto.builder()
                .userId(1)
                .productId(1)
                .likeDate(testLikeDate)
                .build();

        FavouriteDto favouriteDto2 = FavouriteDto.builder()
                .userId(2)
                .productId(1)
                .likeDate(testLikeDate.plusMinutes(10))
                .build();

        FavouriteDto result1 = favouriteService.save(favouriteDto1);
        FavouriteDto result2 = favouriteService.save(favouriteDto2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(1, result1.getUserId());
        assertEquals(2, result2.getUserId());
        verify(favouriteRepository, times(2)).save(any(Favourite.class));
    }

    @Test
    @DisplayName("Should handle favourite with past like date")
    void save_WithPastLikeDate_ShouldSaveFavourite() {
        LocalDateTime pastDate = LocalDateTime.of(2023, 1, 15, 10, 30);

        Favourite pastFavourite = new Favourite();
        pastFavourite.setUserId(1);
        pastFavourite.setProductId(1);
        pastFavourite.setLikeDate(pastDate);

        when(favouriteRepository.save(any(Favourite.class))).thenReturn(pastFavourite);

        FavouriteDto favouriteDto = FavouriteDto.builder()
                .userId(1)
                .productId(1)
                .likeDate(pastDate)
                .build();

        FavouriteDto result = favouriteService.save(favouriteDto);

        assertNotNull(result);
        assertEquals(pastDate, result.getLikeDate());
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    @DisplayName("Should delete favourite for specific user and product")
    void deleteById_SpecificUserAndProduct_ShouldDelete() {
        FavouriteId specificId = new FavouriteId(5, 10, testLikeDate);
        doNothing().when(favouriteRepository).deleteById(any(FavouriteId.class));

        favouriteService.deleteById(specificId);

        verify(favouriteRepository, times(1)).deleteById(any(FavouriteId.class));
    }

    @Test
    @DisplayName("Should handle favourite with different products")
    void save_DifferentProducts_ShouldSaveCorrectly() {
        Favourite expensiveProductFav = new Favourite();
        expensiveProductFav.setUserId(1);
        expensiveProductFav.setProductId(100);
        expensiveProductFav.setLikeDate(testLikeDate);

        when(favouriteRepository.save(any(Favourite.class))).thenReturn(expensiveProductFav);

        ProductDto expensiveProduct = ProductDto.builder()
                .productId(100)
                .productTitle("Expensive Product")
                .priceUnit(9999.99)
                .build();

        FavouriteDto favouriteDto = FavouriteDto.builder()
                .userId(1)
                .productId(100)
                .likeDate(testLikeDate)
                .productDto(expensiveProduct)
                .build();

        FavouriteDto result = favouriteService.save(favouriteDto);

        assertNotNull(result);
        assertEquals(100, result.getProductId());
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    @DisplayName("Should handle rapid favourite additions")
    void save_RapidAdditions_ShouldHandleAll() {
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(testFavourite);

        for (int i = 0; i < 5; i++) {
            FavouriteDto favouriteDto = FavouriteDto.builder()
                    .userId(1)
                    .productId(i + 1)
                    .likeDate(testLikeDate.plusSeconds(i))
                    .build();

            FavouriteDto result = favouriteService.save(favouriteDto);
            assertNotNull(result);
        }

        verify(favouriteRepository, times(5)).save(any(Favourite.class));
    }
}
