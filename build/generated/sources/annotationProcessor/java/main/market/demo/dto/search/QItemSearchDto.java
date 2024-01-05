package market.demo.dto.search;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * market.demo.dto.search.QItemSearchDto is a Querydsl Projection type for ItemSearchDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QItemSearchDto extends ConstructorExpression<ItemSearchDto> {

    private static final long serialVersionUID = 1805052806L;

    public QItemSearchDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<Long> categoryId, com.querydsl.core.types.Expression<String> categoryName, com.querydsl.core.types.Expression<market.demo.domain.status.ItemStatus> status, com.querydsl.core.types.Expression<market.demo.domain.type.PromotionType> promotionType, com.querydsl.core.types.Expression<Integer> stockQuantity, com.querydsl.core.types.Expression<String> brand, com.querydsl.core.types.Expression<java.time.LocalDate> registeredDate, com.querydsl.core.types.Expression<Integer> discountRate, com.querydsl.core.types.Expression<Integer> itemPrice, com.querydsl.core.types.Expression<String> promotionUrl, com.querydsl.core.types.Expression<Integer> discountedPrice) {
        super(ItemSearchDto.class, new Class<?>[]{long.class, String.class, long.class, String.class, market.demo.domain.status.ItemStatus.class, market.demo.domain.type.PromotionType.class, int.class, String.class, java.time.LocalDate.class, int.class, int.class, String.class, int.class}, id, name, categoryId, categoryName, status, promotionType, stockQuantity, brand, registeredDate, discountRate, itemPrice, promotionUrl, discountedPrice);
    }

}

