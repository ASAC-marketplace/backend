package market.demo.domain.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAgeGenderFrequency is a Querydsl query type for AgeGenderFrequency
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgeGenderFrequency extends EntityPathBase<AgeGenderFrequency> {

    private static final long serialVersionUID = -579893103L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAgeGenderFrequency ageGenderFrequency = new QAgeGenderFrequency("ageGenderFrequency");

    public final EnumPath<market.demo.domain.status.AgeStatus> ageRange = createEnum("ageRange", market.demo.domain.status.AgeStatus.class);

    public final NumberPath<Integer> frequency = createNumber("frequency", Integer.class);

    public final EnumPath<market.demo.domain.status.GenderStatus> gender = createEnum("gender", market.demo.domain.status.GenderStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSearchKeyword searchKeyword;

    public QAgeGenderFrequency(String variable) {
        this(AgeGenderFrequency.class, forVariable(variable), INITS);
    }

    public QAgeGenderFrequency(Path<? extends AgeGenderFrequency> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAgeGenderFrequency(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAgeGenderFrequency(PathMetadata metadata, PathInits inits) {
        this(AgeGenderFrequency.class, metadata, inits);
    }

    public QAgeGenderFrequency(Class<? extends AgeGenderFrequency> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.searchKeyword = inits.isInitialized("searchKeyword") ? new QSearchKeyword(forProperty("searchKeyword")) : null;
    }

}

