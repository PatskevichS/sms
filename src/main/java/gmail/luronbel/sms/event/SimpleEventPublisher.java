package gmail.luronbel.sms.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Simple implementation of {@link EventPublisher}. Transforms all events into String value and publishes them.
 */
@Service
@RequiredArgsConstructor
public class SimpleEventPublisher implements EventPublisher {
    private static final String ORDER_ADDED_TEMPLATE = "Order with ID %d added: Stock=%s Operation=%s Quantity=%d " +
            "Price=%d";
    private static final String ORDER_CANCELED_TEMPLATE = "Order with ID %d canceled";
    private static final String TRADE_TEMPLATE = "New trade with ID %d: Stock=%s Quantity=%d Price=%d (orders Buy=%d" +
            " and Sell=%d)";

    private final List<Sink> sinks;

    @Override
    public void publish(final Event event) {
        final String notification;

        if (event instanceof TradeEvent) {
            final TradeEvent tradeEvent = (TradeEvent) event;
            notification = String.format(TRADE_TEMPLATE, tradeEvent.getTradeId(), tradeEvent.getOrderBookSymbol(),
                    tradeEvent.getQuantity(), tradeEvent.getPrice(), tradeEvent.getOrderBuyId(),
                    tradeEvent.getOrderSellId());
        } else if (event instanceof OrderAddEvent) {
            final OrderAddEvent orderAddEvent = (OrderAddEvent) event;
            notification = String.format(ORDER_ADDED_TEMPLATE, orderAddEvent.getOrderId(),
                    orderAddEvent.getOrderBookSymbol(), orderAddEvent.getType()
                            .getOperation(), orderAddEvent.getQuantity(), orderAddEvent.getPrice());
        } else if (event instanceof OrderCancelEvent) {
            final OrderCancelEvent orderCancelEvent = (OrderCancelEvent) event;
            notification = String.format(ORDER_CANCELED_TEMPLATE, orderCancelEvent.getOrderId());
        } else {
            throw new IllegalArgumentException("Unknown event type: " + event.getClass()
                    .getName());
        }

        sinks.forEach(it -> it.publish(notification));
    }
}
