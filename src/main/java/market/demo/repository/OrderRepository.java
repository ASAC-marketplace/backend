package market.demo.repository;

import market.demo.domain.member.Member;
import market.demo.domain.order.Order;
import market.demo.domain.status.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import market.demo.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMemberAndOrderStatus(Member member, OrderStatus orderStatus);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.item WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

    Optional<List<Order>> findAllByMemberAndOrderStatusAndOrderDateTimeAfter(Member member, OrderStatus orderStatus, LocalDateTime orderDateTime);

}
