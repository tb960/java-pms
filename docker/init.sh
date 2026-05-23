#!/bin/bash

# Make the docker-setup.sh script executable
chmod +x "$(dirname "$0")/docker-setup.sh"

echo "Docker setup complete!"
echo ""
echo "To get started:"
echo "1. Install Docker Desktop: https://www.docker.com/products/docker-desktop"
echo "2. Run: make dev-up"
echo "3. Check status: make test"
echo ""
echo "For more info, see: DOCKER.md"
