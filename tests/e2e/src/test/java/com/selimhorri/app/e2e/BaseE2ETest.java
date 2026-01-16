package com.selimhorri.app.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Base class for E2E tests providing common configuration and utilities.
 * E2E tests verify complete user flows through multiple services.
 */
public abstract class BaseE2ETest {

    protected static final String API_GATEWAY_URL = getEnvOrDefault("API_GATEWAY_URL", "http://localhost:8080");
    protected static final String USER_SERVICE_URL = getEnvOrDefault("USER_SERVICE_URL", "http://localhost:8700");
    protected static final String PRODUCT_SERVICE_URL = getEnvOrDefault("PRODUCT_SERVICE_URL", "http://localhost:8500");
    protected static final String ORDER_SERVICE_URL = getEnvOrDefault("ORDER_SERVICE_URL", "http://localhost:8300");
    protected static final String PAYMENT_SERVICE_URL = getEnvOrDefault("PAYMENT_SERVICE_URL", "http://localhost:8400");
    protected static final String SHIPPING_SERVICE_URL = getEnvOrDefault("SHIPPING_SERVICE_URL", "http://localhost:8600");
    protected static final String FAVOURITE_SERVICE_URL = getEnvOrDefault("FAVOURITE_SERVICE_URL", "http://localhost:8800");

    @BeforeAll
    static void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected RequestSpecification givenJson() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    protected void waitForServiceReady(String serviceUrl, String healthEndpoint) {
        await().atMost(30, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .until(() -> {
                    try {
                        Response response = RestAssured.get(serviceUrl + healthEndpoint);
                        return response.statusCode() == 200;
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    protected static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    protected int extractId(Response response) {
        try {
            return response.jsonPath().getInt("id");
        } catch (Exception e) {
            return response.jsonPath().getInt("userId");
        }
    }
}
