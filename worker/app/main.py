import json
import signal
import sys
from typing import Any

from confluent_kafka import Consumer, KafkaError, KafkaException

from app.config import get_settings
from app.database import Database
from app.notification_service import NotificationService


running = True


def shutdown_handler(signum, frame):
    global running
    running = False
    print("Shutdown signal received. Stopping worker...")


def create_consumer(settings):
    consumer = Consumer(
        {
            "bootstrap.servers": settings.kafka_broker,
            "group.id": settings.kafka_consumer_group,
            "auto.offset.reset": "earliest",
            "enable.auto.commit": False,
        }
    )
    consumer.subscribe([settings.kafka_topic])
    return consumer


def decode_event(message_value: bytes) -> dict[str, Any]:
    return json.loads(message_value.decode("utf-8"))


def process_event(
    event: dict[str, Any],
    database: Database,
    notification_service: NotificationService,
) -> None:
    appointment_id = int(event["appointmentId"])
    event_type = event["eventType"]

    print(f"Received event_type={event_type}, appointment_id={appointment_id}")

    database.mark_processing(appointment_id)
    notification_service.send_appointment_notification(event)
    database.mark_notification_sent(appointment_id)


def main() -> int:
    signal.signal(signal.SIGINT, shutdown_handler)
    signal.signal(signal.SIGTERM, shutdown_handler)

    settings = get_settings()
    database = Database(settings)
    notification_service = NotificationService()
    consumer = create_consumer(settings)

    print(
        f"Worker started. topic={settings.kafka_topic}, "
        f"group={settings.kafka_consumer_group}, broker={settings.kafka_broker}"
    )

    try:
        while running:
            message = consumer.poll(1.0)

            if message is None:
                continue

            error = message.error()

            if error is not None:
                if error.code() == KafkaError._PARTITION_EOF:
                    continue

                print(f"Kafka consumer error: {error}")
                continue

            try:
                event = decode_event(message.value())
                process_event(event, database, notification_service)
                consumer.commit(message)
            except Exception as exc:
                print(f"Failed to process message: {exc}")

                try:
                    event = decode_event(message.value())
                    appointment_id = int(event["appointmentId"])
                    database.mark_failed(appointment_id, str(exc))
                except Exception as fail_update_error:
                    print(f"Failed to mark appointment as failed: {fail_update_error}")

                consumer.commit(message)

    finally:
        consumer.close()
        print("Worker stopped.")

    return 0


if __name__ == "__main__":
    sys.exit(main())