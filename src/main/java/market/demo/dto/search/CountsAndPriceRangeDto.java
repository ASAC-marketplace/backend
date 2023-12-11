package market.demo.dto.search;

import lombok.Data;
import market.demo.domain.type.PromotionType;

import java.util.List;
import java.util.Map;

@Data
public class CountsAndPriceRangeDto {
    private final Map<String, Long> categoryCounts;
    private final Map<String, Long> brandCounts;
    private final Map<PromotionType, Long> promotionCounts;
    private final List<String> priceRange;

    public CountsAndPriceRangeDto(Map<String, Long> categoryCounts,
                                  Map<String, Long> brandCounts,
                                  Map<PromotionType, Long> promotionCounts,
                                  List<String> priceRange) {
        this.categoryCounts = categoryCounts;
        this.brandCounts = brandCounts;
        this.promotionCounts = promotionCounts;
        this.priceRange = priceRange;
    }
}
