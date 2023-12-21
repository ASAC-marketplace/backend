package market.demo.dto.itemdetailinfo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.item.Review;
import market.demo.domain.member.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class ReviewDto {
    private Long reviewId;
    private Long memberId;
    private String memberName;
    private int rating;
    private String comment;
    private Integer helpful;// 도움돼요
    private Boolean checked;// 도움돼요 활성화 여부
    private LocalDateTime reviewWriteDate;
    private List<String> imageUrls = new ArrayList<>();

    public ReviewDto(Review review, Member member){
        this.reviewId = review.getId();
        this.memberId = review.getMember().getId();
        this.memberName = review.getMember().getMemberName();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.helpful = review.getHelpful();
        this.reviewWriteDate = review.getReviewWriteDate();
        this.imageUrls = review.getImageUrls();
        this.checked = review.getLikedByMembers().contains(member);
        log.info(String.valueOf(this.checked));
    }

}
