# 🚀 Getting Started: Docker vs Local Setup

## TL;DR - Quick Answer

**Want the fastest, easiest way to run this app?**

```bash
# Install Docker Desktop from https://www.docker.com/products/docker-desktop
# Then:
chmod +x quick-start-docker.sh
./quick-start-docker.sh

# That's it! App runs in ~2-3 minutes
```

**OR if you already have Java/Maven installed locally:**

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

---

## Decision Matrix

Choose your path based on your situation:

### ✅ Use Docker If:
- [ ] You want zero setup on your Mac
- [ ] You're on a team (consistency)
- [ ] You might work on other projects
- [ ] You want clean isolation
- [ ] You plan to deploy to cloud/containers
- [ ] You want CI/CD ready
- [ ] Your Mac has limited disk space for tools
- [ ] You're not familiar with Maven/Java setup

### ✅ Use Local Setup If:
- [ ] You already have Java 17 installed
- [ ] You already have Maven installed
- [ ] You want fastest development loop (no Docker overhead)
- [ ] You need to debug Java directly with IDE
- [ ] You prefer traditional development environment
- [ ] You're experienced with Maven

---

## Path 1: Docker Setup (Recommended for Most)

### Requirements
- **Only**: Docker Desktop installed
- ~2 GB download on first run

### Time to First Run
- **First time**: 3-5 minutes (downloads, builds)
- **Subsequent**: 5-10 seconds

### Steps

1. **Install Docker Desktop**
   ```bash
   # Download from: https://www.docker.com/products/docker-desktop
   # Open and follow installation wizard
   # Verify:
   docker --version
   ```

2. **Navigate to project**
   ```bash
   cd /Users/bkblyde/Documents/proj/java-pms
   ```

3. **Run setup script**
   ```bash
   chmod +x quick-start-docker.sh
   ./quick-start-docker.sh
   
   # OR manually:
   docker-compose build
   docker-compose up -d
   ```

4. **Verify it's running**
   ```bash
   make test
   # Should see: {"status":"UP",...}
   ```

5. **View logs**
   ```bash
   make logs-tail
   ```

### Commands You'll Use

```bash
make dev-up        # Start everything
make down           # Stop everything
make logs-tail      # View logs
make test           # Test endpoint
make rebuild        # Rebuild after code changes
make shell          # Access container bash
make clean          # Remove everything
```

### Cleanup (if needed)

```bash
# Stop only
make down

# Remove everything
make clean

# Restart
docker-compose up -d
```

---

## Path 2: Local Setup (For Maven/Java Users)

### Requirements
```bash
# Check if you have these:
java -version        # Need Java 17+
mvn --version        # Need Maven 3.8.0+
```

### If You Don't Have Them

```bash
# Install Java 17
brew install openjdk@17
sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Install Maven
brew install maven

# Verify
java -version
mvn --version
```

### Time to First Run
- **First time**: 5-10 minutes (downloads dependencies)
- **Subsequent**: 10-15 seconds

### Steps

1. **Navigate to project**
   ```bash
   cd /Users/bkblyde/Documents/proj/java-pms
   ```

2. **Build**
   ```bash
   mvn clean install
   ```

3. **Run with H2 (easiest)**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
   ```

4. **Verify it's running**
   ```bash
   # In another terminal:
   curl http://localhost:8080/api/health
   ```

### Common Commands

```bash
mvn clean install              # Build
mvn spring-boot:run           # Run (MySQL)
mvn spring-boot:run \
  --spring.profiles.active=dev # Run with H2
mvn test                       # Run tests
mvn clean package              # Build JAR
```

### Cleanup

```bash
# Stop: Ctrl+C
# Clean: mvn clean
# Remove: rm -rf target/
```

---

## Path 3: Hybrid (Docker for DB, Local Java)

### Use case
- You have Java/Maven
- But don't want MySQL on your Mac
- Want Docker isolation for database only

```yaml
# docker-compose.yml - uncomment MySQL section
services:
  mysql-db:
    image: mysql:8.0.35
    # ... config ...

  # Don't define spring-boot-app - run locally instead
```

```bash
# Start just MySQL in Docker
docker-compose up -d mysql-db

