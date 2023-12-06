package market.demo.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static market.demo.domain.item.QCategory.category;
import static market.demo.domain.item.QItem.item;
import static org.springframework.util.StringUtils.hasText;

public class ItemRepositoryImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ItemSearchResponse searchPageComplex(ItemSearchCondition condition, Pageable pageable) {
        List<ItemSearchDto> content = queryFactory
                .select(new QItemSearchDto(
                        item.id,
                        item.name,
                        item.category.id,
                        item.category.name,
                        item.status,
                        item.promotionType,
                        item.stockQuantity,
                        item.registerdDate,
                        item.discountRate,
                        item.itemPrice))
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

       return new ItemSearchResponse(page, categoryCountsMap);
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
}
