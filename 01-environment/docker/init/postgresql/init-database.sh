#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER orderproc WITH PASSWORD 'orderproc';
    CREATE DATABASE orderproc;
    GRANT ALL PRIVILEGES ON DATABASE orderproc TO orderproc;
EOSQL
