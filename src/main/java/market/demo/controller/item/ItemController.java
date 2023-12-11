package market.demo.controller.item;

import lombok.RequiredArgsConstructor;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;
import market.demo.dto.item.ItemDto;
import market.demo.dto.item.ItemMainEndDto;
import market.demo.dto.itemdetailinfo.ItemDetailDto;
import market.demo.dto.itemdetailinfo.ItemReviewsDto;
import market.demo.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    //배너 추가
    @GetMapping("/status")
    public ResponseEntity<Map<ItemStatus, List<ItemDto>>> getItemsByStatus(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<ItemStatus, List<ItemDto>> itemsByStatus = itemService.getItemsByStatus(page, size);
        return ResponseEntity.ok(itemsByStatus);
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
    public ResponseEntity<ItemDetailDto> showItemDetail(@RequestParam Long itemId) {
        return ResponseEntity.ok(itemService.searchItemDetail(itemId));
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
}
