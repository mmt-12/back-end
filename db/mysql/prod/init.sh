#!/bin/bash
set -e

ENV_FILE="/env/.env.prod"
export $(grep -v '^#' "$ENV_FILE" | xargs)

DATABASE="memento"

echo "Creating database and user for production environment..."

mysql -u root -p"${MYSQL_ROOT_PASSWORD}" <<EOSQL
CREATE DATABASE IF NOT EXISTS \`${DATABASE}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

DROP USER IF EXISTS '${MYSQL_USER}'@'%';
CREATE USER '${MYSQL_USER}'@'%' IDENTIFIED BY '${MYSQL_PASSWORD}';

GRANT ALL PRIVILEGES ON \`${DATABASE}\`.* TO '${MYSQL_USER}'@'%';
FLUSH PRIVILEGES;
EOSQL

# Check if tables already exist (indicating this is not a fresh database)
TABLE_COUNT=$(mysql -u root -p"${MYSQL_ROOT_PASSWORD}" -D "${DATABASE}" -sN -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '${DATABASE}';")

if [ "$TABLE_COUNT" -eq 0 ]; then
    echo "Fresh database detected. Executing schema.sql to create tables..."
    mysql -u root -p"${MYSQL_ROOT_PASSWORD}" < /docker-entrypoint-initdb.d/schema.sql
    echo "Schema creation completed successfully!"
else
    echo "Existing database detected with ${TABLE_COUNT} tables. Skipping schema.sql execution."
fi

echo "Production database initialization completed successfully!"