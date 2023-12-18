package market.demo.service.payment;

import market.demo.domain.order.Payment;
import market.demo.domain.type.PaymentStatus;
import market.demo.dto.payment.PaymentRequestDto;
import market.demo.dto.payment.PaymentResponseDto;
import org.springframework.stereotype.Service;

@Service
public class ExternalPaymentGateway {
    public PaymentResponseDto processPayment(Payment payment) {
        String fakeTransactionId = "TRANS_" + System.currentTimeMillis();
        return PaymentResponseDto.success(fakeTransactionId, PaymentStatus.COMPLETED);
    }
}
