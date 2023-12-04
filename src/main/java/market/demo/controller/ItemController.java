package market.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.dto.itemdetailinfo.ItemDetailDto;
import market.demo.dto.itemdetailinfo.ItemReviewsDto;
import market.demo.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

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
}
