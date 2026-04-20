#!/bin/bash
set -x

echo "=========================================="
echo "Fixing Keycloak startup issue"
echo "=========================================="

cd /home/sone/repos/Inera/tak

# Stop and remove old containers
echo "Removing old containers..."
docker rm -f keycloak keycloak_postgres 2>/dev/null || true
sleep 2

# Start Keycloak
echo "Starting Keycloak and PostgreSQL..."
docker compose -f kc26-docker-compose.yml up -d

# Wait for startup
sleep 15

# Check status
echo ""
echo "=========================================="
echo "Container Status:"
echo "=========================================="
docker compose -f kc26-docker-compose.yml ps

echo ""
echo "=========================================="
echo "Keycloak Logs (last 50 lines):"
echo "=========================================="
docker compose -f kc26-docker-compose.yml logs keycloak --tail=50

echo ""
echo "=========================================="
echo "Test Keycloak Health:"
echo "=========================================="
curl -s http://localhost:8080/health/ready | head -20 || echo "Cannot reach Keycloak yet"

echo ""
echo "=========================================="
echo "Done!"
echo "=========================================="

