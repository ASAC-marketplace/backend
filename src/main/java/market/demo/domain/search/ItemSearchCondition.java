package market.demo.domain.search;

import lombok.Data;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;

import java.time.LocalDate;
import java.util.List;

@Data
public class ItemSearchCondition {
    private String name;
    private List<String> categoryName; // 카테고리 이름 리스트
    private List<String> brand; // 브랜드 이름 리스트
    private List<ItemStatus> status; // 상태 리스트
    private List<PromotionType> promotionType; // 프로모션 타입 리스트
    private Integer minStockQuantity;
    private LocalDate maxRegisteredDate;
    private LocalDate minRegisteredDate;
    private Integer minDiscountRate;
    private Integer maxDiscountRate;
    private Integer minPrice;
    private Integer maxPrice;
    private String priceRange; // 가격 구간을 나타내는 필드 추가
}
