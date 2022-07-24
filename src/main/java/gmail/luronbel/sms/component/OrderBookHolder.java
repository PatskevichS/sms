package gmail.luronbel.sms.component;

import gmail.luronbel.sms.entity.BalancedOrderBook;

import java.util.List;
import java.util.Map;

/**
 * Component that provides access to order books.
 */
public interface OrderBookHolder {

    List<BalancedOrderBook> getAllOrderBooks();

    Map<String, BalancedOrderBook> getAllOrderBooksBySymbol();
}
