package market.demo.repository;

import market.demo.domain.member.Member;
import market.demo.domain.order.Order;
import market.demo.domain.status.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMemberAndOrderStatus(Member member, OrderStatus orderStatus);

    Optional<List<Order>> findAllByMemberAndOrderStatus(Member member, OrderStatus orderStatus );
}
