package market.demo.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import market.demo.domain.item.Item;
import market.demo.domain.member.Member;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne (fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int rating;
    private String comment;
    private Integer helpful; // 도움돼요

    // 리뷰 이미지
    @ElementCollection
    @CollectionTable(name = "review_image", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    //////////////// 테스트 데이터용
    public Review(Item item, String comment, int rating, Integer helpful, List<String> imageUrls) {
        this.item = item;
        this.comment = comment;
        this.rating = rating;
        this.helpful = helpful;
        this.imageUrls.addAll(imageUrls);
    }
    ///////////////
}
