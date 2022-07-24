package gmail.luronbel.sms.component;

import gmail.luronbel.sms.dto.OrderRequest;

/**
 * Client gateway to manipulate orders.
 */
public interface TradingGateway {

    Long addOrder(OrderRequest orderRequest);

    void cancelOrder(Long orderId, String orderBookSymbol);
}
