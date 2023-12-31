package market.demo.controller.order;

import lombok.RequiredArgsConstructor;
import market.demo.domain.member.jwt.TokenProvider;
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
    private final TokenProvider tokenProvider;

    //결제 api
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDto> initiatePayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        // totalPrice에 3000원 추가
        Long updatedTotalPrice = paymentRequestDto.getTotalPrice() + 3000;
        paymentRequestDto.setTotalPrice(updatedTotalPrice);

        PaymentResponseDto response = paymentService.initiatePayment(paymentRequestDto);
        return ResponseEntity.ok(response);
    }


    //19 주문 api
    @GetMapping()
    public ResponseEntity<OrderDto> showOrder(){
        String loginId = tokenProvider.getLoginIdFromCurrentRequest(); // JWT에서 loginId 추출
        return ResponseEntity.ok(orderService.showOrCreateOrder(loginId));
    }
}
