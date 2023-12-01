package market.demo.service;

import com.querydsl.jpa.impl.JPAQueryFactory;


import lombok.RequiredArgsConstructor;
import market.demo.domain.item.QItem;
import market.demo.domain.item.QItemDetail;

import market.demo.domain.item.QReview;
import market.demo.domain.type.PromotionType;
import market.demo.dto.item.ItemDto;
import market.demo.dto.item.ItemMainEndDto;
import market.demo.dto.item.QItemDto;
import market.demo.dto.item.QItemMainEndDto;
import market.demo.repository.ItemReposistory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final JPAQueryFactory queryFactory;

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

    //메인 마감세일, 주말특가
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
    //

    //주말특가 스케줄
    public void updatePromotionTypeForItems(List<Long> itemIds, PromotionType promotionType) {
        QItem qItem = QItem.item;

        queryFactory
                .update(qItem)
                .set(qItem.promotionType, promotionType)
                .where(qItem.id.in(itemIds))
                .execute();
    }

    public void resetPromotionType(PromotionType promotionType) {
        QItem qItem = QItem.item;

        queryFactory
                .update(qItem)
                .set(qItem.promotionType, PromotionType.NONE)
                .where(qItem.promotionType.eq(promotionType))
                .execute();
    }

    public List<Long> findRandomItemIds(int limit) {
        QItem qItem = QItem.item;
        return queryFactory
                .select(qItem.id)
                .from(qItem)
                .orderBy(qItem.id.asc())
                .limit(limit)
                .fetch();
    }
    //
}
