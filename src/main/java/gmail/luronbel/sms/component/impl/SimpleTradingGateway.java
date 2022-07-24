package gmail.luronbel.sms.component.impl;

import gmail.luronbel.sms.component.OrderBookHolder;
import gmail.luronbel.sms.component.TradingGateway;
import gmail.luronbel.sms.dto.OrderRequest;
import gmail.luronbel.sms.entity.BalancedOrderBook;
import gmail.luronbel.sms.entity.Order;
import gmail.luronbel.sms.event.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * Simple implementation of {@link TradingGateway} that validates and processes client request.
 */
@Service
public class SimpleTradingGateway implements TradingGateway {

    private final Map<String, BalancedOrderBook> orderBooks;
    private final EventPublisher eventPublisher;

    public SimpleTradingGateway(final EventPublisher eventPublisher, final OrderBookHolder orderBookHolder) {
        this.eventPublisher = eventPublisher;
        orderBooks = orderBookHolder.getAllOrderBooksBySymbol();
    }

    @Override
    public Long addOrder(final OrderRequest orderRequest) {
        validateRequest(orderRequest);

        final String orderBookSymbol = orderRequest.getStock();
        if (!orderBooks.containsKey(orderBookSymbol)) {
            throw new IllegalArgumentException("Unknown order book symbol: " + orderBookSymbol);
        }

        final BalancedOrderBook orderBook = orderBooks.get(orderBookSymbol);
        final Order acceptedOrder = orderBook.addOrder(orderRequest);

        final EventPublisher.OrderAddEvent event = new EventPublisher.OrderAddEvent(acceptedOrder.getId(),
                orderBookSymbol, orderRequest.getType(), acceptedOrder.getRequiredQuantity(), acceptedOrder.getPrice());
        eventPublisher.publish(event);
        return acceptedOrder.getId();
    }

    @Override
    public void cancelOrder(final Long orderId, final String orderBookSymbol) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Invalid order id value. Is should be positive number.");
        }
        if (!orderBooks.containsKey(orderBookSymbol)) {
            throw new IllegalArgumentException("Unknown order book symbol: " + orderBookSymbol);
        }

        final BalancedOrderBook orderBook = orderBooks.get(orderBookSymbol);
        orderBook.cancelOrder(orderId);

        final EventPublisher.OrderCancelEvent event = new EventPublisher.OrderCancelEvent(orderId);
        eventPublisher.publish(event);
    }

    private static void validateRequest(final OrderRequest orderRequest) {
        Objects.requireNonNull(orderRequest.getType());
        Objects.requireNonNull(orderRequest.getStock());
        Objects.requireNonNull(orderRequest.getQuantity());
        Objects.requireNonNull(orderRequest.getPrice());

        if (orderRequest.getStock()
                .length() != 4 || orderRequest.getStock()
                .isBlank()) {
            throw new IllegalArgumentException("Invalid stock value. Is should 4-letters code.");
        }
        if (orderRequest.getPrice() <= 0) {
            throw new IllegalArgumentException("Invalid price value. Is should be positive number.");
        }
        if (orderRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid Quantity value. Is should be positive number.");
        }
    }
}
