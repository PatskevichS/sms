package gmail.luronbel.sms.component.impl;

import gmail.luronbel.sms.component.MatchingEngine;
import gmail.luronbel.sms.component.OrderBookHolder;
import gmail.luronbel.sms.component.TradeLedger;
import gmail.luronbel.sms.entity.Balanced;
import gmail.luronbel.sms.entity.BalancedOrderBook;
import gmail.luronbel.sms.entity.Order;
import gmail.luronbel.sms.event.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Simple implementation of {@link MatchingEngine}.
 */
@Service
public class SimpleMatchingEngine implements MatchingEngine {

    private final TradeLedger tradeLedger;
    private final EventPublisher eventPublisher;
    private final List<BalancedOrderBook> orderBooks;

    @Autowired
    public SimpleMatchingEngine(final TradeLedger tradeLedger, final EventPublisher eventPublisher,
                                final OrderBookHolder orderBookHolder) {
        this.tradeLedger = tradeLedger;
        this.eventPublisher = eventPublisher;
        orderBooks = orderBookHolder.getAllOrderBooks();
    }

    @Override
    public void balanceOrderBooks() {
        orderBooks.stream()
                .filter(Balanced::isNotBalanced)
                .filter(SimpleMatchingEngine::hasSellAndBuyOrders)
                .forEach(this::balanceOrderBook);
    }

    private void balanceOrderBook(final BalancedOrderBook orderBook) {
        final Collection<Order> buyOrders = orderBook.getBuyOrders();
        final Collection<Order> sellOrders = orderBook.getSellOrders();

        Iterator<Order> i = buyOrders.iterator();
        while (i.hasNext()) {
            Order buyOrder = i.next();
            final boolean isOrderClosed = processBuyOrder(orderBook, sellOrders, buyOrder);
            if (isOrderClosed) {
                i.remove();
            }
        }

        orderBook.setBalanced();
    }

    private boolean processBuyOrder(final BalancedOrderBook orderBook, final Collection<Order> sellOrders,
                                    final Order buyOrder) {
        Iterator<Order> i = sellOrders.iterator();
        while (i.hasNext()) {
            Order sellOrder = i.next();

            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                final long requiredBuyQuantity = buyOrder.getRequiredQuantity();
                final long requiredSellQuantity = sellOrder.getRequiredQuantity();

                if (requiredBuyQuantity == requiredSellQuantity) {
                    buyOrder.addExecutedQuantity(requiredBuyQuantity);
                    sellOrder.addExecutedQuantity(requiredSellQuantity);
                    i.remove();
                    recordTradeAndSendEvent(orderBook, buyOrder, sellOrder, requiredBuyQuantity);
                    return true;
                } else if (requiredBuyQuantity > requiredSellQuantity) {
                    buyOrder.addExecutedQuantity(requiredSellQuantity);
                    sellOrder.addExecutedQuantity(requiredSellQuantity);
                    i.remove();
                    recordTradeAndSendEvent(orderBook, buyOrder, sellOrder, requiredSellQuantity);
                } else {
                    buyOrder.addExecutedQuantity(requiredBuyQuantity);
                    sellOrder.addExecutedQuantity(requiredBuyQuantity);
                    recordTradeAndSendEvent(orderBook, buyOrder, sellOrder, requiredBuyQuantity);
                    return true;
                }
            }

        }
        return false;
    }

    private void recordTradeAndSendEvent(final BalancedOrderBook orderBook, final Order buyOrder, final Order sellOrder,
                                         final long quantity) {
        final long tradeId = tradeLedger.recordTrade(orderBook.getSymbol(), buyOrder.getId(), sellOrder.getId(),
                quantity, buyOrder.getPrice());
        final EventPublisher.TradeEvent event = new EventPublisher.TradeEvent(
                tradeId, orderBook.getSymbol(), buyOrder.getId(), sellOrder.getId(), quantity, buyOrder.getPrice());
        eventPublisher.publish(event);
    }

    private static boolean hasSellAndBuyOrders(final BalancedOrderBook orderBook) {
        return !orderBook.getBuyOrders()
                .isEmpty() && !orderBook.getSellOrders()
                .isEmpty();
    }
}
