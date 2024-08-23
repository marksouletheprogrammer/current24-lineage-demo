#!/bin/bash
#
# Copyright 2018-2023 contributors to the OpenLineage project
# SPDX-License-Identifier: Apache-2.0
#
# Usage: $ ./init-db.sh

set -eu

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" > /dev/null <<-EOSQL
  CREATE USER ${MARQUEZ_USER};
  ALTER USER ${MARQUEZ_USER} WITH PASSWORD '${MARQUEZ_PASSWORD}';
  CREATE DATABASE ${MARQUEZ_DB};
  GRANT ALL PRIVILEGES ON DATABASE ${MARQUEZ_DB} TO ${MARQUEZ_USER};

  CREATE USER ${WEATHER_USER};
  ALTER USER ${WEATHER_USER} WITH PASSWORD '${WEATHER_PASSWORD}';
  CREATE DATABASE ${WEATHER_DB};
  GRANT ALL PRIVILEGES ON DATABASE ${WEATHER_DB} TO ${WEATHER_USER};
EOSQL

psql -v ON_ERROR_STOP=1 -U ${WEATHER_USER} -d ${WEATHER_DB} > /dev/null <<-EOSQL
  CREATE TABLE IF NOT EXISTS weather_prediction(
      id SERIAL PRIMARY KEY, 
      city VARCHAR(255), 
      state VARCHAR(255),
      current_temp INT,
      predict_low INT,
      predict_high INT
  );
EOSQL

echo "WEATHER_DB created"
