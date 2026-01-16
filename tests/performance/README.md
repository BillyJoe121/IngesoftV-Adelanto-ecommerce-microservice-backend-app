# Performance Tests with Locust

## Overview
This directory contains performance tests for the e-commerce microservices using [Locust](https://locust.io/).

## Test Scenarios

### Scenario 1: User Service Load Test
- GET /api/users - List all users
- GET /api/users/{id} - Get specific user
- POST /api/users - Create new user

### Scenario 2: Product Service Load Test
- GET /api/products - List all products
- GET /api/products/{id} - Get specific product
- GET /api/categories - List all categories
- GET /api/categories/{id} - Get specific category

### Scenario 3: Order Service Load Test
- GET /api/orders - List all orders
- GET /api/orders/{id} - Get specific order
- GET /api/carts - List all carts
- POST /api/orders - Create new order

### Scenario 4: Mixed Workload (Complete Purchase Flow)
Simulates realistic user behavior:
1. Browse products
2. View product details
3. Check user profile
4. Add to favourites
5. View orders

## Installation

```bash
pip install -r requirements.txt
```

## Running Tests

### Web UI Mode (Recommended for development)
```bash
locust -f locustfile.py --host=http://localhost
```
Then open http://localhost:8089 in your browser.

### Headless Mode (For CI/CD)
```bash
locust -f locustfile.py --host=http://localhost \
    --users 50 \
    --spawn-rate 10 \
    --run-time 5m \
    --headless \
    --csv=results/report
```

### Running Specific Scenarios
```bash
# Only user service tests
locust -f locustfile.py --host=http://localhost --tags user

# Only product service tests
locust -f locustfile.py --host=http://localhost --tags product

# Only order service tests
locust -f locustfile.py --host=http://localhost --tags order

# End-to-end flow
locust -f locustfile.py --host=http://localhost --tags e2e
```

## Configuration

### Environment Variables
- `USER_SERVICE_PORT` - Default: 8700
- `PRODUCT_SERVICE_PORT` - Default: 8500
- `ORDER_SERVICE_PORT` - Default: 8300
- `PAYMENT_SERVICE_PORT` - Default: 8400
- `FAVOURITE_SERVICE_PORT` - Default: 8800

### Test Parameters
- **Users**: Number of concurrent users
- **Spawn Rate**: Users spawned per second
- **Run Time**: Total test duration

## Recommended Test Configuration

| Environment | Users | Spawn Rate | Duration |
|-------------|-------|------------|----------|
| Dev         | 10    | 2/s        | 2m       |
| Stage       | 50    | 10/s       | 5m       |
| Pre-Prod    | 100   | 20/s       | 10m      |

## Output

Results are saved in CSV format:
- `results/report_stats.csv` - Request statistics
- `results/report_failures.csv` - Failed requests
- `results/report_stats_history.csv` - Stats over time

## Metrics to Monitor

1. **Response Time**
   - Median (50th percentile)
   - 95th percentile
   - 99th percentile

2. **Throughput**
   - Requests per second (RPS)

3. **Error Rate**
   - Percentage of failed requests

4. **Concurrent Users**
   - Active users during test
