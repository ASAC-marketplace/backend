package market.demo.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import market.demo.domain.item.Item;
import market.demo.domain.item.QItem;
import market.demo.domain.item.QItemDetail;
import market.demo.dto.item.ItemDto;
import market.demo.dto.item.QItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {

    private final JPAQueryFactory queryFactory;

    public ItemService(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    //배너?
    public List<ItemDto> getRecentProducts(int page, int size) {
        QItem item = QItem.item;
        QItemDetail itemDetail = QItemDetail.itemDetail;

        List<ItemDto> results = queryFactory
                .select(new QItemDto(
                        item.id,
                        item.registerdDate,
                        itemDetail.promotionImageUrl
                ))
                .from(item)
                .leftJoin(item.itemDetail, itemDetail)
                .orderBy(item.registerdDate.desc())
                .offset((page - 1) * size)
                .limit(size)
                .fetch();

        return results;
    }
}
