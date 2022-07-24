package gmail.luronbel.sms.dto;

import gmail.luronbel.sms.entity.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO that represents user request to add an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private OrderType type;
    private String stock;
    private Long quantity;
    private Long price;
}
