package market.demo.dto.search;

import lombok.Data;
import market.demo.domain.type.PromotionType;

import java.util.List;
import java.util.Map;

@Data
public class CountsAndPriceRangeNoCategoryDto {
    private final Map<String, Long> brandCounts;
    private final Map<PromotionType, Long> promotionCounts;
    private final List<String> priceRange;

    public CountsAndPriceRangeNoCategoryDto(Map<String, Long> brandCounts, Map<PromotionType, Long> promotionCounts, List<String> priceRange) {
        this.brandCounts = brandCounts;
        this.promotionCounts = promotionCounts;
        this.priceRange = priceRange;
    }
}
