package gmail.luronbel.sms.entity;

import gmail.luronbel.sms.dto.OrderRequest;

import java.util.List;

/**
 * Contains all orders (buy and sell) for a certain stock. Stocks are identified by unique codes called symbols.
 * The book is balanced and all orders are sorted following price/time priority.
 * Orders are ranked according to price (better price having higher priority), and same-price orders are ranked
 * according to the time when they were entered (older orders having higher priority).
 */
public interface BalancedOrderBook extends Balanced {

    String getSymbol();

    Order addOrder(OrderRequest orderRequest);

    void cancelOrder(long orderId);

    List<Order> getBuyOrders();

    List<Order> getSellOrders();
}
