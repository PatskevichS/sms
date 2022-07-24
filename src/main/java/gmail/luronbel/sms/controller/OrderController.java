package gmail.luronbel.sms.controller;


import gmail.luronbel.sms.dto.OrderRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Provides basic functionality to operate orders.
 */
@Api(tags = "Order")
@RequestMapping("order")
public interface OrderController {

    @ApiOperation(value = "Add new order", notes = "Returns order id")
    @PostMapping(value = "add")
    long addOrder(OrderRequest request);

    @ApiOperation(value = "Cancel order", notes = "Cancels order with provided id")
    @PostMapping(value = "cancel")
    void cancelOrder(Long id, String orderBookSymbol);
}
