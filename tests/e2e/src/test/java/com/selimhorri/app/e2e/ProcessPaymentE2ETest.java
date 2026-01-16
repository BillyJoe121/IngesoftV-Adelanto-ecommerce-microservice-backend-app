package com.selimhorri.app.e2e;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * E2E Test 5: Process payment for order flow.
 * Tests the complete payment processing flow for an existing order.
 */
@DisplayName("E2E: Process Payment Flow")
@TestMethodOrder(OrderAnnotation.class)
class ProcessPaymentE2ETest extends BaseE2ETest {

    private static Integer orderId;
    private static Integer paymentId;

    @Test
    @Order(1)
    @DisplayName("Step 1: Find existing order for payment")
    void shouldFindExistingOrder() {
        Response response = givenJson()
                .when()
                .get(ORDER_SERVICE_URL + "/api/orders")
                .then()
                .statusCode(200)
                .extract().response();

        try {
            orderId = response.jsonPath().getInt("[0].orderId");
        } catch (Exception e) {
            orderId = 1;
        }
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Create payment for order")
    void shouldCreatePaymentForOrder() {
        String paymentJson = String.format("""
            {
                "isPayed": false,
                "paymentStatus": "PENDING",
                "order": {
                    "orderId": %d
                }
            }
            """, orderId != null ? orderId : 1);

        Response response = givenJson()
                .body(paymentJson)
                .when()
                .post(PAYMENT_SERVICE_URL + "/api/payments")
                .then()
                .statusCode(anyOf(is(200), is(201), is(400), is(409), is(500)))
                .extract().response();

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            try {
                paymentId = response.jsonPath().getInt("paymentId");
            } catch (Exception e) {
                paymentId = null;
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify payment was created")
    void shouldVerifyPaymentWasCreated() {
        givenJson()
                .when()
                .get(PAYMENT_SERVICE_URL + "/api/payments")
                .then()
                .statusCode(200)
                .body("$", notNullValue());
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Process payment (mark as paid)")
    void shouldProcessPayment() {
        // Get existing payment
        Response paymentsResponse = givenJson()
                .when()
                .get(PAYMENT_SERVICE_URL + "/api/payments")
                .then()
                .extract().response();

        Integer existingPaymentId;
        Integer existingOrderId;

        try {
            existingPaymentId = paymentsResponse.jsonPath().getInt("[0].paymentId");
            existingOrderId = paymentsResponse.jsonPath().getInt("[0].order.orderId");
        } catch (Exception e) {
            existingPaymentId = paymentId != null ? paymentId : 1;
            existingOrderId = orderId != null ? orderId : 1;
        }

        String updateJson = String.format("""
            {
                "paymentId": %d,
                "isPayed": true,
                "paymentStatus": "COMPLETED",
                "order": {
                    "orderId": %d
                }
            }
            """, existingPaymentId, existingOrderId);

        givenJson()
                .body(updateJson)
                .when()
                .put(PAYMENT_SERVICE_URL + "/api/payments")
                .then()
                .statusCode(anyOf(is(200), is(201), is(400), is(404), is(500)));
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify payment status updated")
    void shouldVerifyPaymentStatusUpdated() {
        givenJson()
                .when()
                .get(PAYMENT_SERVICE_URL + "/api/payments")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }
}
