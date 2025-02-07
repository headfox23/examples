package com.example.service;

import many.other.imports.*

@Service
@AllArgsConstructor
@Slf4j
public class ExtraEventProcessor {
    private final ApplicationProperties applicationProperties;
    private final KafkaTemplate<String, ExtraEvent> kafkaTemplate;

    public void process(ExtraEvent extraEvent) {
        try {
            kafkaTemplate.send(applicationProperties.getExtraEventTopicName(), Instant.now().toString(), extraEvent);
        } catch (KafkaException ex) {
            log.error("Could not send message to " + applicationProperties.getExtraEventTopicName(), ex);
        }
    }
}
