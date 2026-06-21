import os
from dataclasses import dataclass

from dotenv import load_dotenv


load_dotenv()


@dataclass(frozen=True)
class Settings:
    kafka_broker: str
    kafka_topic: str
    kafka_consumer_group: str


    postgres_host: str
    postgres_port: int
    postgres_db: str
    postgres_user: str
    postgres_password: str


def get_settings() -> Settings:
    return Settings(
        kafka_broker = os.getenv("KAFKA_BROKER", "localhost:9092"),
        kafka_topic = os.getenv("APPOINTMENT_EVENTS_TOPIC", "appointment-events"),
        kafka_consumer_group = os.getenv("KAFKA_CONSUMER_GROUP", "notification-worker"),
        postgres_host = os.getenv("POSTGRES_HOST", "localhost"),
        postgres_port = int(os.getenv("POSTGRES_PORT", "5433")),
        postgres_db = os.getenv("POSTGRES_DB", "mykare_appointments"),
        postgres_user = os.getenv("POSTGRES_USER", "mykare"),
        postgres_password = os.getenv("POSTGRES_PASSWORD", "mykare_password"),
    )