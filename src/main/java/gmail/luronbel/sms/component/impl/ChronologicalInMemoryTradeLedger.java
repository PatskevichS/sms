package gmail.luronbel.sms.component.impl;

import gmail.luronbel.sms.component.TradeLedger;
import gmail.luronbel.sms.entity.Trade;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link TradeLedger} that holds all trade in chronological order.
 */
@Data
@Service
@NoArgsConstructor
public class ChronologicalInMemoryTradeLedger implements TradeLedger {

    private long idGenerator = 0;

    @Data
    static final class Record implements Trade {
        private final Long id;
        private final String orderBookSymbol;
        private final Long orderBuyId;
        private final Long orderSellId;
        private final Long quantity;
        private final Long price;
    }

    private final List<Record> records = new ArrayList<>();

    @Override
    public long recordTrade(final String orderBookSymbol, final Long orderBuyId, final Long orderSellId,
                            final Long quantity, final Long price) {
        final Record newRecord = new Record(++idGenerator, orderBookSymbol, orderBuyId, orderSellId, quantity, price);
        records.add(newRecord);
        return newRecord.getId();
    }

    List<Record> getRecords() {
        return records;
    }
}
