package market.demo.dto.itemdetailinfo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemDetailDto {
    private Long itemId;
    private Double itemPrice;
    private Double saleItemPrice;
    private String itemName;
    private Double discountRate;
    private String description;
    private Long reviewCount;
    //detail
    private String deliveryMethod;// 배송 방식
    private String SellerInfo;// 판매자 정보 (예: 이름, 연락처 등)
    private String itemInfo;// 상품 정보 (예: 재료, 제조 방법 등)
    private String packagingType;// 포장 타입
    private String notes;// 안내 사항 (예 : 주의 사항, 보관 방법 등)
    private Integer likeCount;// 좋아요 수
    private List<String> detailImages;// 상품 상세 이미지 URL 리스트
    private String additionalDescription;// 추가 설명

}
