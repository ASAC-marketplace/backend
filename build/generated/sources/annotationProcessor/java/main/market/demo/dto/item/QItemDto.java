package market.demo.dto.item;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * market.demo.dto.item.QItemDto is a Querydsl Projection type for ItemDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QItemDto extends ConstructorExpression<ItemDto> {

    private static final long serialVersionUID = 1900097017L;

    public QItemDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<java.time.LocalDate> registeredDate, com.querydsl.core.types.Expression<String> imageUrl) {
        super(ItemDto.class, new Class<?>[]{long.class, java.time.LocalDate.class, String.class}, id, registeredDate, imageUrl);
    }

}

