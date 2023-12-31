package market.demo.repository;

import market.demo.domain.item.Item;
import market.demo.domain.member.Member;
import market.demo.domain.order.Order;
import market.demo.domain.order.OrderItem;
import market.demo.domain.status.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.order.member.id = :memberId AND oi.item.id = :itemId")
    boolean existsByMemberIdAndItemId(@Param("memberId") Long memberId, @Param("itemId") Long itemId);
}
