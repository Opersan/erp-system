# ERP System

## Project Structure
- `com.erp.modules.auth`: Authentication & Authorization (User, Role, Permission, JWT)
- `com.erp.modules.procurement`: Procurement (Supplier, Item, PO)
- `com.erp.modules.inventory`: Inventory (Warehouse, Stock, Transactions)
- `com.erp.modules.manufacturing`: Manufacturing (BOM, Work Order)
- `com.erp.modules.mrp`: MRP (Requirements Planning)
- `com.erp.modules.common`: Shared utilities (Global Exception Handler, BaseEntity)

## Technology Stack
- Java 21
- Spring Boot 3.x
- PostgreSQL
- Spring Security + JWT
- Flyway Migration
- Docker Compose

## Local Run
1. Start Database:
   ```sh
   docker-compose up -d
   ```
2. Run Application:
   ```sh
   ./mvnw spring-boot:run
   ```
   The application will start on port 8080.
   Flyway will automatically migrate the database.

## API Documentation
Swagger UI: http://localhost:8080/swagger-ui/index.html

## Authentication Endpoints

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@erp.com",
    "password": "password123",
    "role": ["admin"]
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

### Refresh Token
```bash
curl -X POST http://localhost:8080/api/auth/refreshtoken \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "your-refresh-token-here"
  }'
```

## Procurement & Inventory Endpoints

### Create Purchase Order
```bash
curl -X POST http://localhost:8080/api/procurement/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": 1,
    "items": [
      { "itemId": 1, "quantity": 100, "price": 10.50 },
      { "itemId": 2, "quantity": 50, "price": 5.00 }
    ]
  }'
```

### Approve Purchase Order
```bash
curl -X POST http://localhost:8080/api/procurement/orders/1/approve \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

### Receive Goods (Inventory)
```bash
curl -X POST http://localhost:8080/api/inventory/receipts \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "purchaseOrderId": 1,
    "warehouseId": 1,
    "items": [
      { "itemId": 1, "quantity": 50 },
      { "itemId": 2, "quantity": 50 }
    ]
  }'
```

## Manufacturing Endpoints

### Create Work Order
```bash
curl -X POST http://localhost:8080/api/manufacturing/work-orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": 3,
    "quantity": 10,
    "startDate": "2023-12-20T09:00:00",
    "endDate": "2023-12-25T17:00:00"
  }'
```
*Note: Item ID 3 is "Steel Box" (Finished Good) in initial data.*

### Update Work Order Status
```bash
curl -X PUT "http://localhost:8080/api/manufacturing/work-orders/1/status?status=RELEASED" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

## MRP Endpoints

### Run MRP
```bash
curl -X POST "http://localhost:8080/api/mrp/run?horizonDays=30" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

### Get Planned Orders
```bash
curl -X GET http://localhost:8080/api/mrp/runs/1/planned-orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

### Convert Planned Orders to PO
```bash
curl -X POST http://localhost:8080/api/mrp/convert-to-po \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '[1, 2]'
```

## Deployment (Heroku)
1. Create Heroku app.
2. Add Heroku Postgres addon.
3. Set Config Vars:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `ERP_APP_JWTSECRET=...`
   - `ERP_APP_JWTEXPIRATIONMS=...`
4. Deploy code.

## Design Notes
- **Multi-tenancy**: Currently single-tenant. For multi-tenancy, we can add `tenant_id` to all entities (via `BaseEntity` or separate aspect) and filter in Hibernate using `@Filter` or separate schemas per tenant.
- **Idempotency**: Critical endpoints like PO creation and Inventory updates should implement idempotency keys (header `Idempotency-Key`) to prevent duplicate processing on retries.
