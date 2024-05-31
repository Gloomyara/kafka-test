package ru.antonovmikhail.payment.service.messaging.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.antonovmikhail.dto.event.OrderEvent;
import ru.antonovmikhail.dto.event.OrderSendEvent;
import ru.antonovmikhail.dto.model.Order;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessagingService {

    @Value("${topic.send-order}")
    private String sendClientTopic;
    private final String kafkaConsumerGroupId = "${spring.kafka.consumer.group-id}";
    private final String topicCreateOrder = "${topic.get-order}";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ModelMapper modelMapper;

    //ArrayIndexOutOfBoundsException условно ошибка о недостатке средств
    @Transactional
    @RetryableTopic(include = {NullPointerException.class, ArrayIndexOutOfBoundsException.class},
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            retryTopicSuffix = "-try-again",
            dltTopicSuffix = "-dead-t")
    @KafkaListener(topics = topicCreateOrder, groupId = kafkaConsumerGroupId,
            properties = {"spring.json.value.default.type=com.example.service.OrderEvent"})
    public OrderEvent orderPayment(OrderEvent orderEvent) {
        log.info("The product: {} was ordered in quantity: {} and at a price: {}", orderEvent.getProductName(), orderEvent.getQuantity(), orderEvent.getPrice());
        log.info("To pay: {}", new BigDecimal(orderEvent.getQuantity()).multiply(orderEvent.getPrice()));
        sendOrder(modelMapper.map(paymentLogic(modelMapper.map(orderEvent, Order.class)), OrderSendEvent.class));
        return orderEvent;
    }

    private Order paymentLogic(Order order) {
        try {
            Thread.sleep(1000);
            order.setPaid(true);
            order.setBarCode("paid-1");
        } catch (InterruptedException e) {
            return order;
        }
        return order;
    }

    public boolean sendOrder(OrderSendEvent orderSendEvent) {
        CompletableFuture<SendResult<String, Object>> sending = kafkaTemplate.send(sendClientTopic, orderSendEvent.getBarCode(), orderSendEvent);
        try {
            return Objects.nonNull(sending.get());
        } catch (Exception e) {
            return false;
        }
    }
}
