package ru.antonovmikhail.payment.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Long UserId;
    private String productName;
    private String barCode;
    private int quantity;
    private BigDecimal price;
    private boolean isPaid;
    private boolean isShipped;
    private boolean isDelivered;

}
