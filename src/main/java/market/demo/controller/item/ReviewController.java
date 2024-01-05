package market.demo.controller.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.jwt.TokenProvider;
import market.demo.dto.itemdetailinfo.ItemReviewsDto;
import market.demo.dto.review.ReviewRequestDto;
import market.demo.service.ItemService;
import market.demo.service.ReviewService;
import market.demo.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final ItemService itemService;
    private final TokenProvider tokenProvider;
    private final S3Service s3Service;

    @PostMapping("/create")
    public ResponseEntity<String> createReview(
                                          @RequestPart("review") String stringDto,
                                          @RequestPart("reviewImages")List<MultipartFile> imageFiles) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ReviewRequestDto reviewRequestDto = objectMapper.readValue(stringDto, ReviewRequestDto.class);

        log.info("Recived createReview request");
        List<String> imageUrls = s3Service.uploadFiles(imageFiles);
        reviewRequestDto.setImageUrls(imageUrls);

        reviewService.createReview(reviewRequestDto);
//        log.info("memberId ={}", reviewRequestDto.getMemberId());
        return ResponseEntity.ok().body("후기가 성공적으로 등록되었습니다.");
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestPart List<MultipartFile> files) throws IOException {
        List<String> uploadedUrls = s3Service.uploadFiles(files);
        return ResponseEntity.ok(uploadedUrls);
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
