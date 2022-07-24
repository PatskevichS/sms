package gmail.luronbel.sms.entity.impl;

import gmail.luronbel.sms.entity.Order;
import gmail.luronbel.sms.entity.OrderType;
import lombok.Data;

/**
 * Order to buy/sell stocks for certain price.
 */
@Data
public class LimitOrder implements Order {
    private final long id;
    private final OrderType type;
    private final String stock;
    private final long price;
    private long requiredQuantity;
    private Long executedQuantity = 0L;

    public LimitOrder(final long id, final OrderType type, final String stock, final long price,
                      final long requiredQuantity) {
        this.id = id;
        this.type = type;
        this.stock = stock;
        this.price = price;
        this.requiredQuantity = requiredQuantity;
    }

    @Override
    public void addExecutedQuantity(final long quantity) {
        if (requiredQuantity < quantity) {
            throw new IllegalArgumentException("Required quantity is less then processed for order with id " + id);
        }
        executedQuantity += quantity;
        requiredQuantity -= quantity;
    }
}
