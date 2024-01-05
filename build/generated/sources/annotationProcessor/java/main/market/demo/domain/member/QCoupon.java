package market.demo.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = -443796887L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final StringPath couponName = createString("couponName");

    public final EnumPath<market.demo.domain.type.DiscountType> discountType = createEnum("discountType", market.demo.domain.type.DiscountType.class);

    public final NumberPath<Integer> discountValue = createNumber("discountValue", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember issuedTo;

    public final BooleanPath isUsed = createBoolean("isUsed");

    public final NumberPath<Integer> minimumOrderPrice = createNumber("minimumOrderPrice", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> validFrom = createDateTime("validFrom", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> validTo = createDateTime("validTo", java.time.LocalDateTime.class);

    public QCoupon(String variable) {
        this(Coupon.class, forVariable(variable), INITS);
    }

    public QCoupon(Path<? extends Coupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCoupon(PathMetadata metadata, PathInits inits) {
        this(Coupon.class, metadata, inits);
    }

    public QCoupon(Class<? extends Coupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.issuedTo = inits.isInitialized("issuedTo") ? new QMember(forProperty("issuedTo"), inits.get("issuedTo")) : null;
    }

}

