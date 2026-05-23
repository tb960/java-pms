#!/bin/bash

# Optical Shop ERP - Docker Startup Script
# This script initializes the application in a Docker container

set -e  # Exit on error

echo "=================================="
echo "Optical Shop ERP - Docker Setup"
echo "=================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed!"
    echo "📥 Please install Docker from https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Check if Docker daemon is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker daemon is not running!"
    echo "🚀 Please start Docker Desktop"
    exit 1
fi

echo "✅ Docker is ready"

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "⚠️ docker-compose is not installed separately (using docker compose)"
fi

echo ""
echo "🏗️  Building Docker image..."
docker-compose build

echo ""
echo "🚀 Starting application..."
docker-compose up -d

echo ""
echo "⏳ Waiting for application to be ready..."
sleep 10

echo ""
echo "🔍 Checking application health..."
if curl -s http://localhost:8080/api/health | grep -q "UP"; then
    echo "✅ Application is healthy and ready!"
else
    echo "⏳ Application is starting, checking again..."
    sleep 5
fi

echo ""
echo "=================================="
echo "✅ Setup Complete!"
echo "=================================="
echo ""
echo "📱 API URL: http://localhost:8080/api"
echo "🏥 Health Check: http://localhost:8080/api/health"
echo ""
echo "📚 Available Commands:"
echo "  make up           - Start containers"
echo "  make down         - Stop containers"
echo "  make logs         - View logs"
echo "  make test         - Test health endpoint"
echo "  make clean        - Remove everything"
echo ""
echo "🎯 Next Steps:"
echo "1. Create a tenant:   make test-create-tenant"
echo "2. View logs:         make logs-tail"
echo "3. Access shell:      make shell"
echo ""
