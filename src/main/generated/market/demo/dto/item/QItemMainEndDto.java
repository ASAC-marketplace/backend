package market.demo.dto.item;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * market.demo.dto.item.QItemMainEndDto is a Querydsl Projection type for ItemMainEndDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QItemMainEndDto extends ConstructorExpression<ItemMainEndDto> {

    private static final long serialVersionUID = -811008829L;

    public QItemMainEndDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> brand, com.querydsl.core.types.Expression<Integer> discountRate, com.querydsl.core.types.Expression<Integer> discountedPrice, com.querydsl.core.types.Expression<Integer> itemPrice, com.querydsl.core.types.Expression<String> promotionImageUrl, com.querydsl.core.types.Expression<Long> reviewCount) {
        super(ItemMainEndDto.class, new Class<?>[]{long.class, String.class, String.class, int.class, int.class, int.class, String.class, long.class}, id, name, brand, discountRate, discountedPrice, itemPrice, promotionImageUrl, reviewCount);
    }

}

