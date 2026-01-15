package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.CredentialRepository;
import com.selimhorri.app.service.impl.CredentialServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("CredentialService Unit Tests")
class CredentialServiceImplTest {

    @Mock
    private CredentialRepository credentialRepository;

    @InjectMocks
    private CredentialServiceImpl credentialService;

    private Credential testCredential;
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

        testCredential = Credential.builder()
                .credentialId(1)
                .username("testuser")
                .password("password123")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("Should return all credentials")
    void findAll_ShouldReturnAllCredentials() {
        User user2 = User.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@test.com")
                .build();

        Credential credential2 = Credential.builder()
                .credentialId(2)
                .username("testuser2")
                .password("password456")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .user(user2)
                .build();

        when(credentialRepository.findAll()).thenReturn(Arrays.asList(testCredential, credential2));

        List<CredentialDto> result = credentialService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(credentialRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return credential by ID")
    void findById_WhenCredentialExists_ShouldReturnCredential() {
        when(credentialRepository.findById(1)).thenReturn(Optional.of(testCredential));

        CredentialDto result = credentialService.findById(1);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(credentialRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when credential not found by ID")
    void findById_WhenCredentialNotExists_ShouldThrowException() {
        when(credentialRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(CredentialNotFoundException.class, () -> {
            credentialService.findById(999);
        });

        verify(credentialRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Should save credential successfully")
    void save_ShouldSaveAndReturnCredential() {
        when(credentialRepository.save(any(Credential.class))).thenReturn(testCredential);

        UserDto userDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .build();

        CredentialDto credentialDto = CredentialDto.builder()
                .username("testuser")
                .password("password123")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .userDto(userDto)
                .build();

        CredentialDto result = credentialService.save(credentialDto);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(credentialRepository, times(1)).save(any(Credential.class));
    }

    @Test
    @DisplayName("Should update credential successfully")
    void update_ShouldUpdateAndReturnCredential() {
        when(credentialRepository.save(any(Credential.class))).thenReturn(testCredential);

        UserDto userDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .build();

        CredentialDto credentialDto = CredentialDto.builder()
                .credentialId(1)
                .username("updateduser")
                .password("newpassword")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .userDto(userDto)
                .build();

        CredentialDto result = credentialService.update(credentialDto);

        assertNotNull(result);
        verify(credentialRepository, times(1)).save(any(Credential.class));
    }

    @Test
    @DisplayName("Should delete credential by ID")
    void deleteById_ShouldDeleteCredential() {
        doNothing().when(credentialRepository).deleteById(1);

        credentialService.deleteById(1);

        verify(credentialRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should find credential by username")
    void findByUsername_WhenCredentialExists_ShouldReturnCredential() {
        when(credentialRepository.findByUsername("testuser")).thenReturn(Optional.of(testCredential));

        CredentialDto result = credentialService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(credentialRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when credential not found by username")
    void findByUsername_WhenCredentialNotExists_ShouldThrowException() {
        when(credentialRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Note: The service throws UserObjectNotFoundException for findByUsername
        assertThrows(UserObjectNotFoundException.class, () -> {
            credentialService.findByUsername("nonexistent");
        });

        verify(credentialRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should return empty list when no credentials exist")
    void findAll_WhenNoCredentials_ShouldReturnEmptyList() {
        when(credentialRepository.findAll()).thenReturn(Arrays.asList());

        List<CredentialDto> result = credentialService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(credentialRepository, times(1)).findAll();
    }
}
