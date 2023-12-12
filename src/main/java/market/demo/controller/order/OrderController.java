package market.demo.controller.order;

import lombok.RequiredArgsConstructor;
import market.demo.dto.order.OrderDto;
import market.demo.dto.payment.PaymentRequestDto;
import market.demo.dto.payment.PaymentResponseDto;
import market.demo.service.OrderService;
import market.demo.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    //결제 api
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDto> initiatePayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto response = paymentService.initiatePayment(paymentRequestDto);
        return ResponseEntity.ok(response);
    }

    //19 주문 api
    @GetMapping("/{loginId}")
    public ResponseEntity<OrderDto> showOrder(@PathVariable("loginId") String loginId){
        return ResponseEntity.ok(orderService.showOrCreateOrder(loginId));
    }
}
