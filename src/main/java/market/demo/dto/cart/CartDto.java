package market.demo.dto.cart;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CartDto {
    private Long cartId;
    private Long amount; // 전체 금액
    private Long salesTotalAmount; // 할인 금액
    private Long totalAmount;// 할인 된 총 금액
    private List<CartItemDto> cartItemDtos = new ArrayList();
}
