package market.demo.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private LocalDateTime orderDateTime;

    @OneToOne(mappedBy = "order", cascade = ALL, fetch = LAZY)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = LAZY)
    private Member member;

    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = ALL, fetch = LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    //총 가격
    private Long totalAmount;

    public void markAsPaid() {
        this.orderStatus = OrderStatus.PAID;
    }
    // 연관관계 편의 메서드
// 생성자 추가
    public Order(Member member, LocalDateTime orderDateTime, OrderStatus orderStatus) {
        this.member = member;
        this.orderDateTime = orderDateTime;
        this.orderStatus = orderStatus;
        this.totalAmount = 0L; // 초기 가격 0으로 설정
    }

    // 주문 항목 추가 및 총 가격 업데이트
    public void addOrderItem(OrderItem orderItem) {
        if (!this.orderItems.contains(orderItem)) {
            this.orderItems.add(orderItem);
            orderItem.setOrder(this);
            this.totalAmount +=  orderItem.getOrderPrice() * orderItem.getOrderCount();
        }
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        if (delivery.getOrder() != this) {
            delivery.setOrder(this);
        }
    }

    // 총 가격 계산 메서드
    public void calculateTotalAmount() {
        this.totalAmount = this.orderItems.stream()
                .mapToLong(OrderItem::getOrderPrice)
                .sum();
    }
}
