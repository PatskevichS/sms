package gmail.luronbel.sms.entity.impl;

import gmail.luronbel.sms.dto.OrderRequest;
import gmail.luronbel.sms.entity.Order;
import gmail.luronbel.sms.entity.OrderType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class SeparateListOrderBookTest {

    private static final String STOCK = "TEST";

    @Test
    public void addBuyAndSellOrder_shouldPlaceOrdersInSeparateLists() {
        final SeparateListOrderBook orderBook = new SeparateListOrderBook(STOCK);
        final OrderRequest buyOrderRequest = new OrderRequest(OrderType.B, STOCK, 100L, 30L);
        final OrderRequest sellOrderRequest = new OrderRequest(OrderType.S, STOCK, 70L, 20L);

        orderBook.addOrder(buyOrderRequest);
        orderBook.addOrder(sellOrderRequest);

        assertThat(orderBook.getBuyOrders()).containsExactly(buildOrder(1, buyOrderRequest));
        assertThat(orderBook.getSellOrders()).containsExactly(buildOrder(2, sellOrderRequest));
    }

    @Test
    public void cancelBuyOrder_shouldRemoveOrderWithGivenId() {
        final SeparateListOrderBook orderBook = new SeparateListOrderBook(STOCK);
        final OrderRequest buyOrderRequest = new OrderRequest(OrderType.B, STOCK, 100L, 30L);
        final OrderRequest sellOrderRequest = new OrderRequest(OrderType.S, STOCK, 70L, 20L);
        orderBook.addOrder(buyOrderRequest);
        orderBook.addOrder(sellOrderRequest);

        orderBook.cancelOrder(1);

        assertThat(orderBook.getBuyOrders()).isEmpty();
        assertThat(orderBook.getSellOrders()).containsExactly(buildOrder(2, sellOrderRequest));
    }

    @Test
    public void cancelSellOrder_shouldRemoveOrderWithGivenId() {
        final SeparateListOrderBook orderBook = new SeparateListOrderBook(STOCK);
        final OrderRequest buyOrderRequest = new OrderRequest(OrderType.B, STOCK, 100L, 30L);
        final OrderRequest sellOrderRequest = new OrderRequest(OrderType.S, STOCK, 70L, 20L);
        orderBook.addOrder(buyOrderRequest);
        orderBook.addOrder(sellOrderRequest);

        orderBook.cancelOrder(2);

        assertThat(orderBook.getBuyOrders()).containsExactly(buildOrder(1, buyOrderRequest));
        assertThat(orderBook.getSellOrders()).isEmpty();
    }

    @Test
    public void cancelOrderWithMultipleOrdersInList_shouldRemoveOrderWithGivenId() {
        final SeparateListOrderBook orderBook = new SeparateListOrderBook(STOCK);
        final OrderRequest buyOrderRequest_1 = new OrderRequest(OrderType.B, STOCK, 100L, 30L);
        final OrderRequest buyOrderRequest_2 = new OrderRequest(OrderType.B, STOCK, 100L, 30L);
        final OrderRequest buyOrderRequest_3 = new OrderRequest(OrderType.B, STOCK, 100L, 30L);
        final OrderRequest buyOrderRequest_4 = new OrderRequest(OrderType.B, STOCK, 100L, 30L);
        orderBook.addOrder(buyOrderRequest_1);
        orderBook.addOrder(buyOrderRequest_2);
        orderBook.addOrder(buyOrderRequest_3);
        orderBook.addOrder(buyOrderRequest_4);
        assertThat(orderBook.getBuyOrders()).hasSize(4);

        orderBook.cancelOrder(3);

        assertThat(orderBook.getBuyOrders()).containsExactly(
                buildOrder(1, buyOrderRequest_1),
                buildOrder(2, buyOrderRequest_2),
                buildOrder(4, buyOrderRequest_4));
    }

    @Test
    public void addMultipleOrdersInOneList_shouldStoreInSortedOrder() {
        final SeparateListOrderBook orderBook = new SeparateListOrderBook(STOCK);
        final OrderRequest buyOrderRequest_1 = new OrderRequest(OrderType.B, STOCK, 10L, 100L);
        final OrderRequest buyOrderRequest_2 = new OrderRequest(OrderType.B, STOCK, 10L, 70L);
        final OrderRequest buyOrderRequest_3 = new OrderRequest(OrderType.B, STOCK, 10L, 50L);
        final OrderRequest buyOrderRequest_4 = new OrderRequest(OrderType.B, STOCK, 10L, 50L);
        final OrderRequest buyOrderRequest_5 = new OrderRequest(OrderType.B, STOCK, 10L, 70L);
        final OrderRequest buyOrderRequest_6 = new OrderRequest(OrderType.B, STOCK, 10L, 75L);
        final OrderRequest buyOrderRequest_7 = new OrderRequest(OrderType.B, STOCK, 10L, 100L);
        final OrderRequest buyOrderRequest_8 = new OrderRequest(OrderType.B, STOCK, 10L, 110L);

        orderBook.addOrder(buyOrderRequest_1);
        orderBook.addOrder(buyOrderRequest_2);
        orderBook.addOrder(buyOrderRequest_3);
        orderBook.addOrder(buyOrderRequest_4);
        orderBook.addOrder(buyOrderRequest_5);
        orderBook.addOrder(buyOrderRequest_6);
        orderBook.addOrder(buyOrderRequest_7);
        orderBook.addOrder(buyOrderRequest_8);

        assertThat(orderBook.getBuyOrders())
                .containsExactly(
                        buildOrder(8, buyOrderRequest_8),
                        buildOrder(1, buyOrderRequest_1),
                        buildOrder(7, buyOrderRequest_7),
                        buildOrder(6, buyOrderRequest_6),
                        buildOrder(2, buyOrderRequest_2),
                        buildOrder(5, buyOrderRequest_5),
                        buildOrder(3, buyOrderRequest_3),
                        buildOrder(4, buyOrderRequest_4));
    }

    @Test
    public void isBalanced_shouldHaveDefaultValueFalse() {
        final SeparateListOrderBook orderBook = new SeparateListOrderBook(STOCK);

        assertThat(orderBook.isBalanced()).isFalse();
        assertThat(orderBook.isNotBalanced()).isTrue();
    }

    @Test
    public void isBalanced_shouldSetBalancedFalseWhenUpdated() {
        final SeparateListOrderBook orderBook = new SeparateListOrderBook(STOCK);
        orderBook.setBalanced();
        final OrderRequest buyOrderRequest = new OrderRequest(OrderType.B, STOCK, 100L, 30L);

        orderBook.addOrder(buyOrderRequest);

        assertThat(orderBook.isBalanced()).isFalse();
        assertThat(orderBook.isNotBalanced()).isTrue();
    }

    @Test
    public void isBalanced_shouldSetBalancedSuccessfully() {
        final SeparateListOrderBook orderBook = new SeparateListOrderBook(STOCK);

        orderBook.setBalanced();

        assertThat(orderBook.isBalanced()).isTrue();
        assertThat(orderBook.isNotBalanced()).isFalse();
    }

    private Order buildOrder(final long id, final OrderRequest orderRequest) {
        return new LimitOrder(id, orderRequest.getType(), STOCK,
                orderRequest.getPrice(), orderRequest.getQuantity());
    }
}
