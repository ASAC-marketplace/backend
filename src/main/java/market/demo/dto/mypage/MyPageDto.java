package market.demo.dto.mypage;

import lombok.Getter;
import lombok.Setter;
import market.demo.domain.member.Member;

@Getter
@Setter
public class MyPageDto {
    //이름, 쿠폰 개수, 찜한 상품
    private String loginId;
    private String memberName;
    private Long couponCount;
    private Long wishListCount;

    public MyPageDto(Member member){
        this.loginId = member.getLoginId();
        this.memberName = member.getMemberName();
        this.couponCount = (long)(member.getCoupons().size());
        this.wishListCount = (long) member.getWishlist().getItems().size();
    }
}
