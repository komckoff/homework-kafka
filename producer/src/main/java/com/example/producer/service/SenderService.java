package com.example.producer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderService {

    private static int MESSAGE_COUNTER = 0;
    private final KafkaTemplate<String, String> kafka;

    @Value("${application.kafka.topic}")
    private String topicName;

    @Scheduled(fixedRate = 1000)
    public void sendMessage() {
        String message = "Message #" + ++MESSAGE_COUNTER;
        int partition = MESSAGE_COUNTER % 2 == 0 ? 0 : 1;

        kafka.send(topicName, partition, String.valueOf(partition), message)
                        .whenComplete(
                                (result, exception) -> {
                                    if (exception == null) {
                                        log.info(
                                                "Producer sent {}, topic: {}, partition: {}, offset: {}",
                                                message,
                                                result.getRecordMetadata().topic(),
                                                result.getRecordMetadata().partition(),
                                                result.getRecordMetadata().offset());
                                    } else {
                                        log.error("{} was not sent: {}", message, exception.getMessage());
                                    }
                                });
    }
}
