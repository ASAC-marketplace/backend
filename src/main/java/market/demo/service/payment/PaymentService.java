package market.demo.service.payment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.etc.Delivery;
import market.demo.domain.order.Order;
import market.demo.domain.order.Payment;
import market.demo.domain.status.DeliveryStatus;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ExternalPaymentGateway externalPaymentGateway;
    private final DeliveryRepository deliveryRepository;
    private final ItemRepository itemRepository;
    private final StockService stockService;

    private Order validateOrder(Long orderId) throws OrderNotFoundException, InvalidOrderException {
        log.info("Validating order with ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));

    }

    public PaymentResponseDto initiatePayment(PaymentRequestDto request) throws PaymentProcessingException {

        log.info("Initiating payment for order ID: {}", request.getOrderId());
        //주문 검증
        Order order = validateOrder(request.getOrderId());

        //결제 객체 생성
        Payment payment = new Payment(
                order,
                request.getTotalPrice(),
                PaymentStatus.PENDING,
                request.getPaymentMethod()
        );
        log.info("Saving payment object for order ID: {}", order.getId());

        //결제 객체 저장
        paymentRepository.save(payment);

        try {
            //외부 결제 게이트웨이를 통한 결제 요청
            //결제 메소드
            log.info("Processing payment through external gateway for order ID: {}", order.getId());
            PaymentResponseDto gatewayResponse = externalPaymentGateway.processPayment(request);

            if (gatewayResponse.isSuccess()) {
                log.info("Payment successful for order ID: {}", order.getId());
                payment.completePayment(gatewayResponse.getTransactionId());
                log.info("target1 ID: {}", order.getId());
                order.markAsPaid();
                log.info("target2 ID: {}", order.getId());
                paymentRepository.save(payment);
                log.info("target3 ID: {}", order.getId());
                orderRepository.save(order);
                log.info("target4 ID: {}", order.getId());
                stockService.reduceStock(order.getId());
                log.info("target5 ID: {}", order.getId());


                //배송 상태 업데이트
                Delivery delivery = order.getDelivery();
                log.info("target6 ID: {}", order.getId());
                if (delivery != null) {
                    log.info("Updating delivery status for order ID: {}", order.getId());
                    delivery.updateDeliveryStatus(DeliveryStatus.READY);
                    deliveryRepository.save(delivery);
                }
                //결제 성공에 대한 응답 생성
                return PaymentResponseDto.success(payment.getTransactionId(), PaymentStatus.COMPLETED);
            } else {
                log.warn("Payment failed for order ID: {}", order.getId());
                payment.updatePaymentStatus(PaymentStatus.CANCEL);
                paymentRepository.save(payment);

                //결제 실패에 대한 응답 생성
                return PaymentResponseDto.failure(gatewayResponse.getErrorCode(), gatewayResponse.getErrorMessage());
            }
        } catch (PaymentGatewayException e) {
            log.error("Error processing payment for order ID: {}", order.getId(), e);
            payment.updatePaymentStatus(PaymentStatus.ERROR);
            paymentRepository.save(payment);
            throw new PaymentProcessingException("결제 처리 중 오류가 발생했습니다.", e);
        }
    }
}
