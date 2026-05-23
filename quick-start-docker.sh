#!/bin/bash

# Quick Docker Setup for Optical Shop ERP
# This script automates the entire setup process

set -e

echo "╔════════════════════════════════════════════════════╗"
echo "║  Optical Shop ERP - Docker Quick Start Setup     ║"
echo "╚════════════════════════════════════════════════════╝"
echo ""

# Check prerequisites
check_docker() {
    if ! command -v docker &> /dev/null; then
        echo "❌ Docker is not installed!"
        echo ""
        echo "📦 Please install Docker Desktop:"
        echo "   Download from: https://www.docker.com/products/docker-desktop"
        echo ""
        exit 1
    fi
    echo "✅ Docker is installed"
}

check_docker_daemon() {
    if ! docker info > /dev/null 2>&1; then
        echo "❌ Docker daemon is not running!"
        echo ""
        echo "🚀 Please start Docker Desktop from Applications"
        echo ""
        exit 1
    fi
    echo "✅ Docker daemon is running"
}

# Main setup
main() {
    echo ""
    echo "📋 Checking prerequisites..."
    check_docker
    check_docker_daemon
    
    echo ""
    echo "🏗️  Building Docker image (this may take a few minutes)..."
    echo "   Downloading JDK 17, Maven, dependencies..."
    echo ""
    
    docker-compose build
    
    echo ""
    echo "✅ Image built successfully!"
    echo ""
    echo "🚀 Starting application..."
    docker-compose up -d
    
    echo ""
    echo "⏳ Waiting for application to start..."
    sleep 10
    
    echo ""
    echo "🔍 Checking application health..."
    for i in {1..5}; do
        if curl -s http://localhost:8080/api/health | grep -q "UP"; then
            echo "✅ Application is healthy!"
            break
        fi
        if [ $i -lt 5 ]; then
            echo "   Attempt $i/5 - Still starting..."
            sleep 2
        fi
    done
    
    echo ""
    echo "╔════════════════════════════════════════════════════╗"
    echo "║  ✅ Setup Complete!                               ║"
    echo "╚════════════════════════════════════════════════════╝"
    echo ""
    echo "📱 Access Application:"
    echo "   API Base URL: http://localhost:8080/api"
    echo "   Health Check: http://localhost:8080/api/health"
    echo ""
    echo "🛠️  Useful Commands:"
    echo "   View logs:       make logs-tail"
    echo "   Test endpoint:   make test"
    echo "   Access shell:    make shell"
    echo "   Stop app:        make down"
    echo "   All commands:    make help"
    echo ""
    echo "📚 Documentation:"
    echo "   Docker Guide:    DOCKER.md"
    echo "   Architecture:    DOCKER_ARCHITECTURE.md"
    echo ""
    echo "🎯 First Steps:"
    echo "   1. Test health:  make test"
    echo "   2. View logs:    make logs-tail"
    echo "   3. Try API:      curl http://localhost:8080/api/health"
    echo ""
}

# Run main function
main
