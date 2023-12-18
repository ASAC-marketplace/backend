package market.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import market.demo.domain.item.Item;
import market.demo.domain.item.Review;
import market.demo.domain.member.Member;
import market.demo.dto.review.ReviewRequestDto;
import market.demo.exception.ItemNotFoundException;
import market.demo.exception.MemberNotFoundException;
import market.demo.repository.ItemRepository;
import market.demo.repository.MemberRepository;
import market.demo.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    public void createReview(ReviewRequestDto reviewRequestDto) {
        Member member = memberRepository.findById(reviewRequestDto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));

        Item item = itemRepository.findById(reviewRequestDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));

        // Review 객체 생성 및 저장
        Review review = new Review(
                item,               // 아이템 객체
                member,             // 회원 객체
                reviewRequestDto.getRating(),  // 평점
                reviewRequestDto.getComment(), // 코멘트
                LocalDateTime.now(),           // 현재 날짜와 시간
                reviewRequestDto.getImageUrls() // 이미지 URL 리스트
        );
        reviewRepository.save(review);
    }
}
