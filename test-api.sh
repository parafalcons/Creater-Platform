#!/bin/bash

echo "🔍 Testing Blog Platform API endpoints..."
echo "=========================================="

BASE_URL="http://localhost:8080/api"

echo ""
echo "1. Testing basic connectivity..."
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" "$BASE_URL/auth/test"

echo ""
echo "2. Testing signup endpoint..."
curl -X POST "$BASE_URL/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }' \
  -w "\nHTTP Status: %{http_code}\n"

echo ""
echo "3. Testing login endpoint..."
curl -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "password123"
  }' \
  -w "\nHTTP Status: %{http_code}\n"

echo ""
echo "4. Testing user exists endpoint..."
curl -s "$BASE_URL/auth/exists?userName=testuser" \
  -w "\nHTTP Status: %{http_code}\n"

echo ""
echo "5. Testing protected endpoint (should fail without token)..."
curl -s "$BASE_URL/blog-posts" \
  -w "\nHTTP Status: %{http_code}\n"

echo ""
echo "=========================================="
echo "Test completed!"
