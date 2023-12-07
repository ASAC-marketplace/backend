package market.demo.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.etc.Delivery;
import market.demo.domain.member.Member;
import market.demo.domain.status.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private LocalDateTime orderDateTime;

    @OneToOne(mappedBy = "order", cascade = ALL, fetch = LAZY)
    private Payment payment;

    private OrderStatus orderStatus;

    @ManyToOne(fetch = LAZY)
    private Member member;

    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = ALL, fetch = LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private Long totalAmount;

    // 연관관계 편의 메서드

    // 주문 상품
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        if (orderItem.getOrder() != this) {
            orderItem.setOrder(this);
        }
    }
}
