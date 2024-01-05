package market.demo.domain.etc;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDelivery is a Querydsl query type for Delivery
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDelivery extends EntityPathBase<Delivery> {

    private static final long serialVersionUID = 1958450431L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDelivery delivery = new QDelivery("delivery");

    public final market.demo.domain.member.QAddress address;

    public final StringPath deliveryRequest = createString("deliveryRequest");

    public final EnumPath<market.demo.domain.status.DeliveryStatus> deliveryStatus = createEnum("deliveryStatus", market.demo.domain.status.DeliveryStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final market.demo.domain.order.QOrder order;

    public QDelivery(String variable) {
        this(Delivery.class, forVariable(variable), INITS);
    }

    public QDelivery(Path<? extends Delivery> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDelivery(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDelivery(PathMetadata metadata, PathInits inits) {
        this(Delivery.class, metadata, inits);
    }

    public QDelivery(Class<? extends Delivery> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new market.demo.domain.member.QAddress(forProperty("address")) : null;
        this.order = inits.isInitialized("order") ? new market.demo.domain.order.QOrder(forProperty("order"), inits.get("order")) : null;
    }

}

