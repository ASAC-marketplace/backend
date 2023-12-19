package market.demo.dto.itemdetailinfo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.item.Review;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReviewDto {
    private Long reviewId;
    private Long memberId;
    private String memberName;
    private int rating;
    private String comment;
    private Integer helpful;// 도움돼요
    private LocalDateTime reviewWriteDate;
    private List<String> imageUrls = new ArrayList<>();

    public ReviewDto(Review review){
        this.reviewId = review.getId();
        this.memberId = review.getMember().getId();
        this.memberName = review.getMember().getMemberName();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.helpful = review.getHelpful();
        this.reviewWriteDate = review.getReviewWriteDate();
        this.imageUrls = review.getImageUrls();
    }

}
