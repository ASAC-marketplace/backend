package market.demo.dto.item;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemDto {
    private Long id;
    private LocalDate registeredDate;
    private String imageUrl;

    @QueryProjection
    public ItemDto(Long id, LocalDate registeredDate, String imageUrl) {
        this.id = id;
        this.registeredDate = registeredDate;
        this.imageUrl = imageUrl;
    }
}
