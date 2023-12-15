package market.demo.dto.search;

import lombok.Data;

@Data
public class ItemRecommendDto {
    private String keyword;
    private Long frequency;

    public ItemRecommendDto(Object[] objects){
        if (objects != null && objects.length > 0) {
            this.keyword = (String) objects[0];
            this.frequency = (Long) objects[1];
        }
    }
}
