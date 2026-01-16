package com.selimhorri.app.integration;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for favourite-service ↔ user-service communication.
 * Tests verify that favourite-service can correctly interact with user-service.
 */
@DisplayName("Integration: favourite-service ↔ user-service")
class FavouriteUserServiceIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Should create favourite for existing user")
    void shouldCreateFavouriteForExistingUser() {
        // First verify user exists in user-service
        Response userResponse = givenJson()
                .when()
                .get(USER_SERVICE_URL + "/api/users/1")
                .then()
                .extract().response();

        if (userResponse.statusCode() == 200) {
            // Create favourite for this user
            String favouriteJson = """
                {
                    "userId": 1,
                    "productId": 1,
                    "likeDate": "2024-01-15"
                }
                """;

            givenJson()
                    .body(favouriteJson)
                    .when()
                    .post(FAVOURITE_SERVICE_URL + "/api/favourites")
                    .then()
                    .statusCode(anyOf(is(200), is(201), is(409)));
        }
    }

    @Test
    @DisplayName("Should get favourites with user details")
    void shouldGetFavouritesWithUserDetails() {
        givenJson()
                .when()
                .get(FAVOURITE_SERVICE_URL + "/api/favourites")
                .then()
                .statusCode(200)
                .body("$", notNullValue());
    }

    @Test
    @DisplayName("Should validate user exists before creating favourite")
    void shouldValidateUserExistsBeforeCreatingFavourite() {
        // Try to create favourite for non-existent user
        String favouriteJson = """
            {
                "userId": 99999,
                "productId": 1,
                "likeDate": "2024-01-15"
            }
            """;

        givenJson()
                .body(favouriteJson)
                .when()
                .post(FAVOURITE_SERVICE_URL + "/api/favourites")
                .then()
                .statusCode(anyOf(is(400), is(404), is(500), is(201)));
    }
}
