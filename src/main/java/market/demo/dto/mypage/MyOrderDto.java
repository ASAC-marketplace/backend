package market.demo.dto.mypage;

import lombok.Getter;
import lombok.Setter;
import market.demo.domain.status.DeliveryStatus;
import market.demo.domain.status.PaymentMethod;

import java.time.LocalDateTime;

@Getter
@Setter
public class MyOrderDto {
    private LocalDateTime orderDateTime;
    private String itemName;
    private Long orderId;
    private Long totalAmount;
    private DeliveryStatus deliveryStatus;
    private PaymentMethod paymentMethod;


}
