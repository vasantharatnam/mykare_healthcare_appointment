import time
from typing import Any



class NotificationService:
      def send_appointment_notification(self, event: dict[str, any]) -> None:
            appointment_id = event["appointmentId"]
            event_type = event["eventType"]
            doctor_name = event["doctorName"]
            slot_start = event["slotStart"]

            print(
                  f"Sending notification for appointment_id={appointment_id}, "
                  f"event_type={event_type}, doctor={doctor_name}, slot_start={slot_start}"
            )

            time.sleep(1)

            print(f"Notification sent for appointment_id={appointment_id}")