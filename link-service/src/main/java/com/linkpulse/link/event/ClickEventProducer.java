package com.linkpulse.link.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ClickEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${link.kafka.topic.click-raw:link-click-raw}")
    private String topic;

    public void publish(ClickEvent event) {
        kafkaTemplate.send(topic, event.getShortCode(), event);
    }

}
