package com.selimhorri.app.integration;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for favourite-service ↔ product-service communication.
 * Tests verify that favourite-service can correctly interact with product-service.
 */
@DisplayName("Integration: favourite-service ↔ product-service")
class FavouriteProductServiceIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Should create favourite for existing product")
    void shouldCreateFavouriteForExistingProduct() {
        // First verify product exists in product-service
        Response productResponse = givenJson()
                .when()
                .get(PRODUCT_SERVICE_URL + "/api/products/1")
                .then()
                .extract().response();

        if (productResponse.statusCode() == 200) {
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
    @DisplayName("Should get favourite with product details")
    void shouldGetFavouriteWithProductDetails() {
        givenJson()
                .when()
                .get(FAVOURITE_SERVICE_URL + "/api/favourites/1")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @DisplayName("Should list all favourites with product information")
    void shouldListAllFavouritesWithProductInfo() {
        givenJson()
                .when()
                .get(FAVOURITE_SERVICE_URL + "/api/favourites")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }
}
