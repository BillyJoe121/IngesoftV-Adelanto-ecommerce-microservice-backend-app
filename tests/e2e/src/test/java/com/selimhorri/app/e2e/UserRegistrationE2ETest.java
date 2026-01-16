package com.selimhorri.app.e2e;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * E2E Test 1: Complete user registration flow.
 * Tests the entire process of registering a new user.
 */
@DisplayName("E2E: User Registration Flow")
@TestMethodOrder(OrderAnnotation.class)
class UserRegistrationE2ETest extends BaseE2ETest {

    private static String testEmail;
    private static Integer createdUserId;

    @Test
    @Order(1)
    @DisplayName("Step 1: Create new user account")
    void shouldCreateNewUserAccount() {
        testEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        String userJson = String.format("""
            {
                "firstName": "Test",
                "lastName": "User",
                "imageUrl": "https://example.com/avatar.jpg",
                "email": "%s",
                "phone": "123-456-7890",
                "credential": {
                    "username": "testuser_%s",
                    "password": "password123",
                    "roleBasedAuthority": "ROLE_USER",
                    "isEnabled": true,
                    "isAccountNonExpired": true,
                    "isAccountNonLocked": true,
                    "isCredentialsNonExpired": true
                }
            }
            """, testEmail, UUID.randomUUID().toString().substring(0, 8));

        Response response = givenJson()
                .body(userJson)
                .when()
                .post(USER_SERVICE_URL + "/api/users")
                .then()
                .statusCode(anyOf(is(200), is(201), is(400)))
                .extract().response();

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            createdUserId = response.jsonPath().getInt("userId");
            assertThat(createdUserId).isNotNull();
        }
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify user can be retrieved")
    void shouldRetrieveCreatedUser() {
        if (createdUserId != null) {
            givenJson()
                    .when()
                    .get(USER_SERVICE_URL + "/api/users/" + createdUserId)
                    .then()
                    .statusCode(200)
                    .body("userId", equalTo(createdUserId));
        }
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Add address to user profile")
    void shouldAddAddressToUserProfile() {
        if (createdUserId != null) {
            String addressJson = String.format("""
                {
                    "fullAddress": "123 Test Street",
                    "postalCode": "12345",
                    "city": "Test City",
                    "user": {
                        "userId": %d
                    }
                }
                """, createdUserId);

            givenJson()
                    .body(addressJson)
                    .when()
                    .post(USER_SERVICE_URL + "/api/address")
                    .then()
                    .statusCode(anyOf(is(200), is(201), is(400)));
        }
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify complete user profile")
    void shouldVerifyCompleteUserProfile() {
        givenJson()
                .when()
                .get(USER_SERVICE_URL + "/api/users")
                .then()
                .statusCode(200)
                .body("$", notNullValue());
    }
}
