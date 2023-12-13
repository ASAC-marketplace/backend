package market.demo.dto.order;

import lombok.Getter;
import lombok.Setter;
import market.demo.domain.member.Address;
import market.demo.dto.cart.CartItemDto;

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
}
