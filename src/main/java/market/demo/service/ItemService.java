package market.demo.service;

import com.querydsl.jpa.impl.JPAQueryFactory;


import market.demo.domain.item.QItem;
import market.demo.domain.item.QItemDetail;

import market.demo.domain.item.QReview;
import market.demo.domain.type.PromotionType;
import market.demo.dto.item.ItemDto;
import market.demo.dto.item.ItemMainEndDto;
import market.demo.dto.item.QItemDto;
import market.demo.dto.item.QItemMainEndDto;
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

        return queryFactory
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
    }

    //메인 마감세일
    public List<ItemMainEndDto> getItemByPromotionType(PromotionType promotionType, int page, int size) {
        QItem item = QItem.item;
        QItemDetail itemDetail = QItemDetail.itemDetail;
        QReview review = QReview.review;

        return queryFactory
                .select(new QItemMainEndDto(
                        item.id,
                        item.name,
                        item.discountRate,
                        item.price.subtract(item.price.multiply(item.discountRate).divide(100)),
                        item.price,
                        itemDetail.promotionImageUrl,
                        review.count()
                ))
                .from(item)
                .leftJoin(item.itemDetail, itemDetail)
                .leftJoin(item.reviews, review)
                .where(item.promotionType.eq(promotionType))
                .groupBy(item.id, item.name, item.discountRate, item.price, itemDetail.promotionImageUrl)
                .orderBy(item.registerdDate.desc())
                .offset((page - 1) * size)
                .limit(size)
                .fetch();
    }
}
