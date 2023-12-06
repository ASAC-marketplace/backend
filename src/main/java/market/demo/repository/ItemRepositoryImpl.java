package market.demo.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import market.demo.domain.search.ItemSearchCondition;
import market.demo.domain.status.ItemStatus;
import market.demo.domain.type.PromotionType;
import market.demo.dto.search.ItemSearchDto;
import market.demo.dto.search.ItemSearchResponse;
import market.demo.dto.search.QItemSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static market.demo.domain.item.QCategory.category;
import static market.demo.domain.item.QItem.item;
import static market.demo.domain.item.QItemDetail.itemDetail;
import static org.springframework.util.StringUtils.hasText;

public class ItemRepositoryImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ItemSearchResponse searchPageComplex(ItemSearchCondition condition, Pageable pageable) {
        NumberExpression<Integer> discountedPrice = item.itemPrice
                .multiply(item.discountRate.multiply(-1).add(100))
                .divide(100);

        Tuple priceRangeTuple = findPriceRange(condition);
        List<String> priceRanges = createPriceRanges(priceRangeTuple);

        List<ItemSearchDto> content = queryFactory
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
                        categoryIdEq(condition.getCategoryId()),
                        statusEq(condition.getStatus()),
                        promotionTypeEq(condition.getPromotionType()),
                        stockQuantityGoe(condition.getMinStockQuantity()),
                        registeredDateGoe(condition.getMinRegisteredDate()),
                        discountRateBetween(condition.getMinDiscountRate(), condition.getMaxDiscountRate()),
                        itemPriceBetween(condition.getMinPrice(), condition.getMaxPrice()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .leftJoin(item.category, category)
                .where(
                        nameEq(condition.getName()),
                        categoryIdEq(condition.getCategoryId()),
                        statusEq(condition.getStatus()),
                        promotionTypeEq(condition.getPromotionType()),
                        stockQuantityGoe(condition.getMinStockQuantity()),
                        registeredDateGoe(condition.getMinRegisteredDate()),
                        discountRateBetween(condition.getMinDiscountRate(), condition.getMaxDiscountRate()),
                        itemPriceBetween(condition.getMinPrice(), condition.getMaxPrice())
                );

        Page<ItemSearchDto> page = PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
        Map<String, Long> categoryCountsMap = getCategoryCounts(condition);
        Map<String, Long> getBrandCountsMap = getBrandCounts(condition);
        Map<PromotionType, Long> getPromotionCountMap = getPromotionTypeCounts(condition);


        return new ItemSearchResponse(page, categoryCountsMap, getBrandCountsMap , getPromotionCountMap, priceRanges);
    }

    private BooleanExpression nameEq(String name) {
        return hasText(name) ? item.name.contains(name) : null;
    }

    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? item.category.id.eq(categoryId) : null;
    }

    private BooleanExpression statusEq(ItemStatus status) {
        return status != null ? item.status.eq(status) : null;
    }

    private BooleanExpression promotionTypeEq(PromotionType promotionType) {
        return promotionType != null ? item.promotionType.eq(promotionType) : null;
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
                .where(category.parent.isNotNull()) //소분류만
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
        Integer minPrice = priceRange.get(item.itemPrice.min());
        Integer maxPrice = priceRange.get(item.itemPrice.max());
        int rangeSize = (maxPrice - minPrice) / 5;

        List<String> priceRanges = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int start = minPrice + i * rangeSize;
            int end = (i < 4) ? start + rangeSize - 1 : maxPrice;
            priceRanges.add(String.format("%d ~ %d", start, end));
        }
        return priceRanges;
    }
}
