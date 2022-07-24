package gmail.luronbel.sms.component.impl;

import gmail.luronbel.sms.component.OrderBookHolder;
import gmail.luronbel.sms.component.TradeLedger;
import gmail.luronbel.sms.entity.BalancedOrderBook;
import gmail.luronbel.sms.entity.Order;
import gmail.luronbel.sms.event.EventPublisher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SimpleMatchingEngineTest {

    private static final String FIRST_BOOK_SYMBOL = "FRST";
    private static final String SECOND_BOOK_SYMBOL = "SCND";
    @Mock
    private TradeLedger tradeLedger;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private OrderBookHolder orderBookHolder;
    private SimpleMatchingEngine matchingEngine;

    private final BalancedOrderBook firstBook = mock(BalancedOrderBook.class);
    private final BalancedOrderBook secondBook = mock(BalancedOrderBook.class);

    @Before
    public void init() {
        when(orderBookHolder.getAllOrderBooks()).thenReturn(Arrays.asList(firstBook, secondBook));
        matchingEngine = new SimpleMatchingEngine(tradeLedger, eventPublisher, orderBookHolder);

        when(firstBook.isNotBalanced()).thenReturn(false);
        when(secondBook.isNotBalanced()).thenReturn(false);
        when(firstBook.getSymbol()).thenReturn(FIRST_BOOK_SYMBOL);
        when(secondBook.getSymbol()).thenReturn(SECOND_BOOK_SYMBOL);
    }

    @Test
    public void balanceOrderBooks_shouldProcessOnlyNotBalancedOrderBooks() {
        matchingEngine.balanceOrderBooks();

        verify(firstBook, only()).isNotBalanced();
        verify(secondBook, only()).isNotBalanced();
    }

    @Test
    public void balanceOrderBooks_shouldProcessOnlyOnesWhichHaveSellAndBuyOrders() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        when(secondBook.isNotBalanced()).thenReturn(true);
        when(firstBook.getSellOrders()).thenReturn(List.of());
        when(firstBook.getBuyOrders()).thenReturn(List.of(mock(Order.class)));
        when(secondBook.getBuyOrders()).thenReturn(List.of());

        matchingEngine.balanceOrderBooks();

        verify(firstBook).getBuyOrders();
        verify(firstBook).getSellOrders();
        verify(secondBook).getBuyOrders();
    }

    @Test
    public void balanceExactOrders_shouldRemoveOrdersFromOrderBooks() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 25);
        final Order sellOrder = mockOrder(2, 50, 25);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).isEmpty();
        assertThat(sellOrders).isEmpty();
        verify(buyOrder).addExecutedQuantity(50);
        verify(sellOrder).addExecutedQuantity(50);
        verify(firstBook).setBalanced();
    }

    @Test
    public void balanceExactOrders_shouldRecordTradeAndSendEvent() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 25);
        final Order sellOrder = mockOrder(2, 50, 25);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);
        final long tradeId = 7L;
        when(tradeLedger.recordTrade(anyString(), anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(tradeId);

        matchingEngine.balanceOrderBooks();

        verify(tradeLedger).recordTrade(FIRST_BOOK_SYMBOL, buyOrder.getId(), sellOrder.getId(), 50L, 25L);
        verify(eventPublisher).publish(
                new EventPublisher.TradeEvent(tradeId, FIRST_BOOK_SYMBOL, buyOrder.getId(), sellOrder.getId(), 50,
                        25L));
    }

    @Test
    public void balanceBuyQuantityMoreThanSell_shouldRemoveOnlySellOrderFromOrderBook() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 70, 25);
        final Order sellOrder = mockOrder(2, 50, 25);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).containsExactly(buyOrder);
        assertThat(sellOrders).isEmpty();
        verify(buyOrder).addExecutedQuantity(50);
        verify(sellOrder).addExecutedQuantity(50);
        verify(firstBook).setBalanced();
    }


    @Test
    public void balanceBuyQuantityMoreThanSell_shouldRecordTradeAndSendEvent() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 70, 25);
        final Order sellOrder = mockOrder(2, 50, 25);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);
        final long tradeId = 7L;
        when(tradeLedger.recordTrade(anyString(), anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(tradeId);

        matchingEngine.balanceOrderBooks();

        verify(tradeLedger).recordTrade(FIRST_BOOK_SYMBOL, buyOrder.getId(), sellOrder.getId(), 50L, 25L);
        verify(eventPublisher).publish(
                new EventPublisher.TradeEvent(tradeId, FIRST_BOOK_SYMBOL, buyOrder.getId(), sellOrder.getId(), 50,
                        25L));
    }

    @Test
    public void balanceBuyQuantityLessThanSell_shouldRemoveOnlyBuyOrderFromOrderBook() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 25);
        final Order sellOrder = mockOrder(2, 70, 25);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).isEmpty();
        assertThat(sellOrders).containsExactly(sellOrder);
        verify(buyOrder).addExecutedQuantity(50);
        verify(sellOrder).addExecutedQuantity(50);
        verify(firstBook).setBalanced();
    }

    @Test
    public void balanceBuyQuantityLessThanSell_shouldRecordTradeAndSendEvent() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 25);
        final Order sellOrder = mockOrder(2, 70, 25);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);
        final long tradeId = 7L;
        when(tradeLedger.recordTrade(anyString(), anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(tradeId);

        matchingEngine.balanceOrderBooks();

        verify(tradeLedger).recordTrade(FIRST_BOOK_SYMBOL, buyOrder.getId(), sellOrder.getId(), 50L, 25L);
        verify(eventPublisher).publish(
                new EventPublisher.TradeEvent(tradeId, FIRST_BOOK_SYMBOL, buyOrder.getId(), sellOrder.getId(), 50,
                        25L));
    }

    @Test
    public void balanceSellPriceHigherThanBuy_shouldNotRemoveAnyOrdersFromOrderBooks() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 25);
        final Order sellOrder = mockOrder(2, 50, 30);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).containsExactly(buyOrder);
        assertThat(sellOrders).containsExactly(sellOrder);
        verify(buyOrder, never()).addExecutedQuantity(anyLong());
        verify(sellOrder, never()).addExecutedQuantity(anyLong());
        verify(firstBook).setBalanced();
    }

    @Test
    public void balanceSellPriceHigherThanBuy_shouldNotRecordTradeAndSendEvent() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 25);
        final Order sellOrder = mockOrder(2, 50, 30);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        verify(tradeLedger, never()).recordTrade(anyString(), anyLong(), anyLong(), anyLong(), anyLong());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    public void balanceBuyPriceHigherThanSell_shouldMakeTrade() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 30);
        final Order sellOrder = mockOrder(2, 50, 25);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).isEmpty();
        assertThat(sellOrders).isEmpty();
        verify(buyOrder).addExecutedQuantity(50);
        verify(sellOrder).addExecutedQuantity(50);
        verify(firstBook).setBalanced();
    }

    @Test
    public void balanceBuyPriceHigherThanSell_shouldRecordTradeWithBuyPriceAndSendEvent() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 30);
        final Order sellOrder = mockOrder(2, 50, 25);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);
        final long tradeId = 7L;
        when(tradeLedger.recordTrade(anyString(), anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(tradeId);

        matchingEngine.balanceOrderBooks();

        verify(tradeLedger).recordTrade(FIRST_BOOK_SYMBOL, buyOrder.getId(), sellOrder.getId(), 50L, 30L);
        verify(eventPublisher).publish(
                new EventPublisher.TradeEvent(tradeId, FIRST_BOOK_SYMBOL, buyOrder.getId(), sellOrder.getId(), 50,
                        30L));
    }

    @Test
    public void balanceOrdersWithMultipleOrderBooks_shouldBalanceOrdersWithinTheSameOrderBook() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        when(secondBook.isNotBalanced()).thenReturn(true);
        final Order buyOrderFirstBook = mockOrder(1, 50, 25);
        final Order sellOrderFirstBook = mockOrder(2, 50, 25);
        final List<Order> buyOrdersFirstBook = new ArrayList<>(List.of(buyOrderFirstBook));
        final List<Order> sellOrdersFirstBook = new ArrayList<>(List.of(sellOrderFirstBook));
        when(firstBook.getBuyOrders()).thenReturn(buyOrdersFirstBook);
        when(firstBook.getSellOrders()).thenReturn(sellOrdersFirstBook);
        final Order buyOrderSecondBook = mockOrder(1, 50, 25);
        final Order sellOrderSecondSecond = mockOrder(2, 50, 100);
        final List<Order> buyOrdersSecondBook = new ArrayList<>(List.of(buyOrderSecondBook));
        final List<Order> sellOrdersSecondBook = new ArrayList<>(List.of(sellOrderSecondSecond));
        when(secondBook.getBuyOrders()).thenReturn(buyOrdersSecondBook);
        when(secondBook.getSellOrders()).thenReturn(sellOrdersSecondBook);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrdersFirstBook).isEmpty();
        assertThat(sellOrdersFirstBook).isEmpty();
        assertThat(sellOrdersSecondBook).containsExactly(sellOrderSecondSecond);
        assertThat(buyOrdersSecondBook).containsExactly(buyOrderSecondBook);
        verify(buyOrderFirstBook).addExecutedQuantity(50);
        verify(sellOrderFirstBook).addExecutedQuantity(50);
        verify(buyOrderSecondBook, never()).addExecutedQuantity(anyLong());
        verify(sellOrderSecondSecond, never()).addExecutedQuantity(anyLong());
        verify(firstBook).setBalanced();
        verify(secondBook).setBalanced();
    }

    @Test
    public void balanceOrdersWithMultipleOrderBooks_shouldBalanceAllOrderBooks() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        when(secondBook.isNotBalanced()).thenReturn(true);
        final Order buyOrderFirstBook = mockOrder(1, 50, 25);
        final Order sellOrderFirstBook = mockOrder(2, 50, 25);
        final List<Order> buyOrdersFirstBook = new ArrayList<>(List.of(buyOrderFirstBook));
        final List<Order> sellOrdersFirstBook = new ArrayList<>(List.of(sellOrderFirstBook));
        when(firstBook.getBuyOrders()).thenReturn(buyOrdersFirstBook);
        when(firstBook.getSellOrders()).thenReturn(sellOrdersFirstBook);
        final Order buyOrderSecondBook = mockOrder(1, 25, 40);
        final Order sellOrderSecondSecond = mockOrder(2, 25, 40);
        final List<Order> buyOrdersSecondBook = new ArrayList<>(List.of(buyOrderSecondBook));
        final List<Order> sellOrdersSecondBook = new ArrayList<>(List.of(sellOrderSecondSecond));
        when(secondBook.getBuyOrders()).thenReturn(buyOrdersSecondBook);
        when(secondBook.getSellOrders()).thenReturn(sellOrdersSecondBook);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrdersFirstBook).isEmpty();
        assertThat(sellOrdersFirstBook).isEmpty();
        assertThat(sellOrdersSecondBook).isEmpty();
        assertThat(buyOrdersSecondBook).isEmpty();
        verify(buyOrderFirstBook).addExecutedQuantity(50);
        verify(sellOrderFirstBook).addExecutedQuantity(50);
        verify(buyOrderSecondBook).addExecutedQuantity(25);
        verify(sellOrderSecondSecond).addExecutedQuantity(25);
        verify(firstBook).setBalanced();
        verify(secondBook).setBalanced();
    }


    @Test
    public void balanceOrdersWithMultipleOrderBooks_shouldRecordTradeAndSendEventForAllOrderBooks() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        when(secondBook.isNotBalanced()).thenReturn(true);
        final Order buyOrderFirstBook = mockOrder(1, 50, 25);
        final Order sellOrderFirstBook = mockOrder(2, 50, 25);
        final List<Order> buyOrdersFirstBook = new ArrayList<>(List.of(buyOrderFirstBook));
        final List<Order> sellOrdersFirstBook = new ArrayList<>(List.of(sellOrderFirstBook));
        when(firstBook.getBuyOrders()).thenReturn(buyOrdersFirstBook);
        when(firstBook.getSellOrders()).thenReturn(sellOrdersFirstBook);
        final Order buyOrderSecondBook = mockOrder(3, 25, 40);
        final Order sellOrderSecondSecond = mockOrder(4, 25, 40);
        final List<Order> buyOrdersSecondBook = new ArrayList<>(List.of(buyOrderSecondBook));
        final List<Order> sellOrdersSecondBook = new ArrayList<>(List.of(sellOrderSecondSecond));
        when(secondBook.getBuyOrders()).thenReturn(buyOrdersSecondBook);
        when(secondBook.getSellOrders()).thenReturn(sellOrdersSecondBook);
        final long firstTradeId = 7L;
        final long secondTradeId = 7L;
        when(tradeLedger.recordTrade(anyString(), anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(firstTradeId)
                .thenReturn(secondTradeId);

        matchingEngine.balanceOrderBooks();

        verify(tradeLedger).recordTrade(FIRST_BOOK_SYMBOL, buyOrderFirstBook.getId(), sellOrderFirstBook.getId(), 50L
                , 25L);
        verify(tradeLedger).recordTrade(SECOND_BOOK_SYMBOL, buyOrderSecondBook.getId(), sellOrderSecondSecond.getId()
                , 25L, 40L);
        verify(eventPublisher).publish(
                new EventPublisher.TradeEvent(firstTradeId, FIRST_BOOK_SYMBOL, buyOrderFirstBook.getId(),
                        sellOrderFirstBook.getId(), 50,
                        25L));
        verify(eventPublisher).publish(
                new EventPublisher.TradeEvent(secondTradeId, SECOND_BOOK_SYMBOL, buyOrderSecondBook.getId(),
                        sellOrderSecondSecond.getId(), 25L,
                        40L));
    }

    @Test
    public void balanceOneBuyOrderToTwoSellOrders_shouldBalanceSuccessfully() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 25);
        when(buyOrder.getRequiredQuantity())
                .thenReturn(50L)
                .thenReturn(20L);
        final Order firstSellOrder = mockOrder(2, 30, 25);
        final Order secondSellOrder = mockOrder(3, 20, 25);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(firstSellOrder, secondSellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).isEmpty();
        assertThat(sellOrders).isEmpty();
        verify(buyOrder).addExecutedQuantity(30);
        verify(buyOrder).addExecutedQuantity(20);
        verify(firstSellOrder).addExecutedQuantity(30);
        verify(secondSellOrder).addExecutedQuantity(20);
        verify(firstBook).setBalanced();
    }

    @Test
    public void balanceTwoBuyOrderToOneSellOrders_shouldBalanceSuccessfully() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order firstBuyOrder = mockOrder(1, 20, 25);
        final Order secondBuyOrder = mockOrder(2, 30, 25);
        final Order sellOrder = mockOrder(2, 30, 25);
        when(sellOrder.getRequiredQuantity())
                .thenReturn(50L)
                .thenReturn(30L);
        final List<Order> buyOrders = new ArrayList<>(List.of(firstBuyOrder, secondBuyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).isEmpty();
        assertThat(sellOrders).isEmpty();
        verify(firstBuyOrder).addExecutedQuantity(20);
        verify(secondBuyOrder).addExecutedQuantity(30);
        verify(sellOrder).addExecutedQuantity(20);
        verify(sellOrder).addExecutedQuantity(30);
        verify(firstBook).setBalanced();
    }

    @Test
    public void balanceOneBuyOrderToMultipleSellOrders_shouldMatchCorrectly() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 25);
        when(buyOrder.getRequiredQuantity())
                .thenReturn(50L)
                .thenReturn(20L);
        final Order firstSellOrder = mockOrder(2, 30, 30);
        final Order secondSellOrder = mockOrder(3, 30, 20);
        final Order thirdSellOrder = mockOrder(4, 20, 35);
        final Order fourthSellOrder = mockOrder(5, 20, 25);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(
                List.of(firstSellOrder, secondSellOrder, thirdSellOrder, fourthSellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).isEmpty();
        assertThat(sellOrders).containsExactly(firstSellOrder, thirdSellOrder);
        verify(buyOrder).addExecutedQuantity(30);
        verify(buyOrder).addExecutedQuantity(20);
        verify(secondSellOrder).addExecutedQuantity(30);
        verify(fourthSellOrder).addExecutedQuantity(20);
        verify(firstBook).setBalanced();
    }

    @Test
    public void balanceMultipleBuyOrdersToOneSellOrder_shouldMatchCorrectly() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order firstBuyOrder = mockOrder(1, 20, 25);
        final Order secondBuyOrder = mockOrder(2, 20, 35);
        final Order thirdBuyOrder = mockOrder(3, 30, 20);
        final Order fourthBuyOrder = mockOrder(4, 30, 30);
        final Order sellOrder = mockOrder(5, 50, 30);
        when(sellOrder.getRequiredQuantity())
                .thenReturn(50L)
                .thenReturn(30L);
        final List<Order> buyOrders = new ArrayList<>(List.of(firstBuyOrder, secondBuyOrder, thirdBuyOrder,
                fourthBuyOrder));
        final List<Order> sellOrders = new ArrayList<>(
                List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).containsExactly(firstBuyOrder, thirdBuyOrder);
        assertThat(sellOrders).isEmpty();
        verify(sellOrder).addExecutedQuantity(20);
        verify(sellOrder).addExecutedQuantity(30);
        verify(secondBuyOrder).addExecutedQuantity(20);
        verify(fourthBuyOrder).addExecutedQuantity(30);
        verify(firstBook).setBalanced();
    }

    @Test
    public void balanceMultipleTradesWithinSingleOrderBook_shouldMakeMultipleTrades() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order firstBuyOrder = mockOrder(1, 15, 35);
        final Order secondBuyOrder = mockOrder(2, 15, 20);
        final Order thirdBuyOrder = mockOrder(3, 10, 15);
        final Order firstSellOrder = mockOrder(4, 30, 20);
        final Order secondSellOrder = mockOrder(5, 10, 15);
        when(firstSellOrder.getRequiredQuantity())
                .thenReturn(30L)
                .thenReturn(15L);
        final List<Order> buyOrders = new ArrayList<>(List.of(firstBuyOrder, secondBuyOrder, thirdBuyOrder));
        final List<Order> sellOrders = new ArrayList<>(
                List.of(firstSellOrder, secondSellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).isEmpty();
        assertThat(sellOrders).isEmpty();
        verify(firstBuyOrder).addExecutedQuantity(15);
        verify(secondBuyOrder).addExecutedQuantity(15);
        verify(thirdBuyOrder).addExecutedQuantity(10);
        verify(firstSellOrder, times(2)).addExecutedQuantity(15);
        verify(secondSellOrder).addExecutedQuantity(10);
        verify(firstBook).setBalanced();
    }

    @Test
    public void balanceNoMatchingOrders_shouldNotRemoveOrdersFromOrderBooks() {
        when(firstBook.isNotBalanced()).thenReturn(true);
        final Order buyOrder = mockOrder(1, 50, 20);
        final Order sellOrder = mockOrder(2, 50, 30);
        final List<Order> buyOrders = new ArrayList<>(List.of(buyOrder));
        final List<Order> sellOrders = new ArrayList<>(List.of(sellOrder));
        when(firstBook.getBuyOrders()).thenReturn(buyOrders);
        when(firstBook.getSellOrders()).thenReturn(sellOrders);

        matchingEngine.balanceOrderBooks();

        assertThat(buyOrders).containsExactly(buyOrder);
        assertThat(sellOrders).containsExactly(sellOrder);
        verify(buyOrder, never()).addExecutedQuantity(anyLong());
        verify(sellOrder, never()).addExecutedQuantity(anyLong());
        verify(firstBook).setBalanced();
    }

    private Order mockOrder(final long id, final long quantity, final long price) {
        final Order order = mock(Order.class);
        when(order.getId()).thenReturn(id);
        when(order.getRequiredQuantity()).thenReturn(quantity);
        when(order.getPrice()).thenReturn(price);
        return order;
    }
}
