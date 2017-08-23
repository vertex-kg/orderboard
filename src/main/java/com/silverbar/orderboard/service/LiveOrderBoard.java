package com.silverbar.orderboard.service;

import com.silverbar.orderboard.model.Order;
import com.silverbar.orderboard.model.OrderType;
import com.silverbar.orderboard.model.SummaryItem;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static com.silverbar.orderboard.model.OrderType.BUY;
import static com.silverbar.orderboard.model.OrderType.SELL;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

public class LiveOrderBoard {

    //using ConcurrentMap to ensure correct operation in multi-threaded environment
    private final ConcurrentMap<Order, String> orders;

    public LiveOrderBoard(ConcurrentMap<Order, String> ordersContainer) {
        this.orders = ordersContainer;
    }

    public void registerOrder(String userId, BigDecimal quantity, BigDecimal unitPrice, OrderType orderType) {
        Order order = new Order(userId, quantity, unitPrice, orderType);
        if(orders.containsKey(order)) {
            throw new RuntimeException("The order already exists");
        } else {
            orders.put(order, "");
        }
    }

    public void cancelOrder(String userId, BigDecimal quantity, BigDecimal unitPrice, OrderType orderType) {
        Order orderToCancel = new Order(userId, quantity, unitPrice, orderType);
        if(orders.containsKey(orderToCancel)) {
            orders.remove(orderToCancel);
        } else {
            throw new RuntimeException("The order does not exist");
        }
    }

    public List<SummaryItem> getSummary() {
        List<SummaryItem> summary = getSummary(BUY);
        summary.addAll(getSummary(SELL));
        return summary;
    }

    private List<SummaryItem> getSummary(OrderType orderType) {

        Map<BigDecimal, BigDecimal> ordersByUnitPrice = orders.keySet()
                .parallelStream()
                .filter(order -> order.getOrderType().equals(orderType))
                .collect(groupingBy(Order::getUnitPrice,
                        mapping(Order::getQuantity,
                                reducing(new BigDecimal("0"), BigDecimal::add))));

        Comparator<SummaryItem> comparator;

        if(orderType == BUY) {
            comparator = comparing(SummaryItem::getUnitPrice).reversed();
        } else {
            comparator = comparing(SummaryItem::getUnitPrice);
        }

        return ordersByUnitPrice
                .entrySet()
                .parallelStream()
                .map(entry -> new SummaryItem(orderType, entry.getKey(), entry.getValue()))
                .sorted(comparator)
                .collect(toList());
    }

}
