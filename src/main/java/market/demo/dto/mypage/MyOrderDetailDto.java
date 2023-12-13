package market.demo.dto.mypage;

import lombok.Getter;
import lombok.Setter;
import market.demo.domain.status.DeliveryStatus;
import market.demo.domain.status.PaymentMethod;
import market.demo.dto.order.OrderItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MyOrderDetailDto {
    private LocalDateTime orderDateTime;
    private String itemName;
    private Long orderId;
    private Long totalAmount;
    private DeliveryStatus deliveryStatus;
    private PaymentMethod paymentMethod;
    private List<OrderItemDto> orderItemDtos = new ArrayList<>();
}
