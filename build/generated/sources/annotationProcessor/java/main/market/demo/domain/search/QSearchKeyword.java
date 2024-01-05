package market.demo.domain.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSearchKeyword is a Querydsl query type for SearchKeyword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSearchKeyword extends EntityPathBase<SearchKeyword> {

    private static final long serialVersionUID = 1226158604L;

    public static final QSearchKeyword searchKeyword = new QSearchKeyword("searchKeyword");

    public final ListPath<AgeGenderFrequency, QAgeGenderFrequency> ageGenderFrequencies = this.<AgeGenderFrequency, QAgeGenderFrequency>createList("ageGenderFrequencies", AgeGenderFrequency.class, QAgeGenderFrequency.class, PathInits.DIRECT2);

    public final NumberPath<Integer> frequency = createNumber("frequency", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath keyword = createString("keyword");

    public QSearchKeyword(String variable) {
        super(SearchKeyword.class, forVariable(variable));
    }

    public QSearchKeyword(Path<? extends SearchKeyword> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSearchKeyword(PathMetadata metadata) {
        super(SearchKeyword.class, metadata);
    }

}

