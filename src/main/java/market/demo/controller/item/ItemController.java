package market.demo.controller.item;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import market.demo.domain.type.PromotionType;
import market.demo.dto.item.ItemDto;
import market.demo.dto.item.ItemMainEndDto;
import market.demo.dto.itemdetailinfo.ItemDetailDto;
import market.demo.dto.itemdetailinfo.ItemReviewsDto;
import market.demo.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    //배너 추가
    @GetMapping("/recent")
    public List<ItemDto> getRecentProducts(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return itemService.getRecentProducts(page, size);
    }

    //9 메인 마감세일
    @GetMapping("/endofseason")
    public List<ItemMainEndDto> getEndOfSeasonSaleItems(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return itemService.getItemByPromotionType(PromotionType.END_OF_SEASON_SALE, page, size);
    }

    //10 메인 주말특가
    @GetMapping("/weekend")
    public List<ItemMainEndDto> getWeekendItems(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return itemService.getItemByPromotionType(PromotionType.WEEKEND_SPECIAL, page, size);
    }

    //17 api 상품 상세 정보
    @GetMapping
    public ResponseEntity<ItemDetailDto> showItemDetail(@RequestParam Long itemId,
                                                        @RequestBody String loginId) {
        return ResponseEntity.ok(itemService.searchItemDetail(itemId, loginId));
    }

    //17 api 상품 리뷰 노출
    @GetMapping("/reviews")
    public ResponseEntity<ItemReviewsDto> showItemReviews(@RequestParam Long itemId){
        return ResponseEntity.ok(itemService.searchItemReview(itemId));
    }

    //17 api 도움돼요
    @PostMapping("/reviews/helpful")
    public ResponseEntity<String> helpfulReviews(@RequestParam Long itemId, Long reviewId){
        itemService.helpfulItemReview(itemId, reviewId);
        return ResponseEntity.ok("도움돼요 추가 성공");
    }
    @PostMapping("/reviews/helpless")
    public ResponseEntity<String> helplessReviews(@RequestParam Long itemId, Long reviewId){
        itemService.helplessItemReview(itemId, reviewId);
        return ResponseEntity.ok("도움돼요 취소 성공");
    }

    //17 쿠폰 받기
    @PostMapping("/coupon")
    public ResponseEntity<String> getCouponMember(@RequestParam String loginId, Long couponId){
        itemService.getCoupon(loginId,couponId);
        return ResponseEntity.ok("쿠폰 받기 성공");
    }

    //23번 찜하기
    @PostMapping("/yeswish")
    public ResponseEntity<String> addWishList(@RequestParam String loginId, Long itemId){
        itemService.addWish(loginId, itemId);
        return ResponseEntity.ok("찜하기 추가되었습니다.");
    }

    @PostMapping("/nowish")
    public ResponseEntity<String> minusWishList(@RequestParam String loginId, Long itemId){
        itemService.minusWish(loginId, itemId);
        return ResponseEntity.ok("찜하기 취소되었습니다.");
    }

}
