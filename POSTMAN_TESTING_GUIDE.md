# Postman Testing Guide for JWT Authentication Microservices

## Setup Instructions

### 1. Create a New Collection
- Open Postman
- Click **Collections** → **Create New** → **Collection**
- Name it: `Microservices API`
- Click **Create**

---

## Environment Variables Setup

### 2. Create an Environment
- Click **Environments** → **Create New Environment**
- Name it: `Local Development`
- Add these variables:

| Variable | Initial Value | Current Value |
|----------|---|---|
| `base_url` | `http://localhost:8080` | `http://localhost:8080` |
| `auth_token` | `` | `` |
| `user_id` | `` | `` |
| `username` | `admin` | `admin` |
| `password` | `testpassword123` | `testpassword123` |

- Click **Save**

---

## Test Cases

### Test 1: Login (Get JWT Token)

**Endpoint Details:**
- **Method:** `POST`
- **URL:** `{{base_url}}/api/auth/login`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "username": "{{username}}",
    "password": "{{password}}"
  }
  ```

**Pre-request Script:**
```javascript
// No pre-request needed for login
```

**Tests (Post-request Script):**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("token");
});

pm.test("Response has expiresIn", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("expiresIn");
});

// Save token to environment variable
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("auth_token", jsonData.token);
    console.log("✓ Token saved to environment");
}
```

**Expected Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGVzIjpbImFkbWluIl0sImlhdCI6MTc2NDMzODQxOSwiZXhwIjoxNzY0MzM5MzE5fQ.RkP_003pR4e13HMUwmruIAnS-52FH5aAiUhUgW_Haxk",
    "expiresIn": 900
}
```

---

### Test 2: Access Protected Endpoint (Orders)

**Endpoint Details:**
- **Method:** `GET`
- **URL:** `{{base_url}}/api/orders`
- **Headers:**
  ```
  Authorization: Bearer {{auth_token}}
  Content-Type: application/json
  ```
- **Body:** None

**Pre-request Script:**
```javascript
// Verify token exists
if (!pm.environment.get("auth_token")) {
    console.log("⚠ Warning: No auth_token found. Run Login test first!");
}
```

**Tests (Post-request Script):**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains _links (HATEOAS)", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("_links");
});

pm.test("Can access protected resource with valid token", function () {
    pm.expect(pm.response.code).to.equal(200);
});
```

**Expected Response:**
```json
{
    "_links": {
        "self": {
            "href": "http://order-service:8081/api/orders"
        }
    }
}
```

---

### Test 3: Verify JWT Contains User Info (Headers Check)

**Endpoint Details:**
- **Method:** `GET`
- **URL:** `{{base_url}}/api/orders`
- **Headers:**
  ```
  Authorization: Bearer {{auth_token}}
  ```

**Tests (Post-request Script):**
```javascript
pm.test("Gateway forwards user context headers", function () {
    // Note: Response headers won't show the X-User-* headers that were added,
    // but the service receives them. This test verifies the request succeeds
    // which means the gateway processed the token correctly.
    pm.expect(pm.response.code).to.equal(200);
});

pm.test("Decode and verify JWT token structure", function () {
    var token = pm.environment.get("auth_token");
    var parts = token.split('.');
    
    // Decode payload (second part)
    var payload = JSON.parse(atob(parts[1]));
    
    pm.expect(payload).to.have.property("sub");
    pm.expect(payload).to.have.property("username");
    pm.expect(payload).to.have.property("roles");
    pm.expect(payload.username).to.equal("admin");
});
```

---

### Test 4: Invalid Token (Should Fail)

**Endpoint Details:**
- **Method:** `GET`
- **URL:** `{{base_url}}/api/orders`
- **Headers:**
  ```
  Authorization: Bearer invalid.fake.token
  Content-Type: application/json
  ```

**Tests (Post-request Script):**
```javascript
pm.test("Status code is 401 for invalid token", function () {
    pm.response.to.have.status(401);
});

pm.test("Request is rejected (no 200 status)", function () {
    pm.expect(pm.response.code).to.not.equal(200);
});
```

**Expected Response:**
- Status: `401 Unauthorized`

---

### Test 5: Missing Token (Should Fail)

**Endpoint Details:**
- **Method:** `GET`
- **URL:** `{{base_url}}/api/orders`
- **Headers:** (No Authorization header)
  ```
  Content-Type: application/json
  ```

**Tests (Post-request Script):**
```javascript
pm.test("Status code is 401 when token missing", function () {
    pm.response.to.have.status(401);
});

pm.test("Cannot access without token", function () {
    pm.expect(pm.response.code).to.not.equal(200);
});
```

---

### Test 6: Create Order (POST with Authentication)

