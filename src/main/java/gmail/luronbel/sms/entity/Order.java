package gmail.luronbel.sms.entity;


/**
 * Stock order.
 */
public interface Order {

    long getId();

    long getPrice();

    /**
     * Returns quantity needed to trade.
     */
    long getRequiredQuantity();

    /**
     * Adds quantity that's been traded.
     *
     * @throws IllegalArgumentException if provided quantity more required
     */
    void addExecutedQuantity(long quantity);
}
