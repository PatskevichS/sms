package gmail.luronbel.sms.entity.impl;

import gmail.luronbel.sms.entity.OrderType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(JUnit4.class)
public class LimitOrderTest {

    @Test
    public void addExecutedQuantityLessThanRequired_shouldAddSuccessfully() {
        final LimitOrder limitOrder = getOrder(50);

        limitOrder.addExecutedQuantity(20);

        assertThat(limitOrder.getExecutedQuantity()).isEqualTo(20);
        assertThat(limitOrder.getRequiredQuantity()).isEqualTo(30);
    }

    @Test
    public void addExecutedQuantityLessThanRequiredMultipleTimes_shouldAddSuccessfully() {
        final LimitOrder limitOrder = getOrder(50);

        limitOrder.addExecutedQuantity(20);
        limitOrder.addExecutedQuantity(10);
        limitOrder.addExecutedQuantity(20);

        assertThat(limitOrder.getExecutedQuantity()).isEqualTo(50);
        assertThat(limitOrder.getRequiredQuantity()).isZero();
    }

    @Test
    public void addExecutedQuantityEqualThanRequired_shouldAddSuccessfully() {
        final LimitOrder limitOrder = getOrder(50);

        limitOrder.addExecutedQuantity(50);

        assertThat(limitOrder.getExecutedQuantity()).isEqualTo(50);
        assertThat(limitOrder.getRequiredQuantity()).isZero();
    }

    @Test
    public void addExecutedQuantityMoreThanRequired_shouldThrowException() {
        final LimitOrder limitOrder = getOrder(20);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> limitOrder.addExecutedQuantity(50));

        assertThat(exception.getMessage())
                .isEqualTo("Required quantity is less then processed for order with id " + limitOrder.getId());
    }

    private static LimitOrder getOrder(final long requiredQuantity) {
        return new LimitOrder(1, OrderType.B, "stock", 1, requiredQuantity);
    }
}
