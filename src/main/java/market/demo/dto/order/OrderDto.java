package market.demo.dto.order;

import lombok.Getter;
import lombok.Setter;
import market.demo.domain.member.Address;
import market.demo.domain.member.Member;
import market.demo.domain.order.Cart;
import market.demo.domain.order.Order;
import market.demo.dto.cart.CartItemDto;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long orderId;
    private Long amount; // 전체 금액
    private Long salesTotalAmount; // 할인 금액
    private Long totalAmount;// 할인 된 총 금액
    private String memberName;
    private String phoneNumber;
    private Address address;
    private List<OrderItemDto> orderItemDtos = new ArrayList<>();

    public OrderDto(@NotNull Order order, @NotNull Member member, @NotNull Cart cart){
        this.orderId = order.getId();
        this.memberName = member.getMemberName();
        this.amount = cart.getAmount();
        this.address = order.getDelivery().getAddress();
        this.phoneNumber = member.getPhoneNumber();
        this.salesTotalAmount = cart.getSalesTotalAmount();
        this.totalAmount = cart.getTotalAmount();
        this.orderItemDtos = setOrderItemDtos(order);
    }

    public List<OrderItemDto> setOrderItemDtos(@NotNull Order order){
        order.getOrderItems().forEach(orderItem -> {
            orderItemDtos.add(new OrderItemDto(orderItem));
        });

        return orderItemDtos;
    }
}