# Run Java app locally
mvn spring-boot:run -Dspring-boot.run.arguments="--server.datasource.url=jdbc:mysql://localhost:3306/optical_shop_erp"
```

---

## Comparing Approaches

| Factor | Docker | Local | Hybrid |
|--------|--------|-------|--------|
| **Setup Time** | 3-5 min | 5-10 min | 2-3 min |
| **Disk Space** | 2-3 GB | 2-4 GB | 1-2 GB |
| **Run Speed** | 1-2 sec overhead | Direct | Mix |
| **Code Changes** | Rebuild needed | Restart only | Restart |
| **Team Consistency** | Perfect | Varies | Good |
| **Cloud Ready** | Yes | No | Partial |
| **Learning Curve** | Low | Requires Maven knowledge | Medium |

---

## Troubleshooting Decision

**"Which path should I take?"**

```
Do you have Java 17 installed?
├─ YES: Have Maven too?
│   ├─ YES: Use Local or Docker (either works)
│   └─ NO: Use Docker (easier than Maven install)
└─ NO: Use Docker (simpler than Java install)

Do you work on multiple projects?
├─ YES: Use Docker (isolation)
└─ NO: Local setup is fine

Will this go to production?
├─ YES: Start with Docker (cloud ready)
└─ NO: Either works

How much disk space do you have?
├─ <50 GB: Docker might be tight, use Local
└─ >50 GB: Docker is fine
```

---

## What Happens When You Start

### Docker Path

```
1. docker-compose up -d
   ├─ Starts container
   ├─ Runs OpticalShopErpApplication.main()
   ├─ DataInitializer creates default data
   ├─ App listens on :8080
   └─ Ready in ~10 seconds

2. Access: http://localhost:8080/api
```

### Local Path

```
1. mvn spring-boot:run
   ├─ Starts Spring Boot
   ├─ DataInitializer creates default data
   ├─ App listens on :8080
   └─ Ready in ~10 seconds

2. Access: http://localhost:8080/api
```

**Both are exactly the same from outside!**

---

## Quick Comparison Cheat Sheet

```
DOCKER:
✅ No global installations needed
✅ Clean per-project environment
✅ Matches production setup
✅ Easy team onboarding
⚠️ Small performance overhead
⚠️ Need 2-3GB disk per project

LOCAL:
✅ Direct IDE debugging
✅ No container overhead
✅ Faster code-change cycles
⚠️ Global tool installations
⚠️ Consistency varies per machine
⚠️ Harder onboarding for team
```

---

## Making Your Decision

**Answer these 3 questions:**

1. **How comfortable are you with Docker?**
   - Not used it: Docker (learn it!)
   - Used before: Docker (you know the power)
   - Prefer local dev: Local

2. **How long will you work on this?**
   - Just starting: Docker (safer investment)
   - Long-term: Docker (better for team)
   - Quick test: Local

3. **Will others work on this?**
   - Yes, team: Docker (consistency!)
   - Solo project: Either works
   - Personal learning: Local is fine

---

## After You Choose Your Path

### Docker Users
```bash
# You now use these commands:
make help              # See all commands
make dev-up           # Start everything
make logs-tail        # View logs
make test             # Test endpoint
```

### Local Users
```bash
# You now use these commands:
mvn spring-boot:run   # Start
Ctrl+C                # Stop
mvn clean             # Clean
```

### Both Users
```bash
# Testing is the same:
curl http://localhost:8080/api/health
curl -X POST http://localhost:8080/api/tenants \
  -H "Content-Type: application/json" \
  -d '{"tenantCode":"TEST_001","tenantName":"Test"}'
```

---

## FAQ

**Q: Can I switch later from Docker to Local?**
A: Yes! The code is the same. Just install Java/Maven.

**Q: Can I switch from Local to Docker?**
A: Yes! Just have Docker installed and run make commands.

**Q: What if Docker is slow on my Mac?**
A: Try allocating more resources in Docker Desktop → Settings → Resources

**Q: Can I use both simultaneously?**
A: No, they'll fight over port 8080. Stop one before starting the other.

**Q: Which will I be faster with?**
A: Docker is faster first time. Local might be faster in long development cycles.

---

## Ready? Choose Your Path

### 🐳 Go with Docker
```bash
chmod +x quick-start-docker.sh
./quick-start-docker.sh
```

### ☕ Go with Local Setup
```bash
brew install openjdk@17 maven
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### 📖 Need More Info?
- Docker Guide: [DOCKER.md](DOCKER.md)
- Architecture: [DOCKER_ARCHITECTURE.md](DOCKER_ARCHITECTURE.md)
- Main README: [README.md](README.md)

---

**You've got this! 🚀 Pick a path and start building!**
