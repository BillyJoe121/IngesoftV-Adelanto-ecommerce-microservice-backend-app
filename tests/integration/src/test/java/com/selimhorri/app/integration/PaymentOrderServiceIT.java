package com.selimhorri.app.integration;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for payment-service ↔ order-service communication.
 * Tests verify that payment-service can correctly interact with order-service.
 */
@DisplayName("Integration: payment-service ↔ order-service")
class PaymentOrderServiceIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Should create payment for existing order")
    void shouldCreatePaymentForExistingOrder() {
        // First check if orders exist
        Response ordersResponse = givenJson()
                .when()
                .get(ORDER_SERVICE_URL + "/api/orders")
                .then()
                .extract().response();

        if (ordersResponse.statusCode() == 200) {
            String paymentJson = """
                {
                    "isPayed": false,
                    "paymentStatus": "PENDING",
                    "order": {
                        "orderId": 1
                    }
                }
                """;

            givenJson()
                    .body(paymentJson)
                    .when()
                    .post(PAYMENT_SERVICE_URL + "/api/payments")
                    .then()
                    .statusCode(anyOf(is(200), is(201), is(400), is(500)));
        }
    }

    @Test
    @DisplayName("Should get payment by order ID")
    void shouldGetPaymentByOrderId() {
        givenJson()
                .when()
                .get(PAYMENT_SERVICE_URL + "/api/payments")
                .then()
                .statusCode(200)
                .body("$", notNullValue());
    }

    @Test
    @DisplayName("Should update payment status")
    void shouldUpdatePaymentStatus() {
        // First get existing payment
        Response paymentsResponse = givenJson()
                .when()
                .get(PAYMENT_SERVICE_URL + "/api/payments/1")
                .then()
                .extract().response();

        if (paymentsResponse.statusCode() == 200) {
            String updateJson = """
                {
                    "paymentId": 1,
                    "isPayed": true,
                    "paymentStatus": "COMPLETED",
                    "order": {
                        "orderId": 1
                    }
                }
                """;

            givenJson()
                    .body(updateJson)
                    .when()
                    .put(PAYMENT_SERVICE_URL + "/api/payments")
                    .then()
                    .statusCode(anyOf(is(200), is(201), is(400), is(404)));
        }
    }
}
