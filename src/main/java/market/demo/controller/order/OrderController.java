package market.demo.controller.order;

import lombok.RequiredArgsConstructor;
import market.demo.dto.payment.PaymentRequestDto;
import market.demo.dto.payment.PaymentResponseDto;
import market.demo.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PaymentService paymentService;

    //결제 api
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDto> initiatePayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto response = paymentService.initiatePayment(paymentRequestDto);
        return ResponseEntity.ok(response);
    }
}
