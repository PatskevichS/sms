package gmail.luronbel.sms.controller;

import gmail.luronbel.sms.component.TradingGateway;
import gmail.luronbel.sms.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of {@link OrderController}.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {

    private final TradingGateway tradingGateway;

    @Override
    public long addOrder(@RequestBody final OrderRequest request) {
        log.info("Received add order request: " + request);
        return tradingGateway.addOrder(request);
    }

    @Override
    public void cancelOrder(@RequestParam(name = "id") final Long orderId,
                            @RequestParam(name = "symbol") final String orderBookSymbol) {
        log.info("Received cancel order request: id=" + orderId + " symbol=" + orderBookSymbol);
        tradingGateway.cancelOrder(orderId, orderBookSymbol);
    }
}
