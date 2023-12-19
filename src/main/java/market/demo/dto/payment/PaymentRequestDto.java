package market.demo.dto.payment;

import lombok.Data;
import market.demo.advice.MemberIdAwardDto;
import market.demo.domain.status.PaymentMethod;

@Data
public class PaymentRequestDto implements MemberIdAwardDto {
    private Long memberId;
    private Long orderId;
    private Long totalPrice;
    private PaymentMethod paymentMethod;
}
