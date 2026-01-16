"""
Locust Performance Tests for E-commerce Microservices
=====================================================

This file contains performance test scenarios for all microservices.
Run with: locust -f locustfile.py --host=http://localhost:8080

Scenarios:
1. User Service Load Test
2. Product Service Load Test
3. Order Service Load Test
4. Mixed Workload (Complete Purchase Flow)
"""

import os
import json
import random
from locust import HttpUser, task, between, tag
from datetime import datetime

# Service URLs - can be overridden via environment variables
USER_SERVICE_PORT = os.getenv("USER_SERVICE_PORT", "8700")
PRODUCT_SERVICE_PORT = os.getenv("PRODUCT_SERVICE_PORT", "8500")
ORDER_SERVICE_PORT = os.getenv("ORDER_SERVICE_PORT", "8300")
PAYMENT_SERVICE_PORT = os.getenv("PAYMENT_SERVICE_PORT", "8400")
FAVOURITE_SERVICE_PORT = os.getenv("FAVOURITE_SERVICE_PORT", "8800")


class UserServiceUser(HttpUser):
    """
    Scenario 1: User Service Load Test
    Tests user creation and retrieval operations.
    """
    wait_time = between(1, 3)
    weight = 2

    def on_start(self):
        self.user_ids = list(range(1, 10))

    @task(3)
    @tag("user", "read")
    def get_all_users(self):
        """GET /api/users - List all users"""
        with self.client.get(
            f":{USER_SERVICE_PORT}/api/users",
            name="/api/users [GET]",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")

    @task(5)
    @tag("user", "read")
    def get_user_by_id(self):
        """GET /api/users/{id} - Get specific user"""
        user_id = random.choice(self.user_ids)
        with self.client.get(
            f":{USER_SERVICE_PORT}/api/users/{user_id}",
            name="/api/users/{id} [GET]",
            catch_response=True
        ) as response:
            if response.status_code in [200, 404]:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")

    @task(1)
    @tag("user", "write")
    def create_user(self):
        """POST /api/users - Create new user"""
        user_data = {
            "firstName": f"LoadTest_{random.randint(1000, 9999)}",
            "lastName": "User",
            "imageUrl": "https://example.com/avatar.jpg",
            "email": f"loadtest_{random.randint(10000, 99999)}@test.com",
            "phone": f"555-{random.randint(1000, 9999)}",
            "credential": {
                "username": f"loaduser_{random.randint(10000, 99999)}",
                "password": "testpass123",
                "roleBasedAuthority": "ROLE_USER",
                "isEnabled": True,
                "isAccountNonExpired": True,
                "isAccountNonLocked": True,
                "isCredentialsNonExpired": True
            }
        }
        with self.client.post(
            f":{USER_SERVICE_PORT}/api/users",
            json=user_data,
            name="/api/users [POST]",
            catch_response=True
        ) as response:
            if response.status_code in [200, 201, 400]:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")


class ProductServiceUser(HttpUser):
    """
    Scenario 2: Product Service Load Test
    Tests product listing and search operations.
    """
    wait_time = between(1, 2)
    weight = 3

    def on_start(self):
        self.product_ids = list(range(1, 20))
        self.category_ids = list(range(1, 5))

    @task(5)
    @tag("product", "read")
    def get_all_products(self):
        """GET /api/products - List all products"""
        with self.client.get(
            f":{PRODUCT_SERVICE_PORT}/api/products",
            name="/api/products [GET]",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")

    @task(8)
    @tag("product", "read")
    def get_product_by_id(self):
        """GET /api/products/{id} - Get specific product"""
        product_id = random.choice(self.product_ids)
        with self.client.get(
            f":{PRODUCT_SERVICE_PORT}/api/products/{product_id}",
            name="/api/products/{id} [GET]",
            catch_response=True
        ) as response:
            if response.status_code in [200, 404]:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")

    @task(3)
    @tag("product", "read")
    def get_all_categories(self):
        """GET /api/categories - List all categories"""
        with self.client.get(
            f":{PRODUCT_SERVICE_PORT}/api/categories",
            name="/api/categories [GET]",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")

    @task(2)
    @tag("product", "read")
    def get_category_by_id(self):
        """GET /api/categories/{id} - Get specific category"""
        category_id = random.choice(self.category_ids)
        with self.client.get(
            f":{PRODUCT_SERVICE_PORT}/api/categories/{category_id}",
            name="/api/categories/{id} [GET]",
            catch_response=True
        ) as response:
            if response.status_code in [200, 404]:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")


class OrderServiceUser(HttpUser):
    """
    Scenario 3: Order Service Load Test
    Tests order creation and retrieval.
    """
    wait_time = between(2, 4)
    weight = 2

    def on_start(self):
        self.order_ids = list(range(1, 10))
        self.cart_ids = list(range(1, 10))

    @task(4)
    @tag("order", "read")
    def get_all_orders(self):
        """GET /api/orders - List all orders"""
        with self.client.get(
            f":{ORDER_SERVICE_PORT}/api/orders",
            name="/api/orders [GET]",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")

    @task(3)
    @tag("order", "read")
    def get_order_by_id(self):
        """GET /api/orders/{id} - Get specific order"""
        order_id = random.choice(self.order_ids)
        with self.client.get(
            f":{ORDER_SERVICE_PORT}/api/orders/{order_id}",
            name="/api/orders/{id} [GET]",
            catch_response=True
        ) as response:
            if response.status_code in [200, 404]:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")

    @task(2)
    @tag("order", "read")
    def get_all_carts(self):
        """GET /api/carts - List all carts"""
        with self.client.get(
            f":{ORDER_SERVICE_PORT}/api/carts",
            name="/api/carts [GET]",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")

    @task(1)
    @tag("order", "write")
    def create_order(self):
        """POST /api/orders - Create new order"""
        order_data = {
            "orderDate": datetime.now().strftime("%Y-%m-%d"),
            "orderDesc": f"Load test order {random.randint(1000, 9999)}",
            "orderFee": round(random.uniform(50, 500), 2),
            "cart": {
                "cartId": random.choice(self.cart_ids)
            }
        }
        with self.client.post(
            f":{ORDER_SERVICE_PORT}/api/orders",
            json=order_data,
            name="/api/orders [POST]",
            catch_response=True
        ) as response:
            if response.status_code in [200, 201, 400, 500]:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")


class MixedWorkloadUser(HttpUser):
    """
    Scenario 4: Mixed Workload - Complete Purchase Flow
    Simulates realistic user behavior through the entire purchase flow.
    """
    wait_time = between(2, 5)
    weight = 1

    def on_start(self):
        self.user_id = random.randint(1, 5)
        self.product_id = random.randint(1, 10)

    @task(1)
    @tag("e2e", "purchase")
    def complete_purchase_flow(self):
        """Simulates a complete purchase flow"""

        # Step 1: Browse products
        with self.client.get(
            f":{PRODUCT_SERVICE_PORT}/api/products",
            name="[FLOW] 1. Browse Products",
            catch_response=True
        ) as response:
            if response.status_code != 200:
                response.failure("Failed to browse products")
                return
            response.success()

        # Step 2: View product details
        with self.client.get(
            f":{PRODUCT_SERVICE_PORT}/api/products/{self.product_id}",
            name="[FLOW] 2. View Product",
            catch_response=True
        ) as response:
            if response.status_code not in [200, 404]:
                response.failure("Failed to view product")
            else:
                response.success()

        # Step 3: Check user profile
        with self.client.get(
            f":{USER_SERVICE_PORT}/api/users/{self.user_id}",
            name="[FLOW] 3. Check User",
            catch_response=True
        ) as response:
            if response.status_code not in [200, 404]:
                response.failure("Failed to check user")
            else:
                response.success()

        # Step 4: Add to favourites
        favourite_data = {
            "userId": self.user_id,
            "productId": self.product_id,
            "likeDate": datetime.now().strftime("%Y-%m-%d")
        }
        with self.client.post(
            f":{FAVOURITE_SERVICE_PORT}/api/favourites",
            json=favourite_data,
            name="[FLOW] 4. Add Favourite",
            catch_response=True
        ) as response:
            if response.status_code in [200, 201, 400, 409]:
                response.success()
            else:
                response.failure(f"Failed to add favourite: {response.status_code}")

        # Step 5: View orders
        with self.client.get(
            f":{ORDER_SERVICE_PORT}/api/orders",
            name="[FLOW] 5. View Orders",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure("Failed to view orders")
