package market.demo.domain.item;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class ItemDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_detail_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    // 배송 방식
    private String deliveryMethod;

    // 판매자 정보 (예: 이름, 연락처 등)
    private String sellerInfo;

    // 상품 정보 (예: 재료, 제조 방법 등)
    private String itemInfo;

    // 포장 타입
    private String packagingType;

    // 안내 사항 (예 : 주의 사항, 보관 방법 등)
    private String notes;

    // 좋아요 수
    private Integer likeCount;

    // 브랜드
    private String brand;

    // 상품 상세 이미지 URL 리스트
    @ElementCollection
    @CollectionTable(name = "item_detail_images", joinColumns = @JoinColumn(name = "item_detail_id"))
    @Column(name = "image_url")
    private List<String> detailImages = new ArrayList<>();

    private String promotionImageUrl;

    // 추가 설명
    private String additionalDescription;

    ///////////////// 테스트 데이터용
    public ItemDetail(Item item, String brand, String deliveryMethod, String sellerInfo, String itemInfo, String packagingType, String notes, Integer likeCount, String additionalDescription, List<String> detailImages, String promotionImageUrl) {
        this.item = item;
        this.brand = brand;
        this.deliveryMethod = deliveryMethod;
        this.sellerInfo = sellerInfo;
        this.itemInfo = itemInfo;
        this.packagingType = packagingType;
        this.notes = notes;
        this.likeCount = likeCount;
        this.additionalDescription = additionalDescription;
        this.detailImages.addAll(detailImages);
        this.promotionImageUrl = promotionImageUrl;
    }

    public ItemDetail() {

    }

    //////////////////
}
