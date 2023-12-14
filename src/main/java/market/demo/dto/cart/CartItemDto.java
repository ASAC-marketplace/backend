package market.demo.dto.cart;

import lombok.Getter;
import lombok.Setter;
import market.demo.domain.item.Item;
import market.demo.domain.order.CartItem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    public CartItemDto(@NotNull CartItem cartItem){
        Item item = cartItem.getItem();

        this.itemId = item.getId();
        this.itemName = item.getName();
        this.discountRate = item.getDiscountRate();
        this.itemPrice = item.getItemPrice();
        this.itemCount = cartItem.getQuantity();
        this.totalPrice = item.getItemPrice() * cartItem.getQuantity();
        this.salePrice = (int) (this.getTotalPrice() * calDiscountPercentage(item));
        this.promotionImageUrl = item.getItemDetail().getPromotionImageUrl();
    }

    @Contract(pure = true)
    private long calDiscountPercentage(@NotNull Item item){
        return (long) ((100 - item.getDiscountRate()) * 0.01);
    }

}
