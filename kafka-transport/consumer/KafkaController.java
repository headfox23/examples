package com.example.controller;

import many.nice.imports.*;

@Component
@AllArgsConstructor
@Slf4j
public class ExtraEventController {
    private final MetricsService metricsService;
    private static Map<String, ExtraEvent> eventMap = new ConcurrentHashMap<String, ExtraEvent>();

    @KafkaListener(topics = "#{applicationProperties.extraEventTopicName}")
    public void receive(ExtraEvent extraEvent) {
        if (extraEvent.getEventType().equals("START")) {
            eventMap.put(extraEvent.getEventId(), extraEvent);
        }
        if (extraEvent.getEventType().equals("STOP")) {
            ExtraEvent start = eventMap.get(extraEvent.getEventId());
            if (start != null) {
                metricsService.incrementCounter(this.getClass(), "eventsFinished");
            } else {
                metricsService.incrementCounter(this.getClass(), "eventsFinishedError");
            }
        }
    }

}
