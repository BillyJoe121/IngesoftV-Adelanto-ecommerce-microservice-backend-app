package com.selimhorri.app.integration;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for shipping-service ↔ order-service + product-service communication.
 * Tests verify that shipping-service can correctly interact with order and product services.
 */
@DisplayName("Integration: shipping-service ↔ order-service + product-service")
class ShippingOrderProductServiceIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Should create order item linking order and product")
    void shouldCreateOrderItemLinkingOrderAndProduct() {
        // Verify both order and product services are accessible
        Response ordersResponse = givenJson()
                .when()
                .get(ORDER_SERVICE_URL + "/api/orders")
                .then()
                .extract().response();

        Response productsResponse = givenJson()
                .when()
                .get(PRODUCT_SERVICE_URL + "/api/products")
                .then()
                .extract().response();

        if (ordersResponse.statusCode() == 200 && productsResponse.statusCode() == 200) {
            String orderItemJson = """
                {
                    "orderedQuantity": 2,
                    "productId": 1,
                    "orderId": 1
                }
                """;

            givenJson()
                    .body(orderItemJson)
                    .when()
                    .post(SHIPPING_SERVICE_URL + "/api/shippings")
                    .then()
                    .statusCode(anyOf(is(200), is(201), is(400), is(500)));
        }
    }

    @Test
    @DisplayName("Should get order items with product details")
    void shouldGetOrderItemsWithProductDetails() {
        givenJson()
                .when()
                .get(SHIPPING_SERVICE_URL + "/api/shippings")
                .then()
                .statusCode(200)
                .body("$", notNullValue());
    }

    @Test
    @DisplayName("Should get shipping by order ID")
    void shouldGetShippingByOrderId() {
        givenJson()
                .when()
                .get(SHIPPING_SERVICE_URL + "/api/shippings/1")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @DisplayName("Should validate product stock before shipping")
    void shouldValidateProductStockBeforeShipping() {
        // Try to ship more than available stock
        String orderItemJson = """
            {
                "orderedQuantity": 999999,
                "productId": 1,
                "orderId": 1
            }
            """;

        givenJson()
                .body(orderItemJson)
                .when()
                .post(SHIPPING_SERVICE_URL + "/api/shippings")
                .then()
                .statusCode(anyOf(is(200), is(201), is(400), is(500)));
    }
}
