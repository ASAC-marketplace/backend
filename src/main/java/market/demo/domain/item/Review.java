package market.demo.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.demo.domain.member.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne (fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToMany(mappedBy = "likedReviews")
    private List<Member> likedByMembers = new ArrayList<>();

    private int rating; // 추천 rating
    private String comment;
    private Integer helpful; // 도움돼요
    private LocalDateTime reviewWriteDate;

    // 리뷰 이미지
    @ElementCollection
    @CollectionTable(name = "review_image", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    //////////////// 테스트 데이터용
    public Review(Member member, Item item, String comment, int rating, Integer helpful, List<String> imageUrls, LocalDateTime reviewWriteDate) {
        this.member = member;
        this.item = item;
        this.comment = comment;
        this.rating = rating;
        this.helpful = helpful;
        this.imageUrls.addAll(imageUrls);
        this.reviewWriteDate = reviewWriteDate;
    }

    //Review 등록


    public Review(Item item, Member member,String comment, LocalDateTime reviewWriteDate, List<String> imageUrls) {
        this.item = item;
        this.member = member;
//        this.rating = rating;
        this.comment = comment;
        this.reviewWriteDate = reviewWriteDate;
        this.imageUrls = imageUrls;
    }

    public void setHelpful(int i, Member member){
        if(i == 1) {
            this.likedByMembers.add(member);
        }
        else this.likedByMembers.remove(member);
        this.helpful += i;
    }
    ///////////////
}
