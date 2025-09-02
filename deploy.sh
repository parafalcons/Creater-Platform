#!/bin/bash

echo "🚀 Starting Blog Platform Deployment..."

# Build the application
echo "📦 Building application..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build successful!"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose down

# Build and start containers
echo "🐳 Building and starting containers..."
docker-compose up --build -d

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 30

# Check if application is running
if curl -f http://localhost:8080/api/auth/exists?userName=test > /dev/null 2>&1; then
    echo "✅ Application is running successfully!"
    echo "🌐 API is available at: http://localhost:8080/api"
    echo "📚 API Documentation: http://localhost:8080/api/swagger-ui.html (if enabled)"
else
    echo "❌ Application failed to start properly."
    echo "📋 Checking logs..."
    docker-compose logs app
    exit 1
fi

echo "🎉 Deployment completed successfully!"



