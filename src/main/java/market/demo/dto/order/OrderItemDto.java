package market.demo.dto.order;

import lombok.Getter;
import lombok.Setter;
import market.demo.domain.order.Order;
import market.demo.domain.order.OrderItem;

@Getter
@Setter
public class OrderItemDto {
    private Long itemId;
    private String itemName;
    private Integer itemPrice;//가격
    private Integer itemCount;//개수
    private Integer discountRate;//할인율

    public OrderItemDto(OrderItem orderItem){
        this.itemCount = orderItem.getOrderCount();
        this.itemId = orderItem.getItem().getId();
        this.itemPrice = orderItem.getItem().getItemPrice();
        this.itemName = orderItem.getItem().getName();
        this.discountRate = orderItem.getItem().getDiscountRate();
    }
}
