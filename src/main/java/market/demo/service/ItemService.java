package market.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.etc.Wishlist;
import market.demo.domain.item.*;
import market.demo.domain.member.Coupon;
import market.demo.domain.member.Member;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;
import market.demo.dto.category.CategoryDto;
import market.demo.dto.item.ItemDto;
import market.demo.dto.item.ItemMainEndDto;
import market.demo.dto.item.QItemDto;
import market.demo.dto.item.QItemMainEndDto;
import market.demo.dto.itemdetailinfo.CouponDto;
import market.demo.dto.itemdetailinfo.ItemDetailDto;
import market.demo.dto.itemdetailinfo.ItemReviewsDto;
import market.demo.exception.*;
import market.demo.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final ReviewRepository reviewRepository;
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final WishListRepository wishListRepository;
    private final JPAQueryFactory queryFactory;
    private static final String DISCOUNT_COUPON_NAME = "할인쿠폰";
    private static final boolean WISHED_IS_EXIST = false;

    private Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
    }

    private Member getMemberByLoginIdOrReturnNull(String loginId) {
        if(loginId == null) return null;
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));
    }

    private ItemDetail getItemDetailByItem(Item item){
        return itemDetailRepository.findByItem(item)
                .orElseThrow(() -> new ItemNotFoundException("상품 상세 정보를 찾을 수 없습니다."));
    }

    private Coupon getCouponById(Long couponId){
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));
    }

    private CouponDto getCouponDto() {
        Optional<Coupon> optionalCoupon = couponRepository.findByCouponName(DISCOUNT_COUPON_NAME);
        return optionalCoupon.map(CouponDto::new).orElseGet(CouponDto::new);
    }

    private boolean checkIfItemIsWished(String loginId, Item item) {
        log.info("ID: " + loginId);
        if(memberRepository.existsByLoginId(loginId)){
            Member member = getMemberByLoginId(loginId);
            return member.getWishlist().getItems().contains(item);
        }
        return false;
    }

    public ItemDetailDto searchItemDetail(Long itemId, String loginId){
        Item item = getItemById(itemId);
        ItemDetail itemDetail = getItemDetailByItem(item);

        //로그인 된 사용자의 찜 여부
        boolean isWished = checkIfItemIsWished(loginId, item);
        log.info(String.valueOf(isWished));
        return new ItemDetailDto(item, itemDetail, getCouponDto(), isWished);
    }

    private List<Review> getReviews(Item item){
        return reviewRepository.findAllByItem(item)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 정보가 없습니다."));
    }

    public ItemReviewsDto searchItemReview(Long itemId, String loginId) {
        Member member = getMemberByLoginIdOrReturnNull(loginId);
        Item item = getItemById(itemId);
        List<Review> reviews = getReviews(item);

        //멤버별 리뷰 정보 저장
        return new ItemReviewsDto(item, reviews, member);
    }

    public void changeReviewCount(Long reviewId, int i, String loginId) {
        Member member = getMemberByLoginId(loginId);
        Review review = getReviewById(reviewId);

        review.setHelpful(i, member);
        member.setLikedReview(i, review);
        memberRepository.save(member);
        reviewRepository.save(review);
    }

    private Review getReviewById(Long reviewId){
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
    }
    private void validateCouponNotIssued(Coupon coupon, Member member) {
        Optional<Coupon> hasCoupon = couponRepository.findByCouponNameAndIssuedTo(coupon.getCouponName(), member);
        if (hasCoupon.isPresent()) throw new CouponFoundException("해당 쿠폰은 이미 발급 되었습니다.");
    }

    public void getCoupon(String loginId ,Long couponId) {
        Coupon coupon = getCouponById(couponId);
        Member member = getMemberByLoginId(loginId);

        validateCouponNotIssued(coupon, member);

        couponRepository.save(new Coupon(coupon, member));
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

        return queryFactory
                .select(new QItemMainEndDto(
                        item.id,
                        item.name,
                        item.brand,
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
                .orderBy(item.registerdDate.desc())
                .offset((long) (page - 1) * size)
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

    public void addWish(String loginId, Long itemId) {
        Member member = getMemberByLoginId(loginId);
        Item item = getItemById(itemId);
        Wishlist wishlist = member.getWishlist();

        //이미 찜한 상품
        wishlist.addItem(item);
        wishListRepository.save(wishlist);
    }

    public void minusWish(String loginId, Long itemId) {
        Member member = getMemberByLoginId(loginId);
        Item item = getItemById(itemId);
        Wishlist wishlist = member.getWishlist();

        wishlist.removeItem(item);
        wishListRepository.save(wishlist);
    }

    public List<CategoryDto> findAllCategoriesWithSubcategories() {
        return itemRepositoryCustom.findAllCategoriesWithSubcategories();
    }
    //
}
