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
    private final StockService stockService;

    private Order validateOrder(Long orderId) throws OrderNotFoundException, InvalidOrderException {
        log.info("Validating order with ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));
    }

    public PaymentResponseDto initiatePayment(PaymentRequestDto request) throws PaymentProcessingException {
        //주문 검증
        Order order = validateOrder(request.getOrderId());

        //결제 객체 생성
        Payment payment = new Payment(
                order,
                request.getTotalPrice(),
                PaymentStatus.PENDING,
                request.getPaymentMethod()
        );

        //결제 객체 저장
        paymentRepository.save(payment);

        try {
            //외부 결제 게이트웨이를 통한 결제 요청
            //결제 메소드
            PaymentResponseDto gatewayResponse = externalPaymentGateway.processPayment(request);

            if (gatewayResponse.isSuccess()) {
                payment.completePayment(gatewayResponse.getTransactionId());
                order.markAsPaid();
                paymentRepository.save(payment);
                orderRepository.save(order);
                stockService.reduceStock(order.getId());

                //배송 상태 업데이트
                Delivery delivery = order.getDelivery();
                if (delivery != null) {
                    delivery.updateDeliveryStatus(DeliveryStatus.READY);
                    deliveryRepository.save(delivery);
                }
                //결제 성공에 대한 응답 생성
                return PaymentResponseDto.success(payment.getTransactionId(), PaymentStatus.COMPLETED);
            } else {
                payment.updatePaymentStatus(PaymentStatus.CANCEL);
                paymentRepository.save(payment);

                //결제 실패에 대한 응답 생성
                return PaymentResponseDto.failure(gatewayResponse.getErrorCode(), gatewayResponse.getErrorMessage());
            }
        } catch (PaymentGatewayException e) {
            payment.updatePaymentStatus(PaymentStatus.ERROR);
            paymentRepository.save(payment);
            throw new PaymentProcessingException("결제 처리 중 오류가 발생했습니다.", e);
        }
    }
}
