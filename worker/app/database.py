from contextlib import contextmanager

import psycopg

from app.config import Settings


class Database:
    def __init__(self, settings: Settings):
        self.settings = settings

    @contextmanager
    def connection(self):
        conn = psycopg.connect(
            host = self.settings.postgres_host,
            port = self.settings.postgres_port,
            dbname = self.settings.postgres_db,
            user = self.settings.postgres_user,
            password = self.settings.postgres_password
        )

        try:
            yield conn
            conn.commit()
        except Exception:
            conn.rollback()
            raise
        finally:
            conn.close()
    

    def mark_processing(self , appointment_id: int) -> None:
        with self.connection() as conn:
            with conn.cursor() as cur:
                cur.execute(
                    """
                     UPDATE appointments
                     SET processing_status = 'PROCESSING',
                         updated_at = NOW()
                     WHERE id = %s
                    """,
                    (appointment_id,),
                )    


                cur.execute(
                    """
                     INSERT INTO appointment_logs (appointment_id, event_type, message)
                     VALUES (%s, %s, %s)
                    """,
                    (
                        appointment_id,
                        "NOTIFICATION_PROCESSING",
                        "Notification worker started processing appointment event"
                    ), 
                )  

    
    def mark_notification_sent(self, appointment_id: int) -> None:
        with self.connection() as conn:
            with conn.cursor() as cur:
                cur.execute(
                    """
                    UPDATE appointments
                    SET processing_status = 'NOTIFICATION_SENT',
                          updated_at = NOW()
                    WHERE  id = %s
                    """,
                    (appointment_id,),
                )

                cur.execute(
                    """
                    INSERT INTO appointment_logs (appointment_id, event_type, message)
                    VALUES (%s, %s, %s)
                    """,
                    (
                      appointment_id,
                      "NOTIFICATION_SENT",
                      "Notification sent successfully"
                    ),
                )

    
    def mark_failed(self, appointment_id : int, error_message: str) -> None:
        with self.connection() as conn:
            with conn.cursor() as cur:
                cur.execute(
                    """
                    UPDATE appointments
                    SET processing_status = 'FAILED',
                        updated_at = NOW()
                    WHERE id = %s
                    """,
                    (appointment_id,),
                )

                cur.execute(
                    """
                    INSERT INTO appointment_logs (appointment_id, event_type, message)
                    VALUES (%s, %s, %s)
                    """,
                    (
                        appointment_id,
                        "NOTIFICATION_FAILED",
                        error_message[:1000]
                    )
                )