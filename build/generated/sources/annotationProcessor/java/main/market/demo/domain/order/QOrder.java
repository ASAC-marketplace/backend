package market.demo.domain.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = 1456288925L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final market.demo.domain.etc.QDelivery delivery;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final market.demo.domain.member.QMember member;

    public final DateTimePath<java.time.LocalDateTime> orderDateTime = createDateTime("orderDateTime", java.time.LocalDateTime.class);

    public final ListPath<OrderItem, QOrderItem> orderItems = this.<OrderItem, QOrderItem>createList("orderItems", OrderItem.class, QOrderItem.class, PathInits.DIRECT2);

    public final EnumPath<market.demo.domain.status.OrderStatus> orderStatus = createEnum("orderStatus", market.demo.domain.status.OrderStatus.class);

    public final QPayment payment;

    public final NumberPath<Long> totalAmount = createNumber("totalAmount", Long.class);

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.delivery = inits.isInitialized("delivery") ? new market.demo.domain.etc.QDelivery(forProperty("delivery"), inits.get("delivery")) : null;
        this.member = inits.isInitialized("member") ? new market.demo.domain.member.QMember(forProperty("member"), inits.get("member")) : null;
        this.payment = inits.isInitialized("payment") ? new QPayment(forProperty("payment"), inits.get("payment")) : null;
    }

}

