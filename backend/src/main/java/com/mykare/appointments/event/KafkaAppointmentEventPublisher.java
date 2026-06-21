package com.mykare.appointments.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class KafkaAppointmentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaAppointmentEventPublisher.class);

    private final KafkaTemplate<String, AppointmentEvent> kafkaTemplate;
    private final String topic;

    public KafkaAppointmentEventPublisher(
            KafkaTemplate<String, AppointmentEvent> kafkaTemplate,
            @Value("${app.kafka.appointment-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(AppointmentEvent event) {
        String key = String.valueOf(event.appointmentId());

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error(
                                "Failed to publish appointment event. eventId{}, appointmentId{}, eventType={}",
                                event.eventId(),
                                event.appointmentId(),
                                event.eventType(),
                                ex);
                        return;
                    }

                    log.info(
                            "Published appointment event. eventId={}, appointmentId={}, eventType={}, topic={}, partition={}, offset={}",
                            event.eventId(),
                            event.appointmentId(),
                            event.eventType(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                });
    }

}
