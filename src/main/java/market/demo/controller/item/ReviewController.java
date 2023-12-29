package market.demo.controller.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.jwt.TokenProvider;
import market.demo.dto.itemdetailinfo.ItemReviewsDto;
import market.demo.dto.review.ReviewRequestDto;
import market.demo.service.ItemService;
import market.demo.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final ItemService itemService;
    private final TokenProvider tokenProvider;

    @PostMapping("/create")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequestDto reviewRequestDto) {
        reviewService.createReview(reviewRequestDto);
        log.info("memberId ={}", reviewRequestDto.getMemberId());
        return ResponseEntity.ok().body("후기가 성공적으로 등록되었습니다.");
    }

    //17 api 상품 리뷰 노출
    @GetMapping()
    public ResponseEntity<ItemReviewsDto> showItemReviews(@RequestParam Long itemId){
        String loginId = tokenProvider.getLoginIdFromCurrentRequest();
        return ResponseEntity.ok(itemService.searchItemReview(itemId, loginId));
    }

    //17 api 도움돼요
    @PostMapping("helpful")
    public ResponseEntity<String> helpfulReviews(@RequestParam Long reviewId){
        String loginId = tokenProvider.getLoginIdFromCurrentRequest();
        itemService.changeReviewCount(reviewId, 1, loginId);
        return ResponseEntity.ok("도움돼요 추가 성공");
    }
    @PostMapping("helpless")
    public ResponseEntity<String> helplessReviews(@RequestParam Long reviewId){
        String loginId = tokenProvider.getLoginIdFromCurrentRequest();
        itemService.changeReviewCount(reviewId, -1, loginId);
        return ResponseEntity.ok("도움돼요 취소 성공");
    }

}
