package market.demo.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.item.Item;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    private int quantity;
    private Long totalPrice;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
}
