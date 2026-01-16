package com.selimhorri.app.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

/**
 * Base class for integration tests providing common configuration.
 * Tests assume services are running (via docker-compose or K8s).
 */
public abstract class BaseIntegrationTest {

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

    protected static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }
}
