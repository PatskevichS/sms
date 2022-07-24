package gmail.luronbel.sms.component;

/**
 * Component that holds all completed trades.
 */
public interface TradeLedger {

    long recordTrade(String orderBookSymbol, Long orderBuyId, Long orderSellId,
                     Long quantity, Long price);
}
