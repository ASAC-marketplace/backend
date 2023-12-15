package market.demo.dto.mypage;

import lombok.Data;
import market.demo.domain.order.Order;
import market.demo.domain.status.DeliveryStatus;
import market.demo.domain.status.PaymentMethod;
import market.demo.dto.order.OrderItemDto;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MyOrderDetailDto {
    private LocalDateTime orderDateTime;
    private String itemName;
    private Long orderId;
    private Long totalAmount;
    private DeliveryStatus deliveryStatus;
    private PaymentMethod paymentMethod;
    private List<OrderItemDto> orderItemDtos = new ArrayList<>();

    public MyOrderDetailDto(Order order) {
        this.totalAmount = order.getTotalAmount();
        this.orderId = order.getId();
        this.paymentMethod = order.getPayment().getPaymentMethod();
        this.deliveryStatus = order.getDelivery().getDeliveryStatus();
        this.itemName = order.getOrderItems().get(0).getItem().getName();
        this.orderDateTime = order.getOrderDateTime();
        this.orderItemDtos = createOrderItemDto(order);
    }

    private @NotNull List<OrderItemDto> createOrderItemDto(@NotNull Order order) {
        return order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
    }
}
