package market.demo.dto.search;

import lombok.Data;

@Data
public class ItemRecomendDto {
    private String keyword;
    private Long frequency;

    public ItemRecomendDto(Object[] objects){
        if (objects != null && objects.length > 0) {
            this.keyword = (String) objects[0];
            this.frequency = (Long) objects[1];
        }
    }
}
