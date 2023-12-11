package market.demo.domain.search;

import lombok.Data;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;

import java.time.LocalDate;

@Data
public class ItemSearchCondition {
    private String name;
    private String categoryName;
    private String brand;
    private ItemStatus status;
    private PromotionType promotionType;
    private Integer minStockQuantity;
    private LocalDate registerDate;
    private LocalDate maxRegisteredDate;
    private LocalDate minRegisteredDate;
    private Integer minDiscountRate;
    private Integer maxDiscountRate;
    private Integer minPrice;
    private Integer maxPrice;
}
