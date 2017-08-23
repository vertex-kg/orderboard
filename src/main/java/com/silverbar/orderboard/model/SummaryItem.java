package com.silverbar.orderboard.model;

import java.math.BigDecimal;
import java.util.Objects;

public class SummaryItem {

    private final OrderType orderType;
    private final BigDecimal unitPrice;
    private final BigDecimal quantity;

    public SummaryItem(OrderType orderType, BigDecimal unitPrice, BigDecimal quantity) {
        this.orderType = orderType;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SummaryItem that = (SummaryItem) o;
        return orderType == that.orderType &&
                Objects.equals(unitPrice, that.unitPrice) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderType, unitPrice, quantity);
    }

    @Override
    public String toString() {
        return String.format("%s %s kg for %s", orderType, quantity.toString(), unitPrice.toString());
    }
}
