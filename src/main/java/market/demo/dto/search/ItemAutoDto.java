package market.demo.dto.search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ItemAutoDto {
    private String name;

    @QueryProjection
    public ItemAutoDto(String name) {
        this.name = name;
    }
}
