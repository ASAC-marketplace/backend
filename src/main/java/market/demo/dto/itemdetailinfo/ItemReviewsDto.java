package market.demo.dto.itemdetailinfo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.item.Item;
import market.demo.domain.item.Review;
import market.demo.domain.member.Member;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ItemReviewsDto {
    private Long itemId;
    private String itemName;
    private Long reviewCount;
    private List<ReviewDto> reviews = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    public ItemReviewsDto(@NotNull Item item, List<Review> reviews, Member member){
        this.itemId = item.getId();
        this.itemName = item.getName();
        this.reviewCount = (long) item.getReviews().size();
        this.reviews = createReviewDtos(reviews, member);
        this.imageUrls = aggregateImageUrls(reviews);
    }

    private List<ReviewDto> createReviewDtos(List<Review> reviews, Member member) {
        return reviews.stream()
                .map((Review review) -> new ReviewDto(review,member))
                .collect(Collectors.toList());
    }

    private List<String> aggregateImageUrls(List<Review> reviews) {
        return reviews.stream()
                .flatMap(review -> review.getImageUrls().stream())
                .collect(Collectors.toList());
    }
}
