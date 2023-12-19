package market.demo.dto.mypage;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.order.Order;
import market.demo.domain.status.DeliveryStatus;
import market.demo.domain.status.PaymentMethod;

import java.time.LocalDateTime;

@Data
public class MyOrderDto {
    private LocalDateTime orderDateTime;
    private String itemName;
    private Long orderId;
    private Long totalAmount;
    private DeliveryStatus deliveryStatus;
    private PaymentMethod paymentMethod;

    public MyOrderDto(Order order){
        this.orderDateTime = order.getOrderDateTime();
        this.totalAmount = order.getTotalAmount();
        this.orderId = order.getId();
        this.paymentMethod = order.getPayment().getPaymentMethod();
        this.deliveryStatus = order.getDelivery().getDeliveryStatus();
        this.itemName = order.getOrderItems().get(0).getItem().getName();
    }

}