# Docker Setup Guide for Optical Shop ERP

## Prerequisites

You only need to install **Docker Desktop** on your Mac:
- Download from: https://www.docker.com/products/docker-desktop
- No need to install Java, Maven, or any other tools globally!

## Quick Start

### **Option 1: Using Makefile (Easiest)**

```bash
cd /Users/bkblyde/Documents/proj/java-pms

# Build and start everything
make dev-up

# View logs
make logs-tail

# Test the application
make test

# Stop everything
make down
```

### **Option 2: Using docker-compose directly**

```bash
# Build the image
docker-compose build

# Start the application
docker-compose up -d

# View logs
docker-compose logs -f optical-shop-app

# Stop the application
docker-compose down
```

### **Option 3: Using the setup script**

```bash
chmod +x docker-setup.sh
./docker-setup.sh
```

## What Happens During Docker Build

```
1. Downloads Maven 3.9.4 + JDK 17 image
2. Copies pom.xml
3. Downloads all Maven dependencies (cached for faster rebuilds)
4. Copies source code
5. Runs: mvn clean package (builds your app)
6. Creates second lightweight image with just the JAR
7. Starts Java application on port 8080
```

## All Makefile Commands

```bash
make help              # Show all commands
make build             # Build Docker image
make up                # Start containers
make down              # Stop containers
make restart           # Restart containers
make logs              # View app logs
make logs-tail         # Follow logs in real-time
make logs-mysql        # View MySQL logs (if enabled)
make clean             # Remove containers and volumes
make shell             # Access container bash
make test              # Test health endpoint
make test-create-tenant # Create a test tenant
make status            # Show container status
make dev-up            # Build and start everything
make rebuild           # Clean rebuild everything
make stats             # Show Docker resource usage
```

## Using MySQL Instead of H2

By default, the app uses **H2 in-memory database** (fastest for dev).

To use **MySQL**:

1. Uncomment MySQL section in `docker-compose.yml`
2. Update `application.yml` to use MySQL datasource
3. Run:

```bash
docker-compose down
docker-compose up -d
```

## Accessing the Application

### Health Check
```bash
curl http://localhost:8080/api/health
```

### Create Tenant
```bash
curl -X POST http://localhost:8080/api/tenants \
  -H "Content-Type: application/json" \
  -d '{"tenantCode":"OPTICAL_001","tenantName":"My Shop"}'
```

### View Logs
```bash
# Real-time logs
make logs-tail

# Or using docker-compose
docker-compose logs -f optical-shop-app
```

### Access Container Shell
```bash
make shell
# Now you're inside the container
ls -la
java -version
exit
```

## Docker Image Details

- **Base Image**: `eclipse-temurin:17-jdk-jammy` (minimal JDK)
- **Size**: ~500MB (optimized with multi-stage build)
- **User**: Non-root user (appuser) for security
- **Health Check**: Built-in `/api/health` endpoint check
- **Port**: 8080

## Stopping and Cleaning Up

```bash
# Just stop (containers remain)
docker-compose down

# Stop and remove volumes (complete cleanup)
docker-compose down -v

# Remove everything including images
make clean

# Then rebuild
make build
```

## Troubleshooting

### Port 8080 already in use
```bash
# Change port in docker-compose.yml
# Or kill the process:
lsof -i :8080
kill -9 <PID>
```

### Container won't start
```bash
# Check logs
docker-compose logs optical-shop-app

# Rebuild
docker-compose down -v
docker-compose build --no-cache
docker-compose up
```

### Need to rebuild after code changes
```bash
# If you modified Java code:
docker-compose down
docker-compose build --no-cache
docker-compose up -d

# Or use shortcut:
make rebuild
```

### Check Docker is running
```bash
docker ps
# Should show running containers
```

## Development Workflow

```
1. Make changes to Java code locally (in VSCode)
2. Rebuild Docker image:
   docker-compose down
   docker-compose build
   docker-compose up -d
3. View logs:
   make logs-tail
4. Test with curl/Postman
5. Repeat
```

## Production Deployment

For production, use:
```bash
docker build -t optical-shop-erp:prod .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://prod-db:3306/optical_shop \
  optical-shop-erp:prod
```

## Environment Variables

You can override settings via docker-compose.yml:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=dev
  - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/optical_shop_erp
  - SERVER_PORT=8080
```

## Benefits of Docker Approach

✅ No need to install Java on your Mac  
✅ No need to install Maven globally  
✅ No need to install MySQL locally  
✅ Consistent development environment  
✅ Easy to onboard new team members  
✅ Same environment in dev and production  
✅ Easy to run multiple projects  
✅ Scales to Kubernetes easily  
✅ CI/CD ready  

---

**Questions?** Check logs with `make logs-tail` or run `make help`
