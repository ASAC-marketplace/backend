package market.demo.dto.payment;

import lombok.Data;
import market.demo.domain.status.PaymentMethod;

@Data
public class PaymentRequestDto {
    private Long memberId;
    private Long orderId;
    private Long totalPrice;
    private PaymentMethod paymentMethod;
}
