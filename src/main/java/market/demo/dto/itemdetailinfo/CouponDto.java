package market.demo.dto.itemdetailinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import market.demo.domain.member.Coupon;
import market.demo.domain.type.DiscountType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CouponDto {
    private Long couponId;
    private String couponName;
    private DiscountType discountType;
    private Integer discountValue;
    private LocalDateTime validFrom;//시작일
    private LocalDateTime validTo;//종료일
    private Integer minimumOrderPrice;//최소 주문 가격

    public CouponDto(Coupon coupon){
        this.couponId = coupon.getId();
        this.couponName = coupon.getCouponName();
        this.discountType = coupon.getDiscountType();
        this.discountValue = coupon.getDiscountValue();
        this.validFrom = coupon.getValidFrom();
        this.validTo = coupon.getValidTo();
        this.minimumOrderPrice = coupon.getMinimumOrderPrice();
    }
}
