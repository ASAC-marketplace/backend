package market.demo.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import market.demo.domain.item.Item;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderCount;

    private int orderPrice; //전체 가격

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 연관관계 편의 메서드

    // 생성자 추가
    public OrderItem(Item item, int orderCount, int orderPrice) {
        this.item = item;
        this.orderCount = orderCount;
        this.orderPrice = orderPrice;
    }

    public OrderItem() {
        
    }

    // 연관관계 편의 메서드 수정
    public void setOrder(Order order) {
        if (this.order != order) {
            this.order = order;
            order.addOrderItem(this);
        }
    }
}
