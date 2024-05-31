package ru.antonovmikhail.orders.service.messaging.service;


import org.springframework.kafka.support.SendResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.antonovmikhail.dto.event.OrderSendEvent;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaMessagingService {

    @Value("${topic.send-order}")
    private String sendClientTopic;

    private final KafkaTemplate<String , Object> kafkaTemplate;

    public boolean sendOrder(OrderSendEvent orderSendEvent) {
        CompletableFuture<SendResult<String, Object>> sending = kafkaTemplate.send(sendClientTopic, orderSendEvent.getBarCode(), orderSendEvent);
        try {
            return Objects.nonNull(sending.get());
        } catch (Exception e) {
            return false;
        }
    }
}
