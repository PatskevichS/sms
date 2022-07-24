package gmail.luronbel.sms.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class ChronologicalInMemoryTradeLedgerTest {

    private static final String ORDER_BOOK_SYMBOL = "TEST";

    @Test
    public void recordTrade_shouldKeepRecordsInChronicleOrder() {
        final ChronologicalInMemoryTradeLedger tradeLedger = new ChronologicalInMemoryTradeLedger();

        tradeLedger.recordTrade(ORDER_BOOK_SYMBOL, 1L, 2L, 100L, 35L);
        tradeLedger.recordTrade(ORDER_BOOK_SYMBOL, 4L, 3L, 80L, 40L);
        tradeLedger.recordTrade(ORDER_BOOK_SYMBOL, 10L, 8L, 110L, 22L);
        tradeLedger.recordTrade(ORDER_BOOK_SYMBOL, 7L, 9L, 70L, 64L);
        tradeLedger.recordTrade(ORDER_BOOK_SYMBOL, 11L, 12L, 20L, 15L);

        assertThat(tradeLedger.getRecords()).containsExactly(
                buildRecord(1L, ORDER_BOOK_SYMBOL, 1L, 2L, 100L, 35L),
                buildRecord(2L, ORDER_BOOK_SYMBOL, 4L, 3L, 80L, 40L),
                buildRecord(3L, ORDER_BOOK_SYMBOL, 10L, 8L, 110L, 22L),
                buildRecord(4L, ORDER_BOOK_SYMBOL, 7L, 9L, 70L, 64L),
                buildRecord(5L, ORDER_BOOK_SYMBOL, 11L, 12L, 20L, 15L));
    }

    private ChronologicalInMemoryTradeLedger.Record buildRecord(final long id, final String orderBookSymbol,
                                                                final Long orderBuyId, final Long orderSellId,
                                                                final Long quantity, final Long price) {
        return new ChronologicalInMemoryTradeLedger.Record(id, orderBookSymbol, orderBuyId, orderSellId, quantity,
                price);
    }
}
