package market.demo.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
    private Long itemId;
    private String itemName;
    private Integer itemPrice;//가격
    private Integer itemCount;//개수
    private Integer discountRate;//할인율
}
