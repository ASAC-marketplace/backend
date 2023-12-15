package market.demo.dto.itemdetailinfo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.item.Item;
import market.demo.domain.item.ItemDetail;

import java.util.List;

@Data
public class ItemDetailDto {
    private Long itemId;
    private Integer itemPrice;
    private Integer saleItemPrice;
    private String itemName;
    private Integer discountRate;
    private String description;
    private Long reviewCount;
    private Integer stockQuantity;//재고
    //detail
    private String promotionImageUrl;
    private String deliveryMethod;// 배송 방식
    private String sellerInfo;// 판매자 정보 (예: 이름, 연락처 등)
    private String itemInfo;// 상품 정보 (예: 재료, 제조 방법 등)
    private String packagingType;// 포장 타입
    private String notes;// 안내 사항 (예 : 주의 사항, 보관 방법 등)
    private Integer likeCount;// 좋아요 수
    private List<String> detailImages;// 상품 상세 이미지 URL 리스트
    private String additionalDescription;// 추가 설명
    private CouponDto coupon;
    private boolean wished;

    public ItemDetailDto(Item item, ItemDetail itemDetail, CouponDto couponDto, boolean isWished){
        this.itemId = item.getId();
        this.itemPrice = item.getItemPrice();
        this.saleItemPrice = (int) (item.getItemPrice()* (100 - item.getDiscountRate()) * 0.01);
        this.itemName = item.getName();
        this.discountRate = item.getDiscountRate();
        this.description = item.getDescription();
        this.reviewCount = (long) item.getReviews().size();
        this.stockQuantity = item.getStockQuantity();
        this.deliveryMethod = itemDetail.getDeliveryMethod();
        this.sellerInfo = itemDetail.getSellerInfo();
        this.itemInfo = itemDetail.getItemInfo();
        this.packagingType = itemDetail.getPackagingType();
        this.notes = itemDetail.getNotes();
        this.likeCount = itemDetail.getLikeCount();
        this.detailImages = itemDetail.getDetailImages();
        this.additionalDescription = itemDetail.getAdditionalDescription();
        this.coupon = couponDto;
        this.promotionImageUrl = itemDetail.getPromotionImageUrl();
        this.wished = isWished;
    }

}
