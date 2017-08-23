package com.silverbar.orderboard.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Order {

    private final String userId;
    private final BigDecimal quantity;
    private final BigDecimal unitPrice;
    private final OrderType orderType;

    public Order(String userId, BigDecimal quantity, BigDecimal unitPrice, OrderType orderType) {
        this.userId = userId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.orderType = orderType;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(userId, order.userId) &&
                Objects.equals(quantity, order.quantity) &&
                Objects.equals(unitPrice, order.unitPrice) &&
                orderType == order.orderType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, quantity, unitPrice, orderType);
    }
}
