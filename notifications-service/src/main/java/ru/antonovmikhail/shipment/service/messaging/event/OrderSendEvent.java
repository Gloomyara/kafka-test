package ru.antonovmikhail.shipment.service.messaging.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSendEvent {

    private Long UserId;
    private String productName;
    private String barCode;
    private int quantity;
    private BigDecimal price;
    private boolean isPaid;
    private boolean isShipped;
    private boolean isDelivered;

}
