# Docker Setup Guide

## Prerequisites

- **Java 11** (Eclipse Temurin/Adoptium recommended)
- **Maven 3.6+**
- **Docker Desktop** with Docker Compose v2

## Quick Start

### 1. Build the project with Maven

```bash
mvn clean package -DskipTests
```

### 2. Start all services with Docker Compose

```bash
docker-compose -f compose-local.yml up --build
```

### 3. Verify services are running

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Zipkin (Distributed Tracing)**: http://localhost:9411

## Services Architecture

| Service | Port | Description |
|---------|------|-------------|
| service-discovery | 8761 | Eureka Server for service registration |
| cloud-config | 9296 | Centralized configuration server |
| api-gateway | 8080 | API Gateway (entry point) |
| user-service | 8700 | User management |
| product-service | 8500 | Product catalog |
| order-service | 8300 | Order processing |
| payment-service | 8400 | Payment processing |
| shipping-service | 8600 | Shipping management |
| favourite-service | 8800 | User favorites |
| proxy-client | 8900 | Proxy client service |
| zipkin | 9411 | Distributed tracing |

## Configuration

### Spring Profiles

The application uses the `docker` profile when running in containers. Each service has an `application-docker.yml` file with Docker-specific settings:

- Container hostnames instead of `localhost`
- H2 in-memory database for development
- Eureka client configuration pointing to `service-discovery-container:8761`

### Docker Compose Files

- `compose-local.yml` - Builds images locally from source code
- `compose.yml` - Uses pre-built images from Docker Hub (original)

## Troubleshooting

### Services not registering with Eureka

1. Ensure `service-discovery-container` is healthy before other services start
2. Check logs: `docker logs <container-name>`
3. Verify network connectivity: all services must be on `ecommerce-network`

### Build failures

1. Ensure Java 11 is set as JAVA_HOME
2. Run `mvn clean package -DskipTests` before Docker build
3. Check that JAR files exist in each service's `target/` directory

### Port conflicts

Stop any local services using the same ports, or modify the port mappings in `compose-local.yml`.

## Stopping Services

```bash
docker-compose -f compose-local.yml down
```

To remove volumes and networks:

```bash
docker-compose -f compose-local.yml down -v --remove-orphans
```
