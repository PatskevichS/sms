package gmail.luronbel.sms.event;

import gmail.luronbel.sms.entity.OrderType;
import lombok.Data;

/**
 * Component that is responsible for publishing events to multiple {@link Sink sinks}.
 */
public interface EventPublisher {

    interface Event {
    }

    /**
     * Completed trade.
     */
    @Data
    class TradeEvent implements Event {
        final long tradeId;
        final String orderBookSymbol;
        final long orderBuyId;
        final long orderSellId;
        final long quantity;
        final long price;
    }

    /**
     * Order is added.
     */
    @Data
    class OrderAddEvent implements Event {
        final long orderId;
        final String orderBookSymbol;
        final OrderType type;
        final Long quantity;
        final Long price;
    }

    /**
     * Order is canceled.
     */
    @Data
    class OrderCancelEvent implements Event {
        final long orderId;
    }

    /**
     * Spread event through multiple {@link Sink sinks}.
     */
    void publish(Event event);
}
