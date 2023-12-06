package market.demo.dto.search;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Map;

@Data
public class ItemSearchResponse {
    private final Page<ItemSearchDto> items;
    private final Map<String, Long> categoryCounts;

    public ItemSearchResponse(Page<ItemSearchDto> items, Map<String, Long> categoryCounts) {
        this.items = items;
        this.categoryCounts = categoryCounts;
    }
}
