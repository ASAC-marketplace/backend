package market.demo.dto.search;

import lombok.Data;
import market.demo.domain.type.PromotionType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Data
public class ItemSearchResponse {
    private final Page<ItemSearchDto> items;
    private final Map<String, Long> categoryCounts;
    private final Map<String, Long> brandCounts;
    private final Map<PromotionType, Long> promotionCounts;
    private final List<String> priceRange;

    public ItemSearchResponse(Page<ItemSearchDto> items, Map<String, Long> categoryCounts, Map<String, Long> brandCounts,  Map<PromotionType, Long> promotionCounts, List<String> priceRange) {
        this.items = items;
        this.categoryCounts = categoryCounts;
        this.brandCounts = brandCounts;
        this.promotionCounts = promotionCounts;
        this.priceRange = priceRange;
    }
}
