#!/bin/bash

# DPoP Security Test Script
# This script demonstrates that a stolen access token cannot be used without the DPoP proof

echo "=================================================="
echo "DPoP Security Test — Stolen Token Prevention"
echo "=================================================="
echo ""

# Replace with your actual stolen token from the logs
STOLEN_TOKEN="${1:-your-token-here}"

if [ "$STOLEN_TOKEN" = "your-token-here" ]; then
    echo "ERROR: No token provided!"
    echo "Usage: $0 '<STOLEN_TOKEN>'"
    echo ""
    echo "To get the token:"
    echo "1. Log in to http://localhost:8001/tak-web"
    echo "2. Look for log line: 'DPoP: Access token (for testing/debugging): <TOKEN>'"
    echo "3. Copy the full token value"
    exit 1
fi

echo "Testing with stolen token..."
echo "Token (first 50 chars): ${STOLEN_TOKEN:0:50}..."
echo ""

# Test 1: Try to use the token WITHOUT DPoP proof (should fail)
echo "TEST 1: Stolen token WITHOUT DPoP proof header"
echo "Command:"
echo "  curl -X POST 'http://localhost:8001/tak-web/rest/create' \\"
echo "    -H 'Authorization: Bearer $STOLEN_TOKEN' \\"
echo "    -H 'Content-Type: application/x-www-form-urlencoded' \\"
echo "    -d 'bestallningJson=test' -v"
echo ""
echo "Expected Result: HTTP 401 Unauthorized"
echo "Expected Error: 'DPoP error=invalid_dpop_proof' or similar"
echo ""

curl -X POST 'http://localhost:8001/tak-web/rest/create' \
    -H "Authorization: Bearer ${STOLEN_TOKEN}" \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -d 'bestallningJson=test' -v 2>&1 | grep -E "(< HTTP|< WWW-Authenticate|error|DPoP)"

echo ""
echo "=================================================="
echo "✅ If you see HTTP 401 above, DPoP protection is WORKING!"
echo "=================================================="
echo ""
echo "Next steps:"
echo "1. Disable DPoP validation in application.properties (set to false)"
echo "2. Rebuild and restart tak-web"
echo "3. Re-run this test — the stolen token will now work without DPoP!"

