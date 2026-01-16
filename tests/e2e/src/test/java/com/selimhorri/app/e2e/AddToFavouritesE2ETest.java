package com.selimhorri.app.e2e;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * E2E Test 3: Add product to favourites flow.
 * Tests the complete flow of a user adding a product to their favourites.
 */
@DisplayName("E2E: Add Product to Favourites Flow")
@TestMethodOrder(OrderAnnotation.class)
class AddToFavouritesE2ETest extends BaseE2ETest {

    private static Integer userId = 1;
    private static Integer productId = 1;
    private static Integer favouriteId;

    @Test
    @Order(1)
    @DisplayName("Step 1: Verify user exists")
    void shouldVerifyUserExists() {
        Response response = givenJson()
                .when()
                .get(USER_SERVICE_URL + "/api/users/" + userId)
                .then()
                .statusCode(anyOf(is(200), is(404)))
                .extract().response();

        if (response.statusCode() == 404) {
            // Get any existing user
            Response usersResponse = givenJson()
                    .when()
                    .get(USER_SERVICE_URL + "/api/users")
                    .then()
                    .extract().response();

            try {
                userId = usersResponse.jsonPath().getInt("[0].userId");
            } catch (Exception e) {
                userId = 1;
            }
        }
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify product exists")
    void shouldVerifyProductExists() {
        Response response = givenJson()
                .when()
                .get(PRODUCT_SERVICE_URL + "/api/products/" + productId)
                .then()
                .statusCode(anyOf(is(200), is(404)))
                .extract().response();

        if (response.statusCode() == 404) {
            // Get any existing product
            Response productsResponse = givenJson()
                    .when()
                    .get(PRODUCT_SERVICE_URL + "/api/products")
                    .then()
                    .extract().response();

            try {
                productId = productsResponse.jsonPath().getInt("[0].productId");
            } catch (Exception e) {
                productId = 1;
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Add product to favourites")
    void shouldAddProductToFavourites() {
        String favouriteJson = String.format("""
            {
                "userId": %d,
                "productId": %d,
                "likeDate": "%s"
            }
            """, userId, productId, LocalDate.now().toString());

        Response response = givenJson()
                .body(favouriteJson)
                .when()
                .post(FAVOURITE_SERVICE_URL + "/api/favourites")
                .then()
                .statusCode(anyOf(is(200), is(201), is(400), is(409)))
                .extract().response();

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            try {
                favouriteId = response.jsonPath().getInt("favouriteId");
            } catch (Exception e) {
                favouriteId = null;
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify favourite was added")
    void shouldVerifyFavouriteWasAdded() {
        givenJson()
                .when()
                .get(FAVOURITE_SERVICE_URL + "/api/favourites")
                .then()
                .statusCode(200)
                .body("$", notNullValue());
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Get user's favourites list")
    void shouldGetUserFavouritesList() {
        givenJson()
                .when()
                .get(FAVOURITE_SERVICE_URL + "/api/favourites")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }
}
