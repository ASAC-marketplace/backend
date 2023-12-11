package market.demo.dto.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
    private Long itemId;
    private String itemName;
    private Integer itemPrice;//가격
    private Integer itemCount;//개수
    private Integer totalPrice;//총가격
    private Integer salePrice;//할인 적용된 가격
    private Integer discountRate;//할인율
    private String promotionImageUrl;
}
