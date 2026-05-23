# 🐳 Docker Architecture & Workflow

## How Docker Works for This Project

```
Your Mac (macOS)
┌─────────────────────────────────────────────┐
│  VSCode / Terminal                          │
│  (Edit code, run make commands)             │
│                                              │
│  ┌──────────────────────────────────────┐  │
│  │   Docker Desktop (virtualization)    │  │
│  │                                       │  │
│  │  ┌──────────────────────────────┐   │  │
│  │  │   Docker Container           │   │  │
│  │  │  ┌──────────────────────┐    │   │  │
│  │  │  │  JDK 17              │    │   │  │
│  │  │  │  Maven 3.9.4         │    │   │  │
│  │  │  │  Spring Boot App     │    │   │  │
│  │  │  │  H2 Database (RAM)   │    │   │  │
│  │  │  └──────────────────────┘    │   │  │
│  │  │  Port 8080                   │   │  │
│  │  └──────────────────────────────┘   │  │
│  │                                       │  │
│  └──────────────────────────────────────┘  │
│                                              │
│  Accessible from Mac:                       │
│  → http://localhost:8080/api                │
└─────────────────────────────────────────────┘
```

## Build Process

```
Step 1: Docker Build
─────────────────────────────────────────────

Your Mac                           Docker Container
┌─────────────────┐          ┌──────────────────────┐
│ pom.xml         │  ──→     │ Maven Downloads      │
│ src/            │  ──→     │ Dependencies (.m2)   │
│                 │  ──→     │ Compiles Source Code │
│                 │  ──→     │ Runs Tests           │
│                 │  ──→     │ Packages into JAR    │
└─────────────────┘          └──────────────────────┘
                                         ↓
                             optical-shop-erp-1.0.0.jar
                                         ↓
                        ┌──────────────────────────────┐
                        │ Create Lightweight Image     │
                        │ - Copy JAR only              │
                        │ - Add JDK 17 (base image)    │
                        │ - ~500MB total               │
                        └──────────────────────────────┘


Step 2: Docker Run
─────────────────────────────────────────────

   docker-compose up -d
            ↓
   ┌──────────────────────┐
   │ Start Container      │
   │ - Boot Java App      │
   │ - Listen on 8080     │
   │ - DataInitializer    │
   │   creates admin user │
   │ - Ready for requests │
   └──────────────────────┘
            ↓
   Application running in container
   Accessible from Mac via localhost:8080
```

## File Structure

```
java-pms/
├── Dockerfile                ← Container build instructions
├── docker-compose.yml        ← Multi-container orchestration
├── .dockerignore             ← Exclude files from build
├── Makefile                  ← Convenient commands
├── docker-setup.sh           ← Automated setup script
├── DOCKER.md                 ← This guide
│
├── pom.xml                   ← Maven dependencies
├── src/
│   ├── main/
│   │   ├── java/com/opticalshop/erp/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── bootstrap/      ← Database seeding
│   │   │   └── security/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-dev.yml
│   └── test/
│
└── target/                   ← Build output (created by Docker)
    └── optical-shop-erp-1.0.0.jar
```

## Command Workflow

```
Initial Setup
─────────────────────────────────────────────
make dev-up
  └─ Builds image (downloads Java, Maven, dependencies)
  └─ Starts container
  └─ Runs application
  └─ App is ready on port 8080


During Development
─────────────────────────────────────────────
Edit code in VSCode
  ↓
make rebuild
  └─ Stops container
  └─ Rebuilds image
  └─ Starts with latest code
  ↓
Test with curl/Postman
  ↓
make logs-tail
  └─ Follow logs in real-time


When Done
─────────────────────────────────────────────
make down
  └─ Stops container
  └─ Data preserved (can restart)

make clean
  └─ Removes everything
  └─ Next time: fresh rebuild
```

## Key Benefits

| Aspect | Docker | Local Setup |
|--------|--------|-------------|
| **Installation** | Just Docker | Java, Maven, MySQL, etc. |
| **Space** | ~2GB per project | +1GB globally |
| **Consistency** | Same everywhere | Varies by machine |
| **Onboarding** | 1 command | 10+ steps |
| **Clean** | Remove container | Clean up globally |
| **Isolation** | Complete | Shared environment |

## Docker vs Local - Side by Side

```
┌──────────────────────┬──────────────────┐
│   Docker Approach    │   Local Approach │
├──────────────────────┼──────────────────┤
│ make dev-up          │ brew install ... │
│                      │ mvn clean ...    │
│                      │ mvn spring-boot  │
│                      │ (much longer)    │
├──────────────────────┼──────────────────┤
│ make logs-tail       │ Console output   │
├──────────────────────┼──────────────────┤
│ make test            │ curl manually    │
├──────────────────────┼──────────────────┤
│ make down            │ Ctrl+C, cleanup  │
├──────────────────────┼──────────────────┤
│ make clean           │ rm -rf target    │
│                      │ brew uninstall   │
└──────────────────────┴──────────────────┘
```

## Advanced Docker Concepts

### Multi-Stage Build

The Dockerfile uses a 2-stage build:

```
Stage 1: Builder
  ├─ Download Maven + JDK
  ├─ Build your application
  └─ Output: JAR file (large)
        ↓
Stage 2: Runtime
  ├─ Start fresh with JDK only
  ├─ Copy JAR from Stage 1
  ├─ Run application
  └─ Output: ~500MB image (slim)
```

This saves ~400MB compared to including Maven in final image!

### Container Networking

```
Host Machine (Mac)           Docker Network
┌────────────────────┐      ┌──────────────────┐
│ localhost:8080     │─────→│ Container:8080   │
│ (Host Port)        │      │ (Container Port) │
└────────────────────┘      └──────────────────┘
```

Port mapping: `8080:8080` means:
- First 8080 = Host (Mac) port
- Second 8080 = Container port

### Health Checks

The container has a built-in health check:

```
Every 30 seconds:
  Docker → curl http://localhost:8080/api/health
  
If UP: Container is healthy ✅
If DOWN: Container marked unhealthy ⚠️
If fails 3 times: Container can auto-restart
```

## Scaling

When ready for production:

```
Docker Compose (current)
  └─ Single application
  └─ Single machine
        ↓
Kubernetes / Docker Swarm
  ├─ Multiple containers
  ├─ Load balancing
  ├─ Auto-scaling
  ├─ Multiple machines
  └─ Same Docker images!
```

Your Dockerfile is already production-ready! 🎉

## Environment Variables

You can override settings without rebuilding:

```bash
# Use MySQL instead of H2
docker-compose up -d -e SPRING_PROFILES_ACTIVE=prod

# Change port
docker-compose up -d -e SERVER_PORT=9090

# Multiple variables
docker run -e VAR1=value1 -e VAR2=value2 image:tag
```

## Debugging in Docker

```bash
# Access container shell
make shell

# Inside container:
$ ls -la
$ java -version
$ cat /app/app.jar
$ ps aux | grep java

# Check environment
$ env | grep SPRING

# Exit container
$ exit
```

## CI/CD Ready

Your Docker setup is ready for:
- ✅ GitHub Actions
- ✅ GitLab CI
- ✅ Jenkins
- ✅ Docker Hub
- ✅ AWS ECR
- ✅ Kubernetes
- ✅ ECS/Fargate

Just push image to registry!

---

**Need help?** Run `make help` or check logs with `make logs-tail`
