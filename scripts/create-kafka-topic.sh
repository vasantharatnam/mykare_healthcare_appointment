#!/usr/bin/env bash
set -euo pipefail

TOPIC="${APPOINTMENT_EVENTS_TOPIC:-appointment-events}"

echo "Waiting for Kafka..."
sleep 10

kafka-topics \
  --bootstrap-server kafka:29092 \
  --create \
  --if-not-exists \
  --topic "${TOPIC}" \
  --partitions 1 \
  --replication-factor 1

echo "Kafka topic ready: ${TOPIC}"