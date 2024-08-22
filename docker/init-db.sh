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

  CREATE USER ${DATA_USER};
  ALTER USER ${DATA_USER} WITH PASSWORD '${DATA_PASSWORD}';
  CREATE DATABASE ${DATA_DB};
  GRANT ALL PRIVILEGES ON DATABASE ${DATA_DB} TO ${DATA_USER};
EOSQL

psql -v ON_ERROR_STOP=1 --username "${DATA_USER}" > /dev/null <<-EOSQL
  \c ${DATA_DB};
  CREATE TABLE IF NOT EXISTS sink_event(id varchar(255), version bigint, counter bigint);
EOSQL

echo "DATA_DB created"
