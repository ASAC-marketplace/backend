package market.demo.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -166992675L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final QAddress address;

    public final EnumPath<market.demo.domain.status.AgeStatus> ageRange = createEnum("ageRange", market.demo.domain.status.AgeStatus.class);

    public final SetPath<market.demo.domain.member.jwt.Authority, market.demo.domain.member.jwt.QAuthority> authorities = this.<market.demo.domain.member.jwt.Authority, market.demo.domain.member.jwt.QAuthority>createSet("authorities", market.demo.domain.member.jwt.Authority.class, market.demo.domain.member.jwt.QAuthority.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> birthday = createDate("birthday", java.time.LocalDate.class);

    public final market.demo.domain.order.QCart cart;

    public final ListPath<Coupon, QCoupon> coupons = this.<Coupon, QCoupon>createList("coupons", Coupon.class, QCoupon.class, PathInits.DIRECT2);

    public final StringPath email = createString("email");

    public final EnumPath<market.demo.domain.status.GenderStatus> gender = createEnum("gender", market.demo.domain.status.GenderStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<market.demo.domain.inquiry.Inquiry, market.demo.domain.inquiry.QInquiry> inquiries = this.<market.demo.domain.inquiry.Inquiry, market.demo.domain.inquiry.QInquiry>createList("inquiries", market.demo.domain.inquiry.Inquiry.class, market.demo.domain.inquiry.QInquiry.class, PathInits.DIRECT2);

    public final ListPath<market.demo.domain.item.Review, market.demo.domain.item.QReview> likedReviews = this.<market.demo.domain.item.Review, market.demo.domain.item.QReview>createList("likedReviews", market.demo.domain.item.Review.class, market.demo.domain.item.QReview.class, PathInits.DIRECT2);

    public final StringPath loginId = createString("loginId");

    public final StringPath memberName = createString("memberName");

    public final ListPath<market.demo.domain.order.Order, market.demo.domain.order.QOrder> orders = this.<market.demo.domain.order.Order, market.demo.domain.order.QOrder>createList("orders", market.demo.domain.order.Order.class, market.demo.domain.order.QOrder.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath provider = createString("provider");

    public final StringPath providerId = createString("providerId");

    public final ListPath<market.demo.domain.item.Review, market.demo.domain.item.QReview> reviews = this.<market.demo.domain.item.Review, market.demo.domain.item.QReview>createList("reviews", market.demo.domain.item.Review.class, market.demo.domain.item.QReview.class, PathInits.DIRECT2);

    public final market.demo.domain.etc.QWishlist wishlist;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new QAddress(forProperty("address")) : null;
        this.cart = inits.isInitialized("cart") ? new market.demo.domain.order.QCart(forProperty("cart"), inits.get("cart")) : null;
        this.wishlist = inits.isInitialized("wishlist") ? new market.demo.domain.etc.QWishlist(forProperty("wishlist"), inits.get("wishlist")) : null;
    }

}

