package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.item.Item;
import market.demo.domain.item.ItemDetail;
import market.demo.domain.item.Review;
import market.demo.dto.itemdetailinfo.ItemDetailDto;
import market.demo.dto.itemdetailinfo.ItemReviewsDto;
import market.demo.dto.itemdetailinfo.ReviewDto;
import market.demo.exception.ItemNotFoundException;
import market.demo.exception.ReviewNotFoundException;
import market.demo.repository.ItemDetailRepository;
import market.demo.repository.ItemRepository;
import market.demo.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemDetailRepository itemDetailRepository;
    private final ReviewRepository reviewRepository;

    public ItemDetailDto searchItemDetail(Long itemId){
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if(optionalItem.isEmpty()) throw new ItemNotFoundException("사용자를 찾을 수 없습니다.");
        Item item = optionalItem.get();

        ItemDetail itemDetail = itemDetailRepository.findByItem(item);
        if(itemDetail == null) throw new ItemNotFoundException("사용자를 찾을 수 없습니다.");

        ItemDetailDto itemDetailDto = new ItemDetailDto();

        itemDetailDto.setItemId(item.getId());
        itemDetailDto.setItemPrice(item.getItemPrice());
        itemDetailDto.setSaleItemPrice(item.getItemPrice()* (1 - item.getDiscountRate()));
        itemDetailDto.setItemName(item.getName());
        itemDetailDto.setDiscountRate(item.getDiscountRate());
        itemDetailDto.setDescription(item.getDescription());
        itemDetailDto.setReviewCount((long) item.getReviews().size());
        itemDetailDto.setDeliveryMethod(itemDetail.getDeliveryMethod());
        itemDetailDto.setSellerInfo(itemDetail.getSellerInfo());
        itemDetailDto.setItemInfo(itemDetail.getProductInfo());
        itemDetailDto.setPackagingType(itemDetail.getPackagingType());
        itemDetailDto.setNotes(itemDetail.getNotes());
        itemDetailDto.setLikeCount(itemDetail.getLikeCount());
        itemDetailDto.setDetailImages(itemDetail.getDetailImages());
        itemDetailDto.setAdditionalDescription(itemDetail.getAdditionalDescription());

        return itemDetailDto;
    }

    public ItemReviewsDto searchItemReview(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if(optionalItem.isEmpty()) throw new ItemNotFoundException("사용자를 찾을 수 없습니다.");
        Item item = optionalItem.get();

        ItemReviewsDto itemReviewsDto = new ItemReviewsDto();
        //멤버별 리뷰 정보 저장
        List<Review> reviews = reviewRepository.findAllByItem(item);
        //if(reviews.isEmpty()) throw new RuntimeException("리뷰 정보가 없습니다.");

        List<ReviewDto> reviewInfos = new ArrayList<>();
        List<String> images = new ArrayList<>();
        
        for(Review review : reviews){
            System.out.println(review.getId());
            ReviewDto reviewDto = getReviewDto(review);
            images.addAll(review.getImageUrls());

            reviewInfos.add(reviewDto);
        }

        itemReviewsDto.setItemId(item.getId());
        itemReviewsDto.setReviewCount((long) item.getReviews().size());
        itemReviewsDto.setReviews(reviewInfos);
        itemReviewsDto.setImageUrls(images);

        return itemReviewsDto;
    }

    private static ReviewDto getReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();

        reviewDto.setReviewId(review.getId());
        reviewDto.setMemberId(review.getMember().getId());
        reviewDto.setMemberName(review.getMember().getMemberName());
        reviewDto.setRating(review.getRating());
        reviewDto.setComment(review.getComment());
        reviewDto.setHelpful(review.getHelpful());
        reviewDto.setReviewWriteDate(review.getReviewWriteDate());
        reviewDto.setImageUrls(review.getImageUrls());
        return reviewDto;
    }

    public void helpfulItemReview(Long itemId, Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);

        if(optionalReview.isEmpty()) throw new ReviewNotFoundException("리뷰를 찾을 수 없습니다.");
        Review review = optionalReview.get();
        review.setHelpful(review.getHelpful() + 1);

        reviewRepository.save(review);
    }

    public void helplessItemReview(Long itemId, Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);

        if(optionalReview.isEmpty()) throw new ReviewNotFoundException("리뷰를 찾을 수 없습니다.");
        Review review = optionalReview.get();
        review.setHelpful(review.getHelpful() - 1 );

        reviewRepository.save(review);
    }
}
