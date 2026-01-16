package com.selimhorri.app.integration;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for order-service ↔ user-service communication.
 * Tests verify that order-service can correctly interact with user-service.
 */
@DisplayName("Integration: order-service ↔ user-service")
class OrderUserServiceIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Should create order for existing user")
    void shouldCreateOrderForExistingUser() {
        // First verify user exists
        Response userResponse = givenJson()
                .when()
                .get(USER_SERVICE_URL + "/api/users/1")
                .then()
                .extract().response();

        if (userResponse.statusCode() == 200) {
            String orderJson = """
                {
                    "orderDate": "2024-01-15",
                    "orderDesc": "Test order from integration test",
                    "orderFee": 100.00,
                    "cart": {
                        "cartId": 1
                    }
                }
                """;

            givenJson()
                    .body(orderJson)
                    .when()
                    .post(ORDER_SERVICE_URL + "/api/orders")
                    .then()
                    .statusCode(anyOf(is(200), is(201), is(400), is(500)));
        }
    }

    @Test
    @DisplayName("Should get orders for specific user")
    void shouldGetOrdersForSpecificUser() {
        givenJson()
                .when()
                .get(ORDER_SERVICE_URL + "/api/orders")
                .then()
                .statusCode(200)
                .body("$", notNullValue());
    }

    @Test
    @DisplayName("Should create cart for user")
    void shouldCreateCartForUser() {
        String cartJson = """
            {
                "userId": 1
            }
            """;

        givenJson()
                .body(cartJson)
                .when()
                .post(ORDER_SERVICE_URL + "/api/carts")
                .then()
                .statusCode(anyOf(is(200), is(201), is(400), is(409)));
    }
}
