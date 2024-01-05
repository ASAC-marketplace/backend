package market.demo.domain.inquiry;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInquiry is a Querydsl query type for Inquiry
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInquiry extends EntityPathBase<Inquiry> {

    private static final long serialVersionUID = -520371953L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInquiry inquiry = new QInquiry("inquiry");

    public final StringPath contactNumber = createString("contactNumber");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<String, StringPath> imageUrls = this.<String, StringPath>createList("imageUrls", String.class, StringPath.class, PathInits.DIRECT2);

    public final EnumPath<market.demo.domain.type.InquiryType> inquiryType = createEnum("inquiryType", market.demo.domain.type.InquiryType.class);

    public final market.demo.domain.member.QMember member;

    public final BooleanPath notificationEnabled = createBoolean("notificationEnabled");

    public final DatePath<java.time.LocalDate> registerDate = createDate("registerDate", java.time.LocalDate.class);

    public final EnumPath<market.demo.domain.status.InquiryStatus> status = createEnum("status", market.demo.domain.status.InquiryStatus.class);

    public final StringPath title = createString("title");

    public QInquiry(String variable) {
        this(Inquiry.class, forVariable(variable), INITS);
    }

    public QInquiry(Path<? extends Inquiry> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInquiry(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInquiry(PathMetadata metadata, PathInits inits) {
        this(Inquiry.class, metadata, inits);
    }

    public QInquiry(Class<? extends Inquiry> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new market.demo.domain.member.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

