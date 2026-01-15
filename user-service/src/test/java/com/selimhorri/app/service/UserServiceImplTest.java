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

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Credential testCredential;

    @BeforeEach
    void setUp() {
        testCredential = Credential.builder()
                .credentialId(1)
                .username("testuser")
                .password("password123")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        testUser = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .phone("1234567890")
                .credential(testCredential)
                .build();
    }

    @Test
    @DisplayName("Should return all users")
    void findAll_ShouldReturnAllUsers() {
        Credential credential2 = Credential.builder()
                .credentialId(2)
                .username("testuser2")
                .password("password456")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        User user2 = User.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@test.com")
                .credential(credential2)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<UserDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return user by ID")
    void findById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        UserDto result = userService.findById(1);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@test.com", result.getEmail());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void findById_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserObjectNotFoundException.class, () -> {
            userService.findById(999);
        });

        verify(userRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Should save user successfully")
    void save_ShouldSaveAndReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        CredentialDto credentialDto = CredentialDto.builder()
                .credentialId(1)
                .username("testuser")
                .password("password123")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        UserDto userDto = UserDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .phone("1234567890")
                .credentialDto(credentialDto)
                .build();

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user by ID")
    void deleteById_ShouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1);

        userService.deleteById(1);

        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should find user by username")
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByCredentialUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDto result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(userRepository, times(1)).findByCredentialUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found by username")
    void findByUsername_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findByCredentialUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UserObjectNotFoundException.class, () -> {
            userService.findByUsername("nonexistent");
        });

        verify(userRepository, times(1)).findByCredentialUsername("nonexistent");
    }
}
