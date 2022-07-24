package gmail.luronbel.sms.entity.impl;

import gmail.luronbel.sms.dto.OrderRequest;
import gmail.luronbel.sms.entity.BalancedOrderBook;
import gmail.luronbel.sms.entity.Order;
import gmail.luronbel.sms.entity.OrderType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link BalancedOrderBook} that makes use of two separate lists to store buy and sell order
 * independently.
 */
@Data
@RequiredArgsConstructor
public class SeparateListOrderBook implements BalancedOrderBook {
    private final String symbol;
    private final List<Order> buyOrders = new LinkedList<>();
    private final List<Order> sellOrders = new LinkedList<>();

    private boolean balanced = false;

    private long idGenerator = 0;

    @Override
    public Order addOrder(final OrderRequest orderRequest) {
        final OrderType type = orderRequest.getType();
        final Order order = buildOrder(orderRequest);

        switch (type) {
            case S:
                addOrderToList(sellOrders, order);
                break;
            case B:
                addOrderToList(buyOrders, order);
                break;
            default:
                throw new IllegalArgumentException("Unknown order type: " + type);
        }

        balanced = false;
        return order;
    }

    public static void addOrderToList(final List<Order> orderList, final Order orderToAdd) {
        for (int i = 0; i < orderList.size(); i++) {
            final Order order = orderList.get(i);
            if (order.getPrice() < orderToAdd.getPrice()) {
                orderList.add(i, orderToAdd);
                return;
            }
        }
        orderList.add(orderToAdd);
    }

    private Order buildOrder(final OrderRequest orderRequest) {
        return new LimitOrder(++idGenerator, orderRequest.getType(), symbol,
                orderRequest.getPrice(), orderRequest.getQuantity());
    }

    @Override
    public void cancelOrder(final long orderId) {
        final Optional<Order> buyOrderOpt = buyOrders.stream()
                .filter(it -> it.getId() == orderId)
                .findAny();
        if (buyOrderOpt.isPresent()) {
            buyOrders.remove(buyOrderOpt.get());
            return;
        }
        final Optional<Order> sellOrderOpt = sellOrders.stream()
                .filter(it -> it.getId() == orderId)
                .findAny();
        sellOrderOpt.ifPresent(sellOrders::remove);
        balanced = false;
    }

    @Override
    public boolean isNotBalanced() {
        return !isBalanced();
    }

    @Override
    public void setBalanced() {
        balanced = true;
    }
}
