package market.demo.service.payment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.etc.Delivery;
import market.demo.domain.member.Member;
import market.demo.domain.order.Cart;
import market.demo.domain.order.CartItem;
import market.demo.domain.order.Order;
import market.demo.domain.order.Payment;
import market.demo.domain.status.DeliveryStatus;
import market.demo.domain.status.OrderStatus;
import market.demo.domain.type.PaymentStatus;
import market.demo.dto.payment.PaymentRequestDto;
import market.demo.dto.payment.PaymentResponseDto;
import market.demo.exception.InvalidOrderException;
import market.demo.exception.OrderNotFoundException;
import market.demo.exception.PaymentGatewayException;
import market.demo.exception.PaymentProcessingException;
import market.demo.repository.DeliveryRepository;
import market.demo.repository.ItemRepository;
import market.demo.repository.OrderRepository;
import market.demo.repository.PaymentRepository;
import market.demo.service.CartService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ExternalPaymentGateway externalPaymentGateway;
    private final DeliveryRepository deliveryRepository;
    private final CartService cartService;
    private final StockService stockService;

    public PaymentResponseDto initiatePayment(PaymentRequestDto request) throws PaymentGatewayException, PaymentProcessingException {
        // 주문 검증
        Order order = validateOrderDetails(request.getOrderId(), request.getTotalPrice());

        // 결제 객체 생성 및 저장
        Payment payment = createAndSavePayment(order, request);

        // 외부 결제 게이트웨이를 통한 결제 요청
        PaymentResponseDto gatewayResponse = externalPaymentGateway.processPayment(payment);

        if (gatewayResponse.isSuccess()) {
            completePayment(payment, order);

            // 결제 성공에 대한 응답 생성
            return PaymentResponseDto.success(gatewayResponse.getTransactionId(), PaymentStatus.COMPLETED);
        } else {
            cancelPayment(payment);

            // 결제 실패에 대한 응답 생성
            return PaymentResponseDto.failure(gatewayResponse.getErrorCode(), gatewayResponse.getErrorMessage());
        }
    }

    private void completePayment(Payment payment, Order order) {
        // 결제 완료 처리
        payment.completePayment(payment.getTransactionId());

        // 주문 상태를 '지불됨'으로 변경
        order.markAsPaid();

        // 결제 정보와 주문 정보 저장
        paymentRepository.save(payment);
        orderRepository.save(order);

        // 재고 감소 처리
        stockService.reduceStock(order.getId());

        // 배송 상태 업데이트
        updateDeliveryStatus(order);

        // 멤버의 장바구니 비우기
        clearMemberCart(order);
    }

    private Payment createAndSavePayment(Order order, PaymentRequestDto request) {
        Payment payment = new Payment(
                order,
                request.getTotalPrice(),
                request.getPaymentMethod()
        );
        paymentRepository.save(payment);
        return payment;
    }

    private void updateDeliveryStatus(Order order) {
        Delivery delivery = order.getDelivery();
        if (delivery != null) {
            delivery.updateDeliveryStatus(DeliveryStatus.READY);
            deliveryRepository.save(delivery);
        }
    }

    private void cancelPayment(Payment payment) {
        // 결제 상태를 '취소됨'으로 변경
        payment.updatePaymentStatus(PaymentStatus.CANCEL);

        // 결제 정보 저장
        paymentRepository.save(payment);
    }

    private void clearMemberCart(Order order) {
        Member member = order.getMember();
        cartService.clearCart(member.getLoginId());
    }

    private Order validateOrderDetails(Long orderId, double paymentAmount)
            throws OrderNotFoundException, InvalidOrderException, PaymentProcessingException {

        // 주문 ID 검증
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("유효하지 않은 주문 ID: " + orderId));

        // 주문 상태 검증
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderException("결제가 불가능한 주문 상태입니다.");
        }

        // 결제 금액 검증
        if (order.getTotalAmount() + 3000 != paymentAmount) {
            throw new PaymentProcessingException("주문 금액과 결제 금액이 일치하지 않습니다.");
        }

        return order;
    }
}
