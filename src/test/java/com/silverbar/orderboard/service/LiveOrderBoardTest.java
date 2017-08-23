package com.silverbar.orderboard.service;


import com.silverbar.orderboard.model.Order;
import com.silverbar.orderboard.model.SummaryItem;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.silverbar.orderboard.model.OrderType.BUY;
import static com.silverbar.orderboard.model.OrderType.SELL;

public class LiveOrderBoardTest {

    private LiveOrderBoard liveOrderBoard;
    private ConcurrentMap<Order, String> ordersContainer;

    @Before
    public void setUp() throws Exception {
        ordersContainer = new ConcurrentHashMap<>();
        liveOrderBoard = new LiveOrderBoard(ordersContainer);
    }

    @Test
    public void testRegisterSingleOrder() throws Exception {
        //when
        liveOrderBoard.registerOrder("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY);

        //then
        Assertions.assertThat(ordersContainer.size()).isEqualTo(1);
        Order order = ordersContainer.entrySet().stream().map(Map.Entry::getKey).findFirst().get();
        Assertions.assertThat(order).isEqualTo(new Order("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY));
    }

    @Test
    public void testRegisterMultipleOrders() throws Exception {
        //when
        liveOrderBoard.registerOrder("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY);
        liveOrderBoard.registerOrder("user2", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY);

        //then
        Assertions.assertThat(ordersContainer.size()).isEqualTo(2);
        List<Order> orderList = ordersContainer.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
        Assertions.assertThat(orderList).containsExactlyInAnyOrder(
                new Order("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY),
                new Order("user2", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY)
        );
    }

    @Test(expected = RuntimeException.class)
    public void testRegisterDuplicateOrder() throws Exception {
        //given
        Order existingOrder = new Order("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY);
        ordersContainer.put(existingOrder, "");

        //when
        liveOrderBoard.registerOrder("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY);

        //then expect exception
    }

    @Test
    public void testCancelExistingOrder() throws Exception {

        //given
        Order existingOrder = new Order("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY);
        ordersContainer.put(existingOrder, "");

        //when
        liveOrderBoard.cancelOrder("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY);

        //then
        Assertions.assertThat(ordersContainer).isEmpty();
    }

    @Test(expected = RuntimeException.class)
    public void testCancelNonExistingOrder() throws Exception {

        //given
        Order existingOrder = new Order("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY);
        ordersContainer.put(existingOrder, "");

        //when
        liveOrderBoard.cancelOrder("user1", new BigDecimal("5.67"), new BigDecimal("204.11"), BUY);

        //then expect exception
    }

    @Test
    public void testGetSummaryWhenThereAreNoOrders() throws Exception {

        //given no orders exist

        //when
        List<SummaryItem> summary = liveOrderBoard.getSummary();

        //then
        Assertions.assertThat(summary).isEmpty();
    }

    @Test
    public void testGetSummaryWhenJoiningBuyOrdersForSamePriceAndType() throws Exception {

        //given
        ordersContainer.put(new Order("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY), "");
        ordersContainer.put(new Order("user2", new BigDecimal("4.56"), new BigDecimal("204.11"), BUY), "");

        //when
        List<SummaryItem> summary = liveOrderBoard.getSummary();

        //then
        Assertions.assertThat(summary.size()).isEqualTo(1);
        SummaryItem actualSummaryItem = summary.get(0);
        Assertions.assertThat(actualSummaryItem).isEqualTo(new SummaryItem(BUY, new BigDecimal("204.11"), new BigDecimal("5.79")));
    }

    @Test
    public void testGetSummaryWhenJoiningSellOrdersForSamePriceAndType() throws Exception {

        //given
        ordersContainer.put(new Order("user1", new BigDecimal("3.21"), new BigDecimal("104.33"), SELL), "");
        ordersContainer.put(new Order("user2", new BigDecimal("4.79"), new BigDecimal("104.33"), SELL), "");

        //when
        List<SummaryItem> summary = liveOrderBoard.getSummary();

        //then
        Assertions.assertThat(summary.size()).isEqualTo(1);
        SummaryItem actualSummaryItem = summary.get(0);
        Assertions.assertThat(actualSummaryItem).isEqualTo(new SummaryItem(SELL, new BigDecimal("104.33"), new BigDecimal("8.00")));

        System.out.println(actualSummaryItem);
    }

    @Test
    public void testGetSummarySorting() throws Exception {

        //given
        ordersContainer.put(new Order("user1", new BigDecimal("3.21"), new BigDecimal("104.33"), SELL), "");
        ordersContainer.put(new Order("user2", new BigDecimal("2.12"), new BigDecimal("304.33"), SELL), "");
        ordersContainer.put(new Order("user3", new BigDecimal("4.79"), new BigDecimal("104.33"), SELL), "");
        ordersContainer.put(new Order("user1", new BigDecimal("1.23"), new BigDecimal("204.11"), BUY), "");
        ordersContainer.put(new Order("user3", new BigDecimal("6.91"), new BigDecimal("100.51"), BUY), "");
        ordersContainer.put(new Order("user2", new BigDecimal("4.56"), new BigDecimal("204.11"), BUY), "");

        //when
        List<SummaryItem> summary = liveOrderBoard.getSummary();

        //then
        Assertions.assertThat(summary.size()).isEqualTo(4);
        Assertions.assertThat(summary).containsExactly(
                new SummaryItem(BUY, new BigDecimal("204.11"), new BigDecimal("5.79")),
                new SummaryItem(BUY, new BigDecimal("100.51"), new BigDecimal("6.91")),
                new SummaryItem(SELL, new BigDecimal("104.33"), new BigDecimal("8.00")),
                new SummaryItem(SELL, new BigDecimal("304.33"), new BigDecimal("2.12"))
        );
    }


}