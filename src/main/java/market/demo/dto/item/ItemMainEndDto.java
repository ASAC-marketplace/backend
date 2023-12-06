package market.demo.dto.item;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ItemMainEndDto {
    private Long itemId;
    private String name;
    private Integer discountRate;
    private Integer discountedPrice;
    private Integer originalPrice;
    private String promotionImageUrl;
    private Long reviewCount;

    @QueryProjection
    public ItemMainEndDto(Long itemId, String name, Integer discountRate, Integer discountedPrice, Integer originalPrice, String promotionImageUrl, Long reviewCount) {
        this.itemId = itemId;
        this.name = name;
        this.discountRate = discountRate;
        this.discountedPrice = discountedPrice;
        this.originalPrice = originalPrice;
        this.promotionImageUrl = promotionImageUrl;
        this.reviewCount = reviewCount;
    }
}
