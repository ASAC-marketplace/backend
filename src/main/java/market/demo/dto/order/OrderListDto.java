package market.demo.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderListDto {
    private Long orderId;
    private Long amount;
    private Long salesTotalAmount;
    private Long totalAmount;
    private List<OrderItemDto> orderItemDtos = new ArrayList();


}
