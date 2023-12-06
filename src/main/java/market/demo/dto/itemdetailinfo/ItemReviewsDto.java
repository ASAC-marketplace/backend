package market.demo.dto.itemdetailinfo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemReviewsDto {
    private Long itemId;
    private Long reviewCount;
    private List<ReviewDto> reviews = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
}
