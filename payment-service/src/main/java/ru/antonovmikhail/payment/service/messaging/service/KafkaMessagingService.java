package ru.antonovmikhail.payment.service.messaging.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.antonovmikhail.payment.model.Order;
import ru.antonovmikhail.payment.service.messaging.event.OrderEvent;
import ru.antonovmikhail.payment.service.messaging.event.OrderSendEvent;

import java.math.BigDecimal;

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

    @Transactional
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
        } catch (InterruptedException e) {
            return order;
        }
        return order;
    }

    public void sendOrder(OrderSendEvent orderSendEvent) {
        kafkaTemplate.send(sendClientTopic, orderSendEvent.getBarCode(), orderSendEvent);
    }
}
