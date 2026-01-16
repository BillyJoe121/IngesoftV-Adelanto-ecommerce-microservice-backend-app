# Performance Test Results

## Test Configuration

| Parameter       | Value              |
|-----------------|-------------------|
| Date            | [To be filled after execution] |
| Duration        | 5 minutes         |
| Users           | 50 concurrent     |
| Spawn Rate      | 10 users/second   |
| Host            | http://localhost  |

## Test Scenarios Executed

1. **User Service Load Test** (Weight: 2)
2. **Product Service Load Test** (Weight: 3)
3. **Order Service Load Test** (Weight: 2)
4. **Mixed Workload - Purchase Flow** (Weight: 1)

## Summary Results

> **Note:** Execute tests and fill in the actual results below.

### Overall Statistics

| Metric                    | Value    |
|---------------------------|----------|
| Total Requests            | -        |
| Total Failures            | -        |
| Error Rate                | -        |
| Avg Response Time         | -        |
| Requests/sec (RPS)        | -        |

### Response Times by Endpoint

| Endpoint                  | Avg (ms) | Median | 95th % | 99th % | RPS   |
|---------------------------|----------|--------|--------|--------|-------|
| /api/users [GET]          | -        | -      | -      | -      | -     |
| /api/users/{id} [GET]     | -        | -      | -      | -      | -     |
| /api/users [POST]         | -        | -      | -      | -      | -     |
| /api/products [GET]       | -        | -      | -      | -      | -     |
| /api/products/{id} [GET]  | -        | -      | -      | -      | -     |
| /api/categories [GET]     | -        | -      | -      | -      | -     |
| /api/orders [GET]         | -        | -      | -      | -      | -     |
| /api/orders [POST]        | -        | -      | -      | -      | -     |

### Service Performance Comparison

| Service          | Avg Response | Error Rate | RPS   |
|------------------|--------------|------------|-------|
| user-service     | -            | -          | -     |
| product-service  | -            | -          | -     |
| order-service    | -            | -          | -     |
| payment-service  | -            | -          | -     |
| favourite-service| -            | -          | -     |

## Performance Goals

| Metric              | Target    | Actual | Status |
|---------------------|-----------|--------|--------|
| Avg Response Time   | < 500ms   | -      | -      |
| 95th Percentile     | < 1000ms  | -      | -      |
| Error Rate          | < 1%      | -      | -      |
| RPS (50 users)      | > 100     | -      | -      |

## Bottlenecks Identified

1. [To be identified after testing]
2. [To be identified after testing]

## Recommendations

1. [To be added based on results]
2. [To be added based on results]

## How to Run These Tests

```bash
# Navigate to performance tests directory
cd tests/performance

# Install dependencies
pip install -r requirements.txt

# Run with Web UI
locust -f locustfile.py --host=http://localhost

# Or run headless for CI/CD
locust -f locustfile.py --host=http://localhost \
    --users 50 \
    --spawn-rate 10 \
    --run-time 5m \
    --headless \
    --csv=results/report
```

## Appendix: Test Execution Commands

### Quick Smoke Test (1 minute, 10 users)
```bash
locust -f locustfile.py --host=http://localhost \
    --users 10 --spawn-rate 5 --run-time 1m --headless
```

### Standard Load Test (5 minutes, 50 users)
```bash
locust -f locustfile.py --host=http://localhost \
    --users 50 --spawn-rate 10 --run-time 5m --headless --csv=results/standard
```

### Stress Test (10 minutes, 100 users)
```bash
locust -f locustfile.py --host=http://localhost \
    --users 100 --spawn-rate 20 --run-time 10m --headless --csv=results/stress
```
