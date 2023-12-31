package market.demo.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.member.Member;
import market.demo.domain.type.DiscountType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    private String couponName;

    //할인 타입이 퍼센트인지 아닌지
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    //할인 값
    private Integer discountValue;

    //쿠폰 시작일, 종료일
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    //최소 주문 가격
    private Integer minimumOrderPrice;

    private boolean isUsed;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member issuedTo; // 발급 대상 사용자 null 가능


    public Coupon(Coupon coupon, Member member) {
        this.couponName = coupon.getCouponName();
        this.discountType = coupon.getDiscountType();
        this.discountValue = coupon.getDiscountValue();
        this.validFrom = coupon.getValidFrom();
        this.validTo = coupon.getValidTo();
        this.minimumOrderPrice = coupon.getMinimumOrderPrice();
        this.isUsed = false;
        this.issuedTo = member;
    }
}