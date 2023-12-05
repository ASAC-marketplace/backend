package market.demo.dto.search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;

import java.time.LocalDate;

@Data
public class ItemSearchDto {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private ItemStatus status;
    private PromotionType promotionType;
    private Integer stockQuantity;
    private LocalDate registeredDate;
    private Integer discountRate;
    private Integer itemPrice;

    @QueryProjection
    public ItemSearchDto(Long id, String name, Long categoryId, String categoryName, ItemStatus status, PromotionType promotionType, Integer stockQuantity, LocalDate registeredDate, Integer discountRate, Integer itemPrice) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.status = status;
        this.promotionType = promotionType;
        this.stockQuantity = stockQuantity;
        this.registeredDate = registeredDate;
        this.discountRate = discountRate;
        this.itemPrice = itemPrice;
    }
}
