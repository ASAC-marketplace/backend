package market.demo.domain.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemDetail is a Querydsl query type for ItemDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemDetail extends EntityPathBase<ItemDetail> {

    private static final long serialVersionUID = -468039762L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemDetail itemDetail = new QItemDetail("itemDetail");

    public final StringPath additionalDescription = createString("additionalDescription");

    public final StringPath deliveryMethod = createString("deliveryMethod");

    public final ListPath<String, StringPath> detailImages = this.<String, StringPath>createList("detailImages", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QItem item;

    public final StringPath itemInfo = createString("itemInfo");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final StringPath notes = createString("notes");

    public final StringPath packagingType = createString("packagingType");

    public final StringPath promotionImageUrl = createString("promotionImageUrl");

    public final StringPath sellerInfo = createString("sellerInfo");

    public QItemDetail(String variable) {
        this(ItemDetail.class, forVariable(variable), INITS);
    }

    public QItemDetail(Path<? extends ItemDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemDetail(PathMetadata metadata, PathInits inits) {
        this(ItemDetail.class, metadata, inits);
    }

    public QItemDetail(Class<? extends ItemDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new QItem(forProperty("item"), inits.get("item")) : null;
    }

}