**Endpoint Details:**
- **Method:** `POST`
- **URL:** `{{base_url}}/api/orders`
- **Headers:**
  ```
  Authorization: Bearer {{auth_token}}
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "customerId": 1,
    "items": [
      {
        "inventoryId": 1,
        "quantity": 5,
        "price": 29.99
      }
    ],
    "totalAmount": 149.95
  }
  ```

**Tests (Post-request Script):**
```javascript
pm.test("Status code is 201 (Created)", function () {
    pm.response.to.have.status(201);
});

pm.test("Response contains order ID", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("id");
});

pm.test("Order was created with correct total", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.totalAmount).to.equal(149.95);
});
```

---

### Test 7: Access Different Microservice (Customers)

**Endpoint Details:**
- **Method:** `GET`
- **URL:** `{{base_url}}/api/customers`
- **Headers:**
  ```
  Authorization: Bearer {{auth_token}}
  ```

**Tests (Post-request Script):**
```javascript
pm.test("Can access Customer Service with same token", function () {
    pm.expect(pm.response.code).to.equal(200);
});

pm.test("Different service also receives request", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("_links");
});
```

---

### Test 8: Test Token Expiration (Advanced)

**Note:** This test requires waiting or manipulating time. Current token expires in 15 minutes.

**Endpoint Details:**
- **Method:** `GET`
- **URL:** `{{base_url}}/api/orders`
- **Headers:**
  ```
  Authorization: Bearer {{auth_token}}
  ```

**Pre-request Script:**
```javascript
// This demonstrates how you could test token refresh (future enhancement)
var token = pm.environment.get("auth_token");
console.log("Current token expiration: 15 minutes from login");
```

**Tests (Post-request Script):**
```javascript
pm.test("Token is still valid (within 15 min window)", function () {
    // Token was just generated, should be valid
    pm.expect(pm.response.code).to.equal(200);
});
```

---

## Test Execution Order

**Recommended order for complete testing:**

1. **Test 1: Login** ← Run this FIRST to get a token
2. **Test 2: Access Protected Endpoint**
3. **Test 3: Verify JWT Structure**
4. **Test 6: Create Order**
5. **Test 7: Access Different Service**
6. **Test 4: Invalid Token**
7. **Test 5: Missing Token**

---

## Running Tests in Postman

### Option A: Run Individual Test
1. Select a request
2. Click **Send**
3. Check the **Tests** tab for results

### Option B: Run Entire Collection
1. Click the collection name
2. Click **Run**
3. Select **Local Development** environment
4. Click **Run Microservices API**
5. Watch all tests execute in sequence

---

## Troubleshooting

### "No auth_token found" Warning
- **Cause:** Haven't run the Login test yet
- **Solution:** Run Test 1 (Login) first to get a token

### 401 Unauthorized on Protected Endpoints
- **Cause 1:** Token not in Authorization header
- **Cause 2:** Token expired (15 minutes)
- **Cause 3:** Wrong Bearer format
- **Solution:** Check Tests 4 & 5 to understand the format

### Connection Refused
- **Cause:** Services not running
- **Solution:** Run `docker-compose up -d`

### Token Doesn't Update
- **Cause:** Post-request script didn't run
- **Solution:** Make sure you're in the **Tests** tab and it shows green checkmarks

---

## Quick Reference: API Endpoints

| Service | Method | Endpoint | Requires Auth |
|---------|--------|----------|---------------|
| Auth | POST | `/api/auth/login` | ❌ No |
| Auth | POST | `/api/auth/hash` | ❌ No (temp endpoint) |
| Orders | GET | `/api/orders` | ✅ Yes |
| Orders | POST | `/api/orders` | ✅ Yes |
| Customers | GET | `/api/customers` | ✅ Yes |
| Inventory | GET | `/api/inventory` | ✅ Yes |
| Payments | GET | `/api/payments` | ✅ Yes |
| Shipping | GET | `/api/shipping` | ✅ Yes |
| Notifications | GET | `/api/notifications` | ✅ Yes |

---

## Sample Test Workflow in Postman

```
1. Login
   ├─ POST /api/auth/login
   └─ ✓ Get token (saved to environment)

2. Use Token
   ├─ GET /api/orders (with token)
   ├─ POST /api/orders (with token)
   └─ ✓ All work

3. Test Security
   ├─ GET /api/orders (without token) → 401
   ├─ GET /api/orders (invalid token) → 401
   └─ ✓ Security verified

4. Multi-Service
   ├─ GET /api/customers (with token) → works
   ├─ GET /api/inventory (with token) → works
   └─ ✓ All services accessible
```

---

## Notes

- **Token Validity:** 15 minutes (900 seconds)
- **Secret Key:** `my-super-secret-key-for-jwt-tokens`
- **Algorithm:** HS256 (HMAC with SHA256)
- **All services:** Behind API Gateway (port 8080)
- **Direct Auth Service:** Port 8087 (for testing)

Enjoy testing! 🚀
