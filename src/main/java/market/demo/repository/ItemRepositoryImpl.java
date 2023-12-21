package market.demo.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import market.demo.domain.item.QItem;
import market.demo.domain.item.QReview;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;
import market.demo.dto.search.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import static market.demo.domain.item.QCategory.category;
import static market.demo.domain.item.QItem.item;
import static market.demo.domain.item.QItemDetail.itemDetail;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public ItemSearchResponse searchPageComplex(ItemSearchCondition condition, Pageable pageable) {
        NumberExpression<Integer> discountedPrice = item.itemPrice
                .multiply(item.discountRate.multiply(-1).add(100))
                .divide(100);

        JPAQuery<ItemSearchDto> query = queryFactory
                .select(new QItemSearchDto(
                        item.id,
                        item.name,
                        item.category.id,
                        item.category.name,
                        item.status,
                        item.promotionType,
                        item.stockQuantity,
                        item.brand,
                        item.registerdDate,
                        item.discountRate,
                        item.itemPrice,
                        item.itemDetail.promotionImageUrl,
                        discountedPrice))
                .from(item)
                .leftJoin(item.category, category)
                .leftJoin(item.itemDetail, itemDetail)
                .where(
                        nameEq(condition.getName()),
                        categoryNameEq(condition.getCategoryName()),
                        brandEq(condition.getBrand()),
                        statusEq(condition.getStatus()),
                        promotionTypeEq(condition.getPromotionType()),
                        stockQuantityGoe(condition.getMinStockQuantity()),
                        registeredDateGoe(condition.getMinRegisteredDate()),
                        discountRateBetween(condition.getMinDiscountRate(), condition.getMaxDiscountRate()),
                        priceRangeEq(condition.getPriceRange()),
                        itemPriceBetween(condition.getMinPrice(), condition.getMaxPrice()));


        // Pageable의 정렬 조건 적용
        for (Sort.Order order : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(item.getType(), item.getMetadata());
            query.orderBy(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty())));
        }

        List<ItemSearchDto> content = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> itemIds = content.stream().map(ItemSearchDto::getId).collect(Collectors.toList());
        Map<Long, Long> reviewCounts = getReviewCounts(itemIds);

        // 리뷰 카운트를 DTO에 설정
        content.forEach(dto -> dto.setReviewCount(reviewCounts.getOrDefault(dto.getId(), 0L)));

        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .leftJoin(item.category, category)
                .where(
                        query.getMetadata().getWhere()
                );

        Page<ItemSearchDto> page = PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
        return new ItemSearchResponse(page);
    }

    @Override
    public CountsAndPriceRangeDto getCounts(ItemSearchCondition condition) {
        Tuple priceRangeTuple = findPriceRange(condition);
        List<String> priceRanges = createPriceRanges(priceRangeTuple);

        Map<String, Long> categoryCounts = getCategoryCounts(condition);
        Map<String, Long> brandCounts = getBrandCounts(condition);
        Map<PromotionType, Long> promotionCounts = getPromotionTypeCounts(condition);

        CountsAndPriceRangeDto dto = new CountsAndPriceRangeDto(categoryCounts, brandCounts, promotionCounts, priceRanges);

        return dto;
    }

    @Override
    public List<ItemAutoDto> findItemNamesByKeyword(String keyword, int limit) {
        QItem item = QItem.item;

        List<ItemAutoDto> result = queryFactory
                .select(Projections.constructor(ItemAutoDto.class, item.name))
                .from(item)
                .where(item.name.like("%" + keyword + "%")) //( "%" + keyword + "%")
                .limit(limit)
                .fetch();

        return result;
    }

    private BooleanExpression nameEq(String name) {
        return hasText(name) ? item.name.contains(name) : null;
    }

    private BooleanExpression categoryIdEq(Long categoryId) {

        return categoryId != null ? item.category.id.eq(categoryId) : null;
    }

    private BooleanExpression categoryNameEq(List<String> categoryName) {
        return categoryName != null && !categoryName.isEmpty() ? item.category.name.in(categoryName) : null;
    }

    private BooleanExpression brandEq(List<String> brand) {
        return brand != null && !brand.isEmpty() ? item.brand.in(brand) : null;
    }

    private BooleanExpression statusEq(List<ItemStatus> status) {
        return status != null && !status.isEmpty() ? item.status.in(status) : null;
    }

    private BooleanExpression promotionTypeEq(List<PromotionType> promotionType) {
        return promotionType != null && !promotionType.isEmpty() ? item.promotionType.in(promotionType) : null;
    }

    private BooleanExpression stockQuantityGoe(Integer minStockQuantity) {
        return minStockQuantity != null ? item.stockQuantity.goe(minStockQuantity) : null;
    }

    private BooleanExpression registeredDateGoe(LocalDate minRegisteredDate) {
        return minRegisteredDate != null ? item.registerdDate.after(minRegisteredDate.minusDays(1)) : null;
    }

    private BooleanExpression discountRateBetween(Integer minDiscountRate, Integer maxDiscountRate) {
        if (minDiscountRate != null && maxDiscountRate != null) {
            return item.discountRate.between(minDiscountRate, maxDiscountRate);
        } else if (minDiscountRate != null) {
            return item.discountRate.goe(minDiscountRate);
        } else if (maxDiscountRate != null) {
            return item.discountRate.loe(maxDiscountRate);
        } else {
            return null;
        }
    }

    NumberExpression<Integer> discountRateExpression = item.discountRate.multiply(-1).add(100);

    NumberExpression<Integer> discountedPrice = item.itemPrice.multiply(discountRateExpression).divide(100);

    private BooleanExpression itemPriceBetween(Integer minPrice, Integer maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return item.itemPrice.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return item.itemPrice.goe(minPrice);
        } else if (maxPrice != null) {
            return item.itemPrice.loe(maxPrice);
        } else {
            return null;
        }
    }

    private Map<String, Long> getCategoryCounts(ItemSearchCondition condition) {
        List<Tuple> categoryCounts = queryFactory
                .select(category.name, item.count())
                .from(item)
                .leftJoin(item.category, category)
                .where(
                        category.parent.isNotNull(),
                        nameEq(condition.getName())) //소분류만
                .groupBy(category.name)
                .fetch();

        return categoryCounts.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(category.name),
                        tuple -> tuple.get(item.count())
                ));
    }

    private Map<String, Long> getBrandCounts(ItemSearchCondition condition) {
        List<Tuple> brandCounts = queryFactory
                .select(item.brand, item.count())
                .from(item)
                .where(
                        nameEq(condition.getName())
                )
                .groupBy(item.brand)
                .fetch();

        return brandCounts.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(item.brand),
                        tuple -> tuple.get(item.count()),
                        Long::sum // 중복된 키에 대해 값을 합산
                ));
    }

    private Map<PromotionType, Long> getPromotionTypeCounts(ItemSearchCondition condition) {
        List<Tuple> promotionTypeCounts = queryFactory
                .select(item.promotionType, item.count())
                .from(item)
                .where(
                        nameEq(condition.getName())
                )
                .groupBy(item.promotionType)
                .fetch();

        return promotionTypeCounts.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(item.promotionType),
                        tuple -> tuple.get(item.count())
                ));
    }

    private Tuple findPriceRange(ItemSearchCondition condition) {
        return queryFactory
                .select(item.itemPrice.min(), item.itemPrice.max())
                .from(item)
                .where(
                        nameEq(condition.getName())
                )
                .fetchOne();
    }

    private List<String> createPriceRanges(Tuple priceRange) {
        if (priceRange == null) {
            return Collections.emptyList();
        }

        Integer minPrice = priceRange.get(item.itemPrice.min());
        Integer maxPrice = priceRange.get(item.itemPrice.max());

        if (minPrice == null || maxPrice == null) {
            return Collections.emptyList();
        }

        int rangeSize = (maxPrice - minPrice) / 5;
        List<String> priceRanges = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int start = minPrice + i * rangeSize;
            int end = (i < 4) ? start + rangeSize - 1 : maxPrice;
            priceRanges.add(String.format("%d ~ %d", start, end));
        }

        return priceRanges;
    }

    private BooleanExpression priceRangeEq(String priceRange) {
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] ranges = priceRange.split("~");
            if (ranges.length == 2) {
                int minRange = Integer.parseInt(ranges[0].trim());
                int maxRange = Integer.parseInt(ranges[1].trim());
                return discountedPrice.between(minRange, maxRange); // discountedPrice를 사용
            }
        }
        return null;
    }

    // 리뷰 카운트 조회 메서드
    private Map<Long, Long> getReviewCounts(List<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return Collections.emptyMap();
        }

        QReview review = QReview.review; // Review 엔티티의 Querydsl 모델

        List<Tuple> counts = queryFactory
                .select(item.id, review.count())
                .from(item)
                .leftJoin(review).on(review.item.eq(item)) // Review 엔티티와 조인
                .where(item.id.in(itemIds))
                .groupBy(item.id)
                .fetch();

        return counts.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(item.id),
                        tuple -> tuple.get(review.count())
                ));
    }
}
