package market.demo.domain.search;

import lombok.Data;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;

import java.time.LocalDate;
import java.util.List;

@Data
public class ItemSearchCondition {
    private String name;
    private Long categoryId;
    private ItemStatus status;
    private PromotionType promotionType;
    private Integer minStockQuantity;
    private LocalDate minRegisteredDate;
    private Integer minDiscountRate;
    private Integer maxDiscountRate;
    private Integer minPrice;
    private Integer maxPrice;

}
