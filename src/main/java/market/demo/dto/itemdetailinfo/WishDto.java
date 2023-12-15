package market.demo.dto.itemdetailinfo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.item.Item;

@Data
public class WishDto {
    private Long itemId;
    private String itemName;
    private Integer itemTotalPrice;
    private Integer saleItemPrice;
    private Integer discountRate;
    private String promotionImageUrl;

    public WishDto(Item item){
        this.itemId = item.getId();
        this.itemName = item.getName();
        this.itemTotalPrice = item.getItemPrice();
        this.discountRate = item.getDiscountRate();
        this.saleItemPrice = (int) (item.getItemPrice() * (item.getDiscountRate() * 0.01));
        this.promotionImageUrl = item.getItemDetail().getPromotionImageUrl();
    }
}
