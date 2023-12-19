package market.demo.dto.item;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ItemMainEndDto {
    private Long id;
    private String name;
    private String brand;
    private Integer discountRate;
    private Integer discountedPrice;
    private Integer itemPrice;
    private String promotionImageUrl;
    private Long reviewCount;

    @QueryProjection
    public ItemMainEndDto(Long id, String name, String brand, Integer discountRate, Integer discountedPrice, Integer itemPrice, String promotionImageUrl, Long reviewCount) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.discountRate = discountRate;
        this.discountedPrice = discountedPrice;
        this.itemPrice = itemPrice;
        this.promotionImageUrl = promotionImageUrl;
        this.reviewCount = reviewCount;
    }
}
