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

    public PaymentResponseDto initiatePayment(PaymentRequestDto request) throws PaymentProcessingException {
        Order order = getOrder(request.getOrderId());
        Payment payment = createPayment(order, request);

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderException("결제가 불가능한 주문 상태입니다.");
        }

        PaymentResponseDto response = processExternalPayment(request, payment);
        if (!response.isSuccess() || !Objects.equals(request.getTotalPrice(), order.getTotalAmount())) {
            throw new PaymentProcessingException("결제 처리 중 오류 발생");
        }
        handlePostPaymentProcessing(payment, response);
        return response;
    }

    private Order getOrder(Long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));
    }

    private Payment createPayment(Order order, PaymentRequestDto request) {
        Payment payment = new Payment(order, request.getTotalPrice(), PaymentStatus.PENDING, request.getPaymentMethod());
        paymentRepository.save(payment);
        return payment;
    }

    private PaymentResponseDto processExternalPayment(PaymentRequestDto request, Payment payment) {
        PaymentResponseDto gatewayResponse = externalPaymentGateway.processPayment(request);
        if (gatewayResponse.isSuccess()) {
            payment.completePayment(gatewayResponse.getTransactionId());
            payment.getOrder().markAsPaid();
            return PaymentResponseDto.success(payment.getTransactionId(), PaymentStatus.COMPLETED);
        } else {
            payment.updatePaymentStatus(PaymentStatus.CANCEL);
            return PaymentResponseDto.failure(gatewayResponse.getErrorCode(), gatewayResponse.getErrorMessage());
        }
    }

    private void handlePostPaymentProcessing(Payment payment, PaymentResponseDto response) {
        if (response.isSuccess()) {
            updateDeliveryStatus(payment.getOrder());
            clearMemberCart(payment.getOrder());
            stockService.reduceStock(payment.getOrder().getId());
        }
        paymentRepository.save(payment);
        orderRepository.save(payment.getOrder());
    }

    private void updateDeliveryStatus(Order order) {
        Delivery delivery = order.getDelivery();
        if (delivery != null) {
            delivery.updateDeliveryStatus(DeliveryStatus.READY);
            deliveryRepository.save(delivery);
        }
    }

    private void clearMemberCart(Order order) {
        Member member = order.getMember();
        cartService.clearCart(member.getLoginId());
    }
}
