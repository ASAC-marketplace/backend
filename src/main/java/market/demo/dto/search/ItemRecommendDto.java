package market.demo.dto.search;

import lombok.Data;
import market.demo.domain.search.SearchKeyword;

@Data
public class ItemRecommendDto {
    private String keyword;
    private Integer frequency;

    public ItemRecommendDto(SearchKeyword searchKeyword){
        this.keyword = searchKeyword.getKeyword();
        this.frequency = searchKeyword.getFrequency();
    }
}
