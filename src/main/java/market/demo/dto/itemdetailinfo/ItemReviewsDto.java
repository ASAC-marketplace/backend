package market.demo.dto.itemdetailinfo;

import lombok.Getter;
import lombok.Setter;
import market.demo.domain.item.Item;
import market.demo.domain.item.Review;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ItemReviewsDto {
    private Long itemId;
    private Long reviewCount;
    private List<ReviewDto> reviews = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    public ItemReviewsDto(@NotNull Item item, List<Review> reviews){
        this.itemId = item.getId();
        this.reviewCount = (long) item.getReviews().size();
        this.reviews = createReviewDtos(reviews);
        this.imageUrls = aggregateImageUrls(reviews);
    }

    private List<ReviewDto> createReviewDtos(List<Review> reviews) {
        return reviews.stream()
                .map(ReviewDto::new)
                .collect(Collectors.toList());
    }

    private List<String> aggregateImageUrls(List<Review> reviews) {
        return reviews.stream()
                .flatMap(review -> review.getImageUrls().stream())
                .collect(Collectors.toList());
    }
}
