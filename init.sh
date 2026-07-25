#!/bin/bash
set -e

echo "=== Harness Initialization: Gestao de Hospedes ==="

echo ""
echo "=== Backend: compile ==="
(cd backend && ./mvnw -q compile)

echo ""
echo "=== Backend: test ==="
(cd backend && ./mvnw -q test)

echo ""
echo "=== Frontend: install ==="
(cd frontend && npm install --no-fund --no-audit)

echo ""
echo "=== Frontend: build (type-check) ==="
(cd frontend && npm run build)

echo ""
echo "=== Frontend: test ==="
(cd frontend && npm run test:ci)

echo ""
echo "=== Verification Complete ==="
echo ""
echo "Next steps:"
echo "1. Read feature_list.json to see the current feature state"
echo "2. Read session-handoff.md and progress.md for context from the last session"
echo "3. Pick ONE unfinished feature (status=not_started), mark it active"
echo "4. Implement only that feature"
echo "5. Re-run ./init.sh before claiming done"
echo ""
echo "Note: 'docker compose up -d' is NOT run automatically by this script."
echo "Backend tests use an in-memory H2 database and do not require Docker."
echo "Run 'docker compose up -d' manually before 'mvnw spring-boot:run' if you need the real Postgres."
