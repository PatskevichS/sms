package gmail.luronbel.sms.component.impl;

import gmail.luronbel.sms.component.OrderBookHolder;
import gmail.luronbel.sms.entity.BalancedOrderBook;
import gmail.luronbel.sms.entity.impl.SeparateListOrderBook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * Simple implementation of {@link OrderBookHolder} that creates order books per each code read from properties.
 */
@Component
public class InMemoryOrderBookHolder implements OrderBookHolder {

    private final List<BalancedOrderBook> orderBooks;

    public InMemoryOrderBookHolder(@Value("${order-books}") final String[] orderBooksSymbols) {
        orderBooks = Arrays.stream(orderBooksSymbols)
                .map(SeparateListOrderBook::new)
                .collect(toUnmodifiableList());
    }

    @Override
    public List<BalancedOrderBook> getAllOrderBooks() {
        return orderBooks;
    }

    @Override
    public Map<String, BalancedOrderBook> getAllOrderBooksBySymbol() {
        return orderBooks
                .stream()
                .collect(Collectors.toMap(BalancedOrderBook::getSymbol,
                        Function.identity()));
    }

}
