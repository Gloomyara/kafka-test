package com.example.producer.service.messaging.util;

import ru.antonovmikhail.dto.event.OrderSendEvent;

import java.math.BigDecimal;

public class FakeOrder {

    public static OrderSendEvent getOrderSendEvent() {
        return new OrderSendEvent(1L,
                "pensil",
                "0000003",
                100,
                new BigDecimal(0.99),
                false,
                false,
                false
        );
    }
}
