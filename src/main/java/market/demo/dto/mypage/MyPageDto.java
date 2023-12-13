package market.demo.dto.mypage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageDto {
    //이름, 쿠폰 개수, 찜한 상품
    private String loginId;
    private String memberName;
    private Long couponCount;
    private Long WishListCount;
}
