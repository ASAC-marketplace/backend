package market.demo.domain.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = 1205449981L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItem item = new QItem("item");

    public final StringPath brand = createString("brand");

    public final QCategory category;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> discountRate = createNumber("discountRate", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QItemDetail itemDetail;

    public final NumberPath<Integer> itemPrice = createNumber("itemPrice", Integer.class);

    public final StringPath name = createString("name");

    public final DateTimePath<java.time.LocalDateTime> promotionEnd = createDateTime("promotionEnd", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> promotionStart = createDateTime("promotionStart", java.time.LocalDateTime.class);

    public final EnumPath<market.demo.domain.type.PromotionType> promotionType = createEnum("promotionType", market.demo.domain.type.PromotionType.class);

    public final DatePath<java.time.LocalDate> registerdDate = createDate("registerdDate", java.time.LocalDate.class);

    public final ListPath<Review, QReview> reviews = this.<Review, QReview>createList("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public final EnumPath<market.demo.domain.status.ItemStatus> status = createEnum("status", market.demo.domain.status.ItemStatus.class);

    public final NumberPath<Integer> stockQuantity = createNumber("stockQuantity", Integer.class);

    public QItem(String variable) {
        this(Item.class, forVariable(variable), INITS);
    }

    public QItem(Path<? extends Item> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItem(PathMetadata metadata, PathInits inits) {
        this(Item.class, metadata, inits);
    }

    public QItem(Class<? extends Item> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category"), inits.get("category")) : null;
        this.itemDetail = inits.isInitialized("itemDetail") ? new QItemDetail(forProperty("itemDetail"), inits.get("itemDetail")) : null;
    }

}

