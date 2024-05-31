package ru.antonovmikhail.orders.service.messaging.producer;

import lombok.SneakyThrows;
import ru.antonovmikhail.dto.event.OrderSendEvent;
import ru.antonovmikhail.dto.model.Order;
import ru.antonovmikhail.orders.service.messaging.service.KafkaMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Producer {

    private final KafkaMessagingService kafkaMessagingService;
    private final ModelMapper modelMapper;

    @SneakyThrows
    public Order sendOrderEvent(Order order) {
        if (kafkaMessagingService.sendOrder(modelMapper.map(order, OrderSendEvent.class))) {
            log.info("Send order from producer {}", order);
            return order;
        } else {
            Thread.sleep(1000);
            return sendOrderEvent(order);
        }
    }
}
