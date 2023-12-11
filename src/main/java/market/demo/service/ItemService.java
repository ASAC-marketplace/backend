package market.demo.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.item.*;
import market.demo.domain.member.Coupon;
import market.demo.domain.member.Member;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;
import market.demo.dto.item.ItemDto;
import market.demo.dto.item.ItemMainEndDto;
import market.demo.dto.item.QItemDto;
import market.demo.dto.item.QItemMainEndDto;
import market.demo.dto.itemdetailinfo.CouponDto;
import market.demo.dto.itemdetailinfo.ItemDetailDto;
import market.demo.dto.itemdetailinfo.ItemReviewsDto;
import market.demo.dto.itemdetailinfo.ReviewDto;
import market.demo.exception.*;
import market.demo.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;

import market.demo.domain.item.QItem;
import market.demo.domain.item.QItemDetail;
import market.demo.domain.item.QReview;

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
    private final JPAQueryFactory queryFactory;
    private static final String DISCOUNT_COUPON_NAME = "할인쿠폰";
    public ItemDetailDto searchItemDetail(Long itemId){
        Item item = itemRepository.findById(itemId).
                orElseThrow(()-> new ItemNotFoundException("상품을 찾을 수 없습니다."));
        ItemDetail itemDetail = itemDetailRepository.findByItem(item)
                .orElseThrow(() -> new ItemNotFoundException("사용자를 찾을 수 없습니다."));

        Optional<Coupon> optionalCoupon = couponRepository.findByCouponName(DISCOUNT_COUPON_NAME);
        CouponDto couponDto = getCouponDto(optionalCoupon);
        return getItemDetailDto(item, itemDetail, couponDto);
    }

    @NotNull
    private static CouponDto getCouponDto(Optional<Coupon> optionalCoupon) {
        CouponDto couponDto = new CouponDto();
        if (optionalCoupon.isPresent()) {
            Coupon coupon = optionalCoupon.get();
            couponDto.setCouponId(coupon.getId());
            couponDto.setCouponName(coupon.getCouponName());
            couponDto.setDiscountType(coupon.getDiscountType());
            couponDto.setDiscountValue(coupon.getDiscountValue());
            couponDto.setValidTo(coupon.getValidTo());
            couponDto.setValidFrom(coupon.getValidFrom());
            couponDto.setMinimumOrderPrice(coupon.getMinimumOrderPrice());
        }
        return couponDto;
    }

    @NotNull
    private static ItemDetailDto getItemDetailDto(Item item, ItemDetail itemDetail, CouponDto couponDto) {
        ItemDetailDto itemDetailDto = new ItemDetailDto();

        itemDetailDto.setItemId(item.getId());
        itemDetailDto.setItemPrice(item.getItemPrice());
        itemDetailDto.setSaleItemPrice((int) (item.getItemPrice()* (100 - item.getDiscountRate()) * 0.01));
        itemDetailDto.setItemName(item.getName());
        itemDetailDto.setDiscountRate(item.getDiscountRate());
        itemDetailDto.setDescription(item.getDescription());
        itemDetailDto.setReviewCount((long) item.getReviews().size());
        itemDetailDto.setStockQuantity(item.getStockQuantity());
        itemDetailDto.setDeliveryMethod(itemDetail.getDeliveryMethod());
        itemDetailDto.setSellerInfo(itemDetail.getSellerInfo());
        itemDetailDto.setItemInfo(itemDetail.getItemInfo());
        itemDetailDto.setPackagingType(itemDetail.getPackagingType());
        itemDetailDto.setNotes(itemDetail.getNotes());
        itemDetailDto.setLikeCount(itemDetail.getLikeCount());
        itemDetailDto.setDetailImages(itemDetail.getDetailImages());
        itemDetailDto.setAdditionalDescription(itemDetail.getAdditionalDescription());
        itemDetailDto.setCoupon(couponDto);

        return itemDetailDto;
    }

    public ItemReviewsDto searchItemReview(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));

        ItemReviewsDto itemReviewsDto = new ItemReviewsDto();
        //멤버별 리뷰 정보 저장
        List<Review> reviews = reviewRepository.findAllByItem(item);
        if(reviews.isEmpty()) throw new RuntimeException("리뷰 정보가 없습니다.");

        List<ReviewDto> reviewInfos = new ArrayList<>();
        List<String> images = new ArrayList<>();

        for(Review review : reviews){
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
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        review.setHelpful(review.getHelpful() + 1);
        reviewRepository.save(review);
    }

    public void helplessItemReview(Long itemId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        review.setHelpful(review.getHelpful() - 1 );
        reviewRepository.save(review);
    }

    public void getCoupon(String loginId ,Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("해당 사용자를 찾을 수 없습니다."));

        Optional<Coupon> hasCoupon =  couponRepository.findByCouponNameAndIssuedTo(coupon.getCouponName(), member);
        if(hasCoupon.isPresent()) throw new CouponFoundException("해당 쿠폰은 이미 발급 되었습니다.");

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

    //배너?
    public Map<ItemStatus, List<ItemDto>> getItemsByStatus(int page, int size) {
        QItem item = QItem.item;
        QItemDetail itemDetail = QItemDetail.itemDetail;

        Map<ItemStatus, List<ItemDto>> itemsMap = new HashMap<>();

        for (ItemStatus status : ItemStatus.values()) {
            JPAQuery<ItemDto> query = queryFactory
                    .select(new QItemDto(
                            item.id,
                            item.registerdDate,
                            itemDetail.promotionImageUrl
                    ))
                    .from(item)
                    .leftJoin(item.itemDetail, itemDetail)
                    .where(item.status.eq(status));

            // 상태별 정렬 기준 적용
            switch (status) {
                case NEW:
                    query.orderBy(item.registerdDate.desc());
                    break;
                case BESTSELLER:
                    break;
                case DISCOUNT:
                    query.orderBy(item.discountRate.desc(), item.registerdDate.desc());
                    break;
                case SPECIAL_OFFER:
                    break;
                case RECOMMENDATION:
                    break;
            }

            List<ItemDto> items = query
                    .offset((page - 1) * size)
                    .limit(size)
                    .fetch();

            itemsMap.put(status, items);
        }

        return itemsMap;
    }

    //메인 마감세일, 주말특가
    public List<ItemMainEndDto> getItemByPromotionType(PromotionType promotionType, int page, int size) {
        QItem item = QItem.item;
        QItemDetail itemDetail = QItemDetail.itemDetail;
        QReview review = QReview.review;

        JPAQuery<ItemMainEndDto> query = queryFactory
                .select(new QItemMainEndDto(
                        item.id,
                        item.name,
                        item.discountRate,
                        item.itemPrice.subtract(item.itemPrice.multiply(item.discountRate).divide(100)),
                        item.itemPrice,
                        itemDetail.promotionImageUrl,
                        review.count()
                ))
                .from(item)
                .leftJoin(item.itemDetail, itemDetail)
                .leftJoin(item.reviews, review)
                .where(item.promotionType.eq(promotionType))
                .groupBy(item.id, item.name, item.discountRate, item.itemPrice, itemDetail.promotionImageUrl)
                .orderBy(item.registerdDate.desc());

        // size가 -1이 아닌 경우에만 페이징 적용
        if (size != -1) {
            query.offset((page - 1) * size)
                    .limit(size);
        }
        return query.fetch();
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
