package market.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.item.Item;
import market.demo.domain.item.ItemDetail;
import market.demo.domain.item.Review;
import market.demo.domain.member.Coupon;
import market.demo.domain.member.Member;
import market.demo.dto.itemdetailinfo.CouponDto;
import market.demo.dto.itemdetailinfo.ItemDetailDto;
import market.demo.dto.itemdetailinfo.ItemReviewsDto;
import market.demo.dto.itemdetailinfo.ReviewDto;
import market.demo.exception.*;
import market.demo.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.querydsl.jpa.impl.JPAQueryFactory;
import market.demo.domain.item.QItem;
import market.demo.domain.item.QItemDetail;
import market.demo.domain.item.QReview;
import market.demo.domain.type.PromotionType;
import market.demo.dto.item.ItemDto;
import market.demo.dto.item.ItemMainEndDto;
import market.demo.dto.item.QItemDto;
import market.demo.dto.item.QItemMainEndDto;
import market.demo.repository.ItemReposistory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemDetailRepository itemDetailRepository;
    private final ReviewRepository reviewRepository;
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    public ItemDetailDto searchItemDetail(Long itemId){
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if(optionalItem.isEmpty()) throw new ItemNotFoundException("상품을 찾을 수 없습니다.");
        Item item = optionalItem.get();

        ItemDetail itemDetail = itemDetailRepository.findByItem(item);
        if(itemDetail == null) throw new ItemNotFoundException("사용자를 찾을 수 없습니다.");

        String couponName = "할인쿠폰";
        Optional<Coupon> optionalCoupon = couponRepository.findByCouponName(couponName);
        CouponDto couponDto = new CouponDto();
        if (optionalCoupon.isEmpty()) {
            System.out.println("none");
            return getItemDetailDto(item, itemDetail, couponDto);
        }
        System.out.println("in");
        Coupon coupon = optionalCoupon.get();
        couponDto.setCouponId(coupon.getId());
        couponDto.setCouponName(coupon.getCouponName());
        couponDto.setDiscountType(coupon.getDiscountType());
        couponDto.setDiscountValue(coupon.getDiscountValue());
        couponDto.setValidTo(coupon.getValidTo());
        couponDto.setValidFrom(coupon.getValidFrom());
        couponDto.setMinimumOrderPrice(coupon.getMinimumOrderPrice());

        return getItemDetailDto(item, itemDetail, couponDto);
    }

    @NotNull
    private static ItemDetailDto getItemDetailDto(Item item, ItemDetail itemDetail, CouponDto couponDto) {
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
        itemDetailDto.setCoupon(couponDto);
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

    public void getCoupon(String loginId ,Long couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);

        if(optionalCoupon.isEmpty()) throw new CouponNotFoundException("쿠폰을 찾을 수 없습니다.");
        Coupon coupon = optionalCoupon.get();

        Member member = memberRepository.findByLoginId(loginId);
        if(member == null){
            throw new MemberNotFoundException("해당 사용자를 찾을 수 없습니다.");
        }

        Coupon hasCoupon =  couponRepository.findByCouponNameAndIssuedTo(coupon.getCouponName(), member);
        if(hasCoupon != null){
            throw new CouponFoundException("해당 쿠폰은 이미 발급 되었습니다.");
        }

        Coupon newCoupon = new Coupon();
        newCoupon.setCouponName(coupon.getCouponName());
        newCoupon.setDiscountType(coupon.getDiscountType());
        newCoupon.setDiscountValue(coupon.getDiscountValue());
        newCoupon.setValidFrom(coupon.getValidFrom());
        newCoupon.setValidTo(coupon.getValidTo());
        newCoupon.setMinimumOrderPrice(coupon.getMinimumOrderPrice());
        newCoupon.setUsed(false);
        newCoupon.setIssuedTo(member);

        couponRepository.save(newCoupon);
    }
  
    private final JPAQueryFactory queryFactory;

    //배너?
    public List<ItemDto> getRecentProducts(int page, int size) {
        QItem item = QItem.item;
        QItemDetail itemDetail = QItemDetail.itemDetail;

        return queryFactory
                .select(new QItemDto(
                        item.id,
                        item.registerdDate,
                        itemDetail.promotionImageUrl
                ))
                .from(item)
                .leftJoin(item.itemDetail, itemDetail)
                .orderBy(item.registerdDate.desc())
                .offset((page - 1) * size)
                .limit(size)
                .fetch();
    }

    //메인 마감세일, 주말특가
    public List<ItemMainEndDto> getItemByPromotionType(PromotionType promotionType, int page, int size) {
        QItem item = QItem.item;
        QItemDetail itemDetail = QItemDetail.itemDetail;
        QReview review = QReview.review;

        return queryFactory
                .select(new QItemMainEndDto(
                        item.id,
                        item.name,
                        item.discountRate,
                        item.price.subtract(item.price.multiply(item.discountRate).divide(100)),
                        item.price,
                        itemDetail.promotionImageUrl,
                        review.count()
                ))
                .from(item)
                .leftJoin(item.itemDetail, itemDetail)
                .leftJoin(item.reviews, review)
                .where(item.promotionType.eq(promotionType))
                .groupBy(item.id, item.name, item.discountRate, item.price, itemDetail.promotionImageUrl)
                .orderBy(item.registerdDate.desc())
                .offset((page - 1) * size)
                .limit(size)
                .fetch();
    }
    //

    //주말특가 스케줄
    public void updatePromotionTypeForItems(List<Long> itemIds, PromotionType promotionType) {
        QItem qItem = QItem.item;

        queryFactory
                .update(qItem)
                .set(qItem.promotionType, promotionType)
                .where(qItem.id.in(itemIds))
                .execute();
    }

    public void resetPromotionType(PromotionType promotionType) {
        QItem qItem = QItem.item;

        queryFactory
                .update(qItem)
                .set(qItem.promotionType, PromotionType.NONE)
                .where(qItem.promotionType.eq(promotionType))
                .execute();
    }

    public List<Long> findRandomItemIds(int limit) {
        QItem qItem = QItem.item;
        return queryFactory
                .select(qItem.id)
                .from(qItem)
                .orderBy(qItem.id.asc())
                .limit(limit)
                .fetch();
    }
    //
}
