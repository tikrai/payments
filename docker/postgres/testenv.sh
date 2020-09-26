#!/bin/sh
echo "Creating test database"
"${psql[@]}" --username $POSTGRES_USER <<-EOSQL
  CREATE DATABASE "${POSTGRES_DB}_test" ;
EOSQL
echo "Test database created"
