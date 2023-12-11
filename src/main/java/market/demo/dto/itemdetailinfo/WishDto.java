package market.demo.dto.itemdetailinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishDto {
    private Long itemId;
    private String itemName;
    private Integer itemTotalPrice;
    private Integer saleItemPrice;
    private Integer discountRate;
    private String promotionImageUrl;
}
