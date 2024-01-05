package market.demo.domain.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSearchHistory is a Querydsl query type for SearchHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSearchHistory extends EntityPathBase<SearchHistory> {

    private static final long serialVersionUID = -1327466313L;

    public static final QSearchHistory searchHistory = new QSearchHistory("searchHistory");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath keyword = createString("keyword");

    public final DateTimePath<java.time.LocalDateTime> searchedAt = createDateTime("searchedAt", java.time.LocalDateTime.class);

    public QSearchHistory(String variable) {
        super(SearchHistory.class, forVariable(variable));
    }

    public QSearchHistory(Path<? extends SearchHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSearchHistory(PathMetadata metadata) {
        super(SearchHistory.class, metadata);
    }

}

