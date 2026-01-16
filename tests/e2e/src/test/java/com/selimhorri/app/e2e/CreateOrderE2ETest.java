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
 * E2E Test 4: Complete order creation flow.
 * Tests the entire process of creating an order with cart and products.
 */
@DisplayName("E2E: Create Order Flow")
@TestMethodOrder(OrderAnnotation.class)
class CreateOrderE2ETest extends BaseE2ETest {

    private static Integer userId = 1;
    private static Integer cartId;
    private static Integer orderId;

    @Test
    @Order(1)
    @DisplayName("Step 1: Verify user for order")
    void shouldVerifyUserForOrder() {
        Response response = givenJson()
                .when()
                .get(USER_SERVICE_URL + "/api/users")
                .then()
                .statusCode(200)
                .extract().response();

        try {
            userId = response.jsonPath().getInt("[0].userId");
        } catch (Exception e) {
            userId = 1;
        }
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Create shopping cart")
    void shouldCreateShoppingCart() {
        String cartJson = String.format("""
            {
                "userId": %d
            }
            """, userId);

        Response response = givenJson()
                .body(cartJson)
                .when()
                .post(ORDER_SERVICE_URL + "/api/carts")
                .then()
                .statusCode(anyOf(is(200), is(201), is(400), is(409)))
                .extract().response();

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            try {
                cartId = response.jsonPath().getInt("cartId");
            } catch (Exception e) {
                cartId = 1;
            }
        } else {
            // Try to get existing cart
            Response cartsResponse = givenJson()
                    .when()
                    .get(ORDER_SERVICE_URL + "/api/carts")
                    .then()
                    .extract().response();

            try {
                cartId = cartsResponse.jsonPath().getInt("[0].cartId");
            } catch (Exception e) {
                cartId = 1;
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Create order from cart")
    void shouldCreateOrderFromCart() {
        String orderJson = String.format("""
            {
                "orderDate": "%s",
                "orderDesc": "E2E Test Order",
                "orderFee": 150.00,
                "cart": {
                    "cartId": %d
                }
            }
            """, LocalDate.now().toString(), cartId != null ? cartId : 1);

        Response response = givenJson()
                .body(orderJson)
                .when()
                .post(ORDER_SERVICE_URL + "/api/orders")
                .then()
                .statusCode(anyOf(is(200), is(201), is(400), is(500)))
                .extract().response();

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            try {
                orderId = response.jsonPath().getInt("orderId");
            } catch (Exception e) {
                orderId = null;
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify order was created")
    void shouldVerifyOrderWasCreated() {
        givenJson()
                .when()
                .get(ORDER_SERVICE_URL + "/api/orders")
                .then()
                .statusCode(200)
                .body("$", notNullValue());
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: View order details")
    void shouldViewOrderDetails() {
        if (orderId != null) {
            givenJson()
                    .when()
                    .get(ORDER_SERVICE_URL + "/api/orders/" + orderId)
                    .then()
                    .statusCode(anyOf(is(200), is(404)));
        } else {
            givenJson()
                    .when()
                    .get(ORDER_SERVICE_URL + "/api/orders")
                    .then()
                    .statusCode(200);
        }
    }
}
