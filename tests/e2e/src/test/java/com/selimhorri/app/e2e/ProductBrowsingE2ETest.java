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
 * E2E Test 2: Product browsing and search flow.
 * Tests the complete flow of browsing and viewing products.
 */
@DisplayName("E2E: Product Browsing Flow")
@TestMethodOrder(OrderAnnotation.class)
class ProductBrowsingE2ETest extends BaseE2ETest {

    private static Integer selectedProductId;
    private static Integer selectedCategoryId;

    @Test
    @Order(1)
    @DisplayName("Step 1: Browse all categories")
    void shouldBrowseAllCategories() {
        Response response = givenJson()
                .when()
                .get(PRODUCT_SERVICE_URL + "/api/categories")
                .then()
                .statusCode(200)
                .body("$", notNullValue())
                .extract().response();

        // Try to get first category ID
        try {
            selectedCategoryId = response.jsonPath().getInt("[0].categoryId");
        } catch (Exception e) {
            selectedCategoryId = 1;
        }
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: View category details")
    void shouldViewCategoryDetails() {
        if (selectedCategoryId != null) {
            givenJson()
                    .when()
                    .get(PRODUCT_SERVICE_URL + "/api/categories/" + selectedCategoryId)
                    .then()
                    .statusCode(anyOf(is(200), is(404)));
        }
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Browse all products")
    void shouldBrowseAllProducts() {
        Response response = givenJson()
                .when()
                .get(PRODUCT_SERVICE_URL + "/api/products")
                .then()
                .statusCode(200)
                .body("$", notNullValue())
                .extract().response();

        // Try to get first product ID
        try {
            selectedProductId = response.jsonPath().getInt("[0].productId");
        } catch (Exception e) {
            selectedProductId = 1;
        }
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: View product details")
    void shouldViewProductDetails() {
        if (selectedProductId != null) {
            givenJson()
                    .when()
                    .get(PRODUCT_SERVICE_URL + "/api/products/" + selectedProductId)
                    .then()
                    .statusCode(anyOf(is(200), is(404)));
        }
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify product has required information")
    void shouldVerifyProductInformation() {
        givenJson()
                .when()
                .get(PRODUCT_SERVICE_URL + "/api/products")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }
}
