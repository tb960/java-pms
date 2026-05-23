.PHONY: help build up down logs clean restart test shell

help:
	@echo "Optical Shop ERP - Docker Commands"
	@echo "===================================="
	@echo "make build          - Build Docker image"
	@echo "make up             - Start Docker containers"
	@echo "make down           - Stop Docker containers"
	@echo "make restart        - Restart containers"
	@echo "make logs           - View application logs"
	@echo "make logs-tail      - Tail application logs (-f follow)"
	@echo "make clean          - Remove containers and volumes"
	@echo "make shell          - Access container shell"
	@echo "make test           - Test the application endpoint"
	@echo "make status         - Show container status"
	@echo ""

build:
	@echo "Building Docker image..."
	docker-compose build --no-cache

up:
	@echo "Starting Docker containers..."
	docker-compose up -d
	@echo ""
	@echo "✅ Application started!"
	@echo "📱 API: http://localhost:8080/api"
	@echo "🏥 Health Check: http://localhost:8080/api/health"
	@echo ""
	@echo "Waiting for application to be ready..."
	@sleep 5
	@curl -s http://localhost:8080/api/health | grep -q "UP" && echo "✅ Application is healthy!" || echo "⏳ Still starting..."

down:
	@echo "Stopping Docker containers..."
	docker-compose down

restart:
	@echo "Restarting containers..."
	docker-compose restart

logs:
	docker-compose logs optical-shop-app

logs-tail:
	docker-compose logs -f optical-shop-app

logs-mysql:
	docker-compose logs mysql-db

clean:
	@echo "Removing containers and volumes..."
	docker-compose down -v
	@echo "Cleaning up Docker resources..."
	docker system prune -f

shell:
	docker-compose exec optical-shop-app /bin/bash

test:
	@echo "Testing application health..."
	@curl -X GET http://localhost:8080/api/health | jq .

test-create-tenant:
	@echo "Creating test tenant..."
	@curl -X POST http://localhost:8080/api/tenants \
	  -H "Content-Type: application/json" \
	  -d '{"tenantCode":"TEST_001","tenantName":"Test Optical Shop"}' | jq .

status:
	docker-compose ps

rebuild:
	docker-compose down
	docker-compose build --no-cache
	docker-compose up -d

# Development shortcuts
dev-up: build up
	@echo "Development environment is ready!"

prod-build:
	@echo "Building production image..."
	docker build -t optical-shop-erp:prod --build-arg PROFILE=prod .

# View resource usage
stats:
	docker stats optical-shop-erp
