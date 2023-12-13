package market.demo.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import market.demo.domain.type.PaymentStatus;

@Data
@AllArgsConstructor
public class PaymentResponseDto {
    private boolean success;
    private String transactionId;
    private PaymentStatus paymentStatus;
    private String errorCode;
    private String errorMessage;

    public static PaymentResponseDto success(String transactionId, PaymentStatus paymentStatus) {
        return new PaymentResponseDto(true, transactionId, paymentStatus, null, null);
    }




    public static PaymentResponseDto failure(String errorCode, String errorMessage) {
        return new PaymentResponseDto(false, null, null, errorCode, errorMessage);
    }
}
